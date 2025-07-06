package com.company.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // Allow access to Camunda webapp
                        .requestMatchers(new AntPathRequestMatcher("/app/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/engine/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/lib/**")).permitAll()
                        
                        // Allow access to H2 Console
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        
                        // Allow access to workflow API
                        .requestMatchers(new AntPathRequestMatcher("/api/workflow/**")).permitAll()
                        
                        // Allow access to actuator endpoints
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                        
                        // Allow access to static resources
                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                        
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions().sameOrigin() // Allow H2 Console to be embedded in frames
                );
        
        return http.build();
    }
}