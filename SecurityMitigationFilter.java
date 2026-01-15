package com.code.aplusbinary.warehouse.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Security filter to mitigate CVE-2025-41234 (Spring Framework RFD vulnerability)
 * and other potential security issues
 */
@Component
@Slf4j
public class SecurityMitigationFilter extends OncePerRequestFilter {

    private static final List<String> DANGEROUS_EXTENSIONS = Arrays.asList(
        ".bat", ".cmd", ".exe", ".scr", ".com", ".pif", ".vbs", ".js", ".jar"
    );
    private static final List<String> WHITELISTED_PATH_PREFIXES = Arrays.asList(
        "/swagger-ui/",
        "/swagger-ui.html",
        "/swagger-resources",
        "/webjars/",
        "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        log.info("SecurityMitigationFilter - Processing request: {} {}", method, requestUri);
        
        // CRITICAL: Mitigate CVE-2025-24813 - Block ALL PUT requests if not explicitly needed
        if ("PUT".equalsIgnoreCase(request.getMethod())) {
            // Only allow PUT for specific endpoints that actually need it
            String lowerRequestUri = request.getRequestURI().toLowerCase();
            if (!isAllowedPutEndpoint(lowerRequestUri)) {
                log.warn("SecurityMitigationFilter - BLOCKED PUT request to potentially vulnerable endpoint: {} from {}", 
                        lowerRequestUri, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                response.setHeader("Allow", "GET, POST, DELETE");
                response.getWriter().write("PUT method not allowed for security reasons");
                return;
            }
            
            // Additional PUT request validation for allowed endpoints
            if (!validatePutRequest(request)) {
                log.warn("SecurityMitigationFilter - BLOCKED malicious PUT request: {} from {}", 
                        lowerRequestUri, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid PUT request");
                return;
            }
            log.debug("SecurityMitigationFilter - PUT request validated for: {}", lowerRequestUri);
        }
        
        // Mitigate CVE-2025-41234: Reflected File Download (RFD)
        String lowerRequestUri = request.getRequestURI().toLowerCase();

        // Allowlisted static Swagger/OpenAPI assets to avoid false positives on .js checks
        if (isWhitelistedPath(lowerRequestUri)) {
            addSecurityHeaders(response);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check for dangerous file extensions in URL
        if (containsDangerousExtension(lowerRequestUri)) {
            log.warn("SecurityMitigationFilter - BLOCKED request with dangerous file extension: {}", lowerRequestUri);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Potentially dangerous file request");
            return;
        }
        
        // Mitigate potential reflected file download attacks
        if (isPotentialRFDAttack(request)) {
            log.warn("SecurityMitigationFilter - BLOCKED potential RFD attack from: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Bad Request: Invalid request parameters");
            return;
        }
        
        // Add security headers to mitigate various vulnerabilities
        addSecurityHeaders(response);
        
        // Mitigate CVE-2025-48988: DoS in multipart upload - ENHANCED PROTECTION
        if (request.getContentType() != null && 
            request.getContentType().toLowerCase().startsWith("multipart/")) {
            
            // Allow file upload endpoints to have larger size limits
            if (isFileUploadEndpoint(lowerRequestUri)) {
                long contentLength = request.getContentLengthLong();
                if (contentLength > 50 * 1024 * 1024) { // 50MB limit for file uploads
                    log.warn("SecurityMitigationFilter - BLOCKED oversized file upload: {} bytes from {}", 
                            contentLength, request.getRemoteAddr());
                    response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                    response.getWriter().write("Request entity too large - maximum 50MB allowed");
                    return;
                }
                log.debug("SecurityMitigationFilter - Allowing file upload to: {}", lowerRequestUri);
            } else {
                // Strict limit for non-file-upload multipart requests
                long contentLength = request.getContentLengthLong();
                if (contentLength > 1 * 1024 * 1024) { // 1MB strict limit
                    log.warn("SecurityMitigationFilter - BLOCKED oversized multipart request: {} bytes from {}", 
                            contentLength, request.getRemoteAddr());
                    response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                    response.getWriter().write("Request entity too large - maximum 1MB allowed");
                    return;
                }
                
                // Additional multipart validation for non-file-upload endpoints
                if (!isValidMultipartRequest(request)) {
                    log.warn("SecurityMitigationFilter - BLOCKED invalid multipart request from {}", request.getRemoteAddr());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Invalid multipart request format");
                    return;
                }
            }
        }
        
        // CVE-2025-22233: Enhanced DataBinder protection
        if (hasDataBinderVulnerability(request)) {
            log.warn("SecurityMitigationFilter - BLOCKED DataBinder vulnerability attempt from {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid request parameters detected");
            return;
        }
        
        log.debug("SecurityMitigationFilter - Request passed all security checks, proceeding to next filter: {} {}", method, requestUri);
        filterChain.doFilter(request, response);
    }
    
    /**
     * Check if endpoint is for file uploads
     */
    private boolean isFileUploadEndpoint(String uri) {
        String[] fileUploadEndpoints = {
            "/product/upload-file",
            "/product/batch-upload"
        };
        
        for (String endpoint : fileUploadEndpoints) {
            if (uri.equals(endpoint)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * CRITICAL: CVE-2025-24813 mitigation - Only allow PUT for specific safe endpoints
     */
    private boolean isAllowedPutEndpoint(String uri) {
        // Whitelist of endpoints that actually need PUT method
        String[] allowedPutEndpoints = {
            "/product/update",
            "/category/update", 
            "/warehouse/update",
            "/unit/update",
            "/supplier/update",
            "/stock/update",
            "/stockbatch/update",
            "/shipment/update",
            "/storagelocation/update"
        };
        
        for (String allowedEndpoint : allowedPutEndpoints) {
            if (uri.equals(allowedEndpoint)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Additional validation for PUT requests to prevent RCE
     */
    private boolean validatePutRequest(HttpServletRequest request) {
        // Check Content-Type header for suspicious values
        String contentType = request.getContentType();
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            
            // Block potentially dangerous content types
            if (lowerContentType.contains("application/x-") ||
                lowerContentType.contains("text/x-") ||
                lowerContentType.contains("multipart/x-") ||
                lowerContentType.contains("image/svg+xml")) {
                return false;
            }
        }
        
        // Check for suspicious headers that could exploit PUT vulnerability
        String[] suspiciousHeaders = {
            "x-http-method-override", 
            "x-method-override",
            "x-forwarded-method"
        };
        
        for (String header : suspiciousHeaders) {
            if (request.getHeader(header) != null) {
                log.warn("Blocked PUT request with suspicious header: {}", header);
                return false;
            }
        }
        
        // Limit PUT request size to prevent buffer overflow attacks
        long contentLength = request.getContentLengthLong();
        if (contentLength > 1024 * 1024) { // 1MB limit for PUT requests
            log.warn("Blocked oversized PUT request: {} bytes", contentLength);
            return false;
        }
        
        return true;
    }
    
    private boolean containsDangerousExtension(String uri) {
        return DANGEROUS_EXTENSIONS.stream().anyMatch(uri::endsWith);
    }
    
    private boolean isPotentialRFDAttack(HttpServletRequest request) {
        // Check for suspicious parameters that could lead to RFD
        String[] suspiciousParams = {"filename", "file", "download", "attachment"};
        
        for (String param : suspiciousParams) {
            String value = request.getParameter(param);
            if (value != null) {
                String lowerValue = value.toLowerCase();
                if (containsDangerousExtension(lowerValue) || 
                    lowerValue.contains("..") || 
                    lowerValue.contains("/") || 
                    lowerValue.contains("\\")) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void addSecurityHeaders(HttpServletResponse response) {
        // Prevent clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Strict transport security (if HTTPS is used)
        response.setHeader("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains; preload");
        
        // Content Security Policy
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'");
        
        // Referrer policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Feature policy
        response.setHeader("Permissions-Policy", 
            "camera=(), microphone=(), geolocation=(), payment=()");
            
        // CVE-2025-41234: Additional RFD Protection Headers
        response.setHeader("X-Download-Options", "noopen");
        response.setHeader("Content-Disposition", "inline");
    }
    
    /**
     * CVE-2025-48988: Enhanced multipart request validation
     */
    private boolean isValidMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) return false;
        
        // Validate multipart boundary exists
        if (!contentType.contains("boundary=")) {
            return false;
        }
        
        // Extract boundary value for validation
        String boundaryPart = contentType.substring(contentType.indexOf("boundary="));
        
        // Check for suspicious patterns in boundary value (not in "multipart/form-data" part)
        if (boundaryPart.contains("..") || 
            boundaryPart.contains("\\") ||
            boundaryPart.contains("<") ||
            boundaryPart.contains(">")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * CVE-2025-22233: Enhanced DataBinder vulnerability detection
     */
    private boolean hasDataBinderVulnerability(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            
            // Check for case sensitivity exploitation attempts
            if (paramName != null) {
                String lowerParamName = paramName.toLowerCase();
                
                // Detect property binding attacks
                if (lowerParamName.contains("class.") ||
                    lowerParamName.contains("constructor.") ||
                    lowerParamName.contains("declaringclass.") ||
                    lowerParamName.contains("protectiondomain.")) {
                    return true;
                }
                
                // Check for nested property binding attempts
                if (paramName.matches(".*\\[.*class.*\\].*") ||
                    paramName.matches(".*\\[.*constructor.*\\].*")) {
                    return true;
                }
            }
            
            // Check parameter values for injection attempts
            if (paramValues != null) {
                for (String value : paramValues) {
                    if (value != null && (
                        value.contains("java.lang.") ||
                        value.contains("java.util.") ||
                        value.contains("java.io.") ||
                        value.toLowerCase().contains("runtime") ||
                        value.toLowerCase().contains("process"))) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    private boolean isWhitelistedPath(String uri) {
        return WHITELISTED_PATH_PREFIXES.stream().anyMatch(uri::startsWith);
    }
}
