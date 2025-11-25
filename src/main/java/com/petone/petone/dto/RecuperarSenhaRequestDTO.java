package com.petone.petone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//DTO para o formulário "Esqueci minha senha". Recebe apenas o email do usuário.
@Data
public class RecuperarSenhaRequestDTO {
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email inválido.")
    private String email;
}