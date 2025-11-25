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
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs
            .authorizeHttpRequests(auth -> auth
                // 1. Libera endpoints de Autenticação e Documentação
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // 2. [CORREÇÃO] Libera TODOS os arquivos estáticos (HTMLs, imagens, pastas)
                // Isso permite carregar as páginas de login, cadastro, etc. sem estar logado
                .requestMatchers(
                    "/", 
                    "/index.html",
                    "/cadastro_tutor.html",
                    "/cadastro_hospital.html",
                    "/recuperar_senha.html",
                    "/resetar_senha.html",
                    "/pages/**",       // Libera tudo dentro da pasta pages (ex: login)
                    "/assets/**",      // Caso tenha css/js externos
                    "/dashboard_tutor.html",   // O HTML é público, os dados da API não
                    "/dashboard_hospital.html",
                    "/emergencia.html"
                ).permitAll()

                // 3. Qualquer outra requisição (ex: /api/tutor/me) exige token válido
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // Adiciona o filtro de JWT antes da autenticação padrão
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