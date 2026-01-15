package com.code.aplusbinary.warehouse.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.code.aplusbinary.warehouse.config.ActuatorSecurityConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final SecurityMitigationFilter securityMitigationFilter;
    private static final String[] SWAGGER_WHITELIST = {
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-resources/**",
        "/webjars/**"
    };
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT authentication
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Handles missing/invalid token (401 Unauthorized)
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized: " + authException.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Handles insufficient roles (403 Forbidden)
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Forbidden: Access is denied\"}");
                        })
                )
                .authorizeHttpRequests(auth -> {
                        // Apply actuator security configuration to mitigate CVE-2025-22235
                        ActuatorSecurityConfig.configureActuatorSecurity(auth);
                        
                        auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow CORS preflight
                        .requestMatchers(SWAGGER_WHITELIST).permitAll() // Swagger / OpenAPI docs
                        .requestMatchers("/internal/**", "/public/**").permitAll() // Public endpoints
                        
                        // CRITICAL: CVE-2025-24813 mitigation - Block PUT to vulnerable endpoints
                        //.requestMatchers(HttpMethod.PUT, "/**").denyAll() // Block ALL PUT requests by default
                        
                        // Category endpoints with action-based permissions
                        .requestMatchers(HttpMethod.GET, "/category").hasRole("VIEW_CATEGORY")
                        .requestMatchers(HttpMethod.POST, "/category/create").hasRole("CREATE_CATEGORY")
                        .requestMatchers(HttpMethod.PUT, "/category/update").hasRole("UPDATE_CATEGORY")
                        .requestMatchers(HttpMethod.DELETE, "/category/delete").hasRole("DELETE_CATEGORY")
                        
                        // Product endpoints with action-based permissions
                        .requestMatchers(HttpMethod.GET, "/product").hasRole("VIEW_PRODUCT")
                        .requestMatchers(HttpMethod.GET, "/product/search").hasRole("VIEW_PRODUCT")
                        .requestMatchers(HttpMethod.POST, "/product/create").hasRole("CREATE_PRODUCT")
                        .requestMatchers(HttpMethod.POST, "/product/batch-upload").hasRole("CREATE_PRODUCT")
                        .requestMatchers(HttpMethod.PUT, "/product/update").hasRole("UPDATE_PRODUCT")
                        .requestMatchers(HttpMethod.DELETE, "/product/delete").hasRole("DELETE_PRODUCT")
                        
                        // Warehouse endpoints with action-based permissions
                        .requestMatchers(HttpMethod.POST, "/warehouse/search").hasRole("VIEW_WAREHOUSE")
                        .requestMatchers(HttpMethod.POST, "/warehouse/create").hasRole("CREATE_WAREHOUSE")
                        .requestMatchers(HttpMethod.PUT, "/warehouse/update").hasRole("UPDATE_WAREHOUSE")
                        .requestMatchers(HttpMethod.DELETE, "/warehouse/delete").hasRole("DELETE_WAREHOUSE")
                        
                        // Stock endpoints with action-based permissions
                        .requestMatchers(HttpMethod.GET, "/stock").hasRole("VIEW_STOCK")
                        .requestMatchers(HttpMethod.GET, "/stock/filter").hasRole("VIEW_STOCK")
                        .requestMatchers(HttpMethod.POST, "/stock/create").hasRole("CREATE_STOCK")
                        .requestMatchers(HttpMethod.PUT, "/stock/update").hasRole("UPDATE_STOCK")
                        .requestMatchers(HttpMethod.DELETE, "/stock/delete").hasRole("DELETE_STOCK")
                        
                        // StockBatch endpoints with action-based permissions
                        .requestMatchers(HttpMethod.GET, "/stockBatch").hasRole("VIEW_STOCK")
                        .requestMatchers(HttpMethod.POST, "/stockBatch/create").hasRole("CREATE_STOCK")
                        .requestMatchers(HttpMethod.PUT, "/stockBatch/update").hasRole("UPDATE_STOCK")
                        .requestMatchers(HttpMethod.DELETE, "/stockBatch/delete").hasRole("DELETE_STOCK")
                        
                        // Shipment endpoints with action-based permissions
                        .requestMatchers(HttpMethod.POST, "/shipment/search").hasRole("VIEW_SHIPMENT")
                        .requestMatchers(HttpMethod.POST, "/shipment/create").hasRole("CREATE_SHIPMENT")
                        .requestMatchers(HttpMethod.PUT, "/shipment/update").hasRole("UPDATE_SHIPMENT")
                        .requestMatchers(HttpMethod.DELETE, "/shipment/delete").hasRole("DELETE_SHIPMENT")
                        
                        // Supplier endpoints with action-based permissions
                        .requestMatchers(HttpMethod.POST, "/supplier/search").hasRole("VIEW_SUPPLIER")
                        .requestMatchers(HttpMethod.POST, "/supplier/create").hasRole("CREATE_SUPPLIER")
                        .requestMatchers(HttpMethod.PUT, "/supplier/update").hasRole("UPDATE_SUPPLIER")
                        .requestMatchers(HttpMethod.DELETE, "/supplier/delete").hasRole("DELETE_SUPPLIER")
                        
                        // Unit endpoints with action-based permissions
                        .requestMatchers(HttpMethod.GET, "/unit/search").hasRole("VIEW_UNIT")
                        .requestMatchers(HttpMethod.POST, "/unit/create").hasRole("CREATE_UNIT")
                        .requestMatchers(HttpMethod.PUT, "/unit/update").hasRole("UPDATE_UNIT")
                        .requestMatchers(HttpMethod.DELETE, "/unit/delete").hasRole("DELETE_UNIT")
                        
                        // Storage Location endpoints with action-based permissions
                        .requestMatchers(HttpMethod.POST, "/storageLocation/search").hasRole("VIEW_STORAGE")
                        .requestMatchers(HttpMethod.POST, "/storageLocation/create").hasRole("CREATE_STORAGE")
                        .requestMatchers(HttpMethod.PUT, "/storageLocation/update").hasRole("UPDATE_STORAGE")
                        .requestMatchers(HttpMethod.DELETE, "/storageLocation/delete").hasRole("DELETE_STORAGE")
                        
                        .anyRequest().authenticated(); // Secure all other endpoints
                })
                // Additional security headers to mitigate CVE vulnerabilities
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.deny())
                    .contentTypeOptions(Customizer.withDefaults())
                    .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                        .maxAgeInSeconds(31536000)
                        .includeSubDomains(true)
                        .preload(true)
                    )
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                )
                // Add SecurityMitigationFilter FIRST (before JWT filter)
                .addFilterBefore(securityMitigationFilter, UsernamePasswordAuthenticationFilter.class)
                // Add the JWT Token Filter after SecurityMitigationFilter
                .addFilterAfter(jwtTokenFilter, SecurityMitigationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
