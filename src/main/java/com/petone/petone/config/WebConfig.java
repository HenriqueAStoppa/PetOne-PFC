package com.petone.petone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Mapeia a raiz para a Home
        registry.addViewController("/").setViewName("forward:/pages/Home/index.html");
        registry.addViewController("/index.html").setViewName("forward:/pages/Home/index.html");

        // Mapeia URLs curtas para os arquivos organizados
        
        // Login e Auth
        registry.addViewController("/login").setViewName("forward:/pages/login/index.html");
        registry.addViewController("/recuperar_senha").setViewName("forward:/pages/RecuperarSenha/recuperar_senha.html");
        registry.addViewController("/resetar_senha").setViewName("forward:/pages/ResetarSenha/resetar_senha.html");

        // Cadastros
        registry.addViewController("/cadastro_tutor").setViewName("forward:/pages/Cadastro/Tutor/cadastro_tutor.html");
        registry.addViewController("/cadastro_hospital").setViewName("forward:/pages/Cadastro/Hospital/cadastro_hospital.html");

        // Dashboards e Funcionalidades
        registry.addViewController("/dashboard_tutor").setViewName("forward:/pages/Dashboard/Tutor/dashboard_tutor.html");
        registry.addViewController("/dashboard_hospital").setViewName("forward:/pages/Dashboard/Hospital/dashboard_hospital.html");
        registry.addViewController("/emergencia").setViewName("forward:/pages/Emergencia/emergencia.html");
    }
}