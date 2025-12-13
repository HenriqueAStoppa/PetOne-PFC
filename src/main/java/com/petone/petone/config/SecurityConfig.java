package com.petone.petone.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtRequestFilter jwtRequestFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/favicon.ico",
                                                                "/assets/**",
                                                                "/pages/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/login",
                                                                "/cadastro_tutor",
                                                                "/cadastro_hospital",
                                                                "/recuperar_senha",
                                                                "/resetar_senha")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/dashboard_tutor",
                                                                "/dashboard_hospital",
                                                                "/emergencia")
                                                .permitAll()

                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}