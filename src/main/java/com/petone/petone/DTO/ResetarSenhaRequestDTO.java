package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para o formulário "Resetar minha senha".
 * Recebe o token (da URL/email) e a nova senha.
 */
@Data
public class ResetarSenhaRequestDTO {

    @NotBlank(message = "O token é obrigatório.")
    private String token;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String novaSenha;
}