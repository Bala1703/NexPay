package com.nexpay.ai_cicd_analyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public ApiKeyAuthFilter apiKeyAuthFilter(
            @Value("${nexpay.security.api-key:}") String configuredApiKey) {
        return new ApiKeyAuthFilter(configuredApiKey);
    }

    // Prevent Spring Boot from registering the API-key filter twice.
    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyFilterRegistration(
            ApiKeyAuthFilter apiKeyAuthFilter) {

        FilterRegistrationBean<ApiKeyAuthFilter> registration =
                new FilterRegistrationBean<>(apiKeyAuthFilter);

        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ApiKeyAuthFilter apiKeyAuthFilter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/analyze"
                ).authenticated()
                .anyRequest().authenticated())
            .addFilterBefore(
                apiKeyAuthFilter,
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    response.sendError(401, "Unauthorized")));

        return http.build();
    }
}
