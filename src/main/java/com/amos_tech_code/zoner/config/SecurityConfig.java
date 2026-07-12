package com.amos_tech_code.zoner.config;

import com.amos_tech_code.zoner.security.JwtAccessDeniedHandler;
import com.amos_tech_code.zoner.security.JwtAuthenticationEntryPoint;
import com.amos_tech_code.zoner.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Auth endpoints requiring authentication
                        .requestMatchers(
                                "/api/v1/auth/logout",
                                "/api/v1/auth/logout-all",
                                "/api/v1/auth/sessions/**",
                                "/api/v1/auth/change-password"
                        )
                        .authenticated()

                        // All other authentication endpoints are permitted
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/actuator/health"
                        )
                        .permitAll()

                        // Public Business Endpoints
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/business/categories",
                                "/api/v1/business/*"
                        )
                        .permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}