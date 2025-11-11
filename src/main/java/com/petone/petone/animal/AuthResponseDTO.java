package com.petone.petone.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para enviar a resposta de Login (Tutor e Hospital).
 */
@Data
@Builder // Facilita a criação do objeto
public class AuthResponseDTO {
    private String token;
    private String idTutor; // Ou idHospital, reutilizando o campo
    private String email;
    private String nomeCompleto; // Ou nomeFantasia, reutilizando o campo
}