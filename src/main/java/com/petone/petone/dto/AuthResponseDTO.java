package com.petone.petone.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private String idTutor;
    private String email;
    private String nomeCompleto;
}