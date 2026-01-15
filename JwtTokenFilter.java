package com.code.aplusbinary.warehouse.security;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.code.aplusbinary.warehouse.dto.am.SessionErrorResponse;
import com.code.aplusbinary.warehouse.dto.am.SessionValidationResult;
import com.code.aplusbinary.warehouse.enums.am.SessionErrorCode;
import com.code.aplusbinary.warehouse.service.am.DeviceSessionService;
import com.code.aplusbinary.warehouse.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    private final DeviceSessionService deviceSessionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtTokenFilter(DeviceSessionService deviceSessionService) {
        this.deviceSessionService = deviceSessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        logger.info("JwtTokenFilter - Processing request: {} {}", method, requestUri);
        
        String token = request.getHeader("Authorization");

        if (token == null) {
            logger.warn("JwtTokenFilter - No Authorization header found for request: {} {} - Request will proceed UNAUTHENTICATED", method, requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        if (!token.startsWith("Bearer ")) {
            logger.warn("JwtTokenFilter - Authorization header does not start with 'Bearer ' for request: {} {} - Received: {}", 
                    method, requestUri, token.substring(0, Math.min(20, token.length())));
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7); // Remove "Bearer " prefix
        logger.debug("JwtTokenFilter - Bearer token extracted, length: {}", token.length());

        try {
            logger.debug("JwtTokenFilter - Parsing JWT token");
            Claims claims = JwtUtil.parseToken(token); // Decode the token
            logger.debug("JwtTokenFilter - JWT token parsed successfully");
            
            String username = claims.getSubject();
            logger.info("JwtTokenFilter - Token parsed for user: {}", username);
            
            String roles = claims.get("roles", String.class); // Extract roles
            Long adminId = claims.get("adminId", Long.class);
            Long memberId = claims.get("memberId", Long.class); // Extract memberId
            Long roleId = claims.get("roleId", Long.class); // Extract roleId
            String mainRole = claims.get("mainRole", String.class);
            String deviceSessionToken = claims.get("deviceSessionToken", String.class); // Extract device session token
            
            logger.debug("JwtTokenFilter - Extracted claims - username: {}, roles: {}, adminId: {}, memberId: {}, roleId: {}, mainRole: {}", 
                    username, roles, adminId, memberId, roleId, mainRole);
            
            // Validate device session if deviceSessionToken is present
            if (deviceSessionToken != null && !deviceSessionToken.trim().isEmpty()) {
                logger.info("JwtTokenFilter - Device session token found, validating for user: {}", username);
                SessionValidationResult validationResult = deviceSessionService.validateSession(deviceSessionToken);
                
                if (!validationResult.isValid()) {
                    logger.warn("JwtTokenFilter - AUTHENTICATION FAILED - Device session validation failed for user: {} - Error: {} - {}", 
                            username, validationResult.getErrorCode(), validationResult.getErrorMessage());
                    
                    // Create detailed error response
                    SessionErrorCode errorCode = SessionErrorCode.valueOf(validationResult.getErrorCode());
                    SessionErrorResponse errorResponse = new SessionErrorResponse(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        errorCode.getHint()
                    );
                    
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    return;
                }
                logger.info("JwtTokenFilter - Device session validated successfully for user: {}", username);
            } else {
                logger.debug("JwtTokenFilter - No device session token found in JWT claims for user: {}", username);
            }
            
            logger.debug("JwtTokenFilter - Creating authorities from roles: {}", roles);
            List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            logger.debug("JwtTokenFilter - Authorities created: {}", authorities);

            // Use CustomUserDetails instead of just username
            logger.debug("JwtTokenFilter - Creating CustomUserDetails for user: {}", username);
            CustomUserDetails customUserDetails = new CustomUserDetails(username, null, adminId, memberId, roleId, mainRole, authorities);

            logger.debug("JwtTokenFilter - Creating authentication token");
            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
            
            logger.debug("JwtTokenFilter - Setting authentication in SecurityContextHolder");
            SecurityContextHolder.getContext().setAuthentication(authentication); // Set authentication in the context
            
            logger.info("JwtTokenFilter - AUTHENTICATION SUCCESS - User: {}, Roles: {}, Request: {} {}", 
                    username, roles, method, requestUri);
                    
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("JwtTokenFilter - AUTHENTICATION FAILED - JWT token expired for request: {} {}", method, requestUri, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"JWT token has expired\"}");
            response.setContentType("application/json");
            return;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("JwtTokenFilter - AUTHENTICATION FAILED - Malformed JWT token for request: {} {}", method, requestUri, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Malformed JWT token\"}");
            response.setContentType("application/json");
            return;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("JwtTokenFilter - AUTHENTICATION FAILED - Invalid JWT signature for request: {} {}", method, requestUri, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid JWT signature\"}");
            response.setContentType("application/json");
            return;
        } catch (Exception e) {
            logger.error("JwtTokenFilter - AUTHENTICATION FAILED - Unexpected error parsing JWT token for request: {} {}", 
                    method, requestUri, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
            response.setContentType("application/json");
            return;
        }

        logger.debug("JwtTokenFilter - Proceeding to next filter in chain");
        filterChain.doFilter(request, response);
    }
}

