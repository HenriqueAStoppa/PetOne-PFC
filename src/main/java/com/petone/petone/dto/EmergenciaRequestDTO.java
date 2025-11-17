package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para receber os dados do formulário de emergência do frontend.
 */
@Data
public class EmergenciaRequestDTO {
    @NotBlank(message = "O ID do animal é obrigatório.")
    private String idAnimal;

    @NotBlank(message = "O tipo de emergência é obrigatório.")
    private String tipoEmergencia; // Ex: "Convulsão", "Hemorragia", etc.

    // (Opcional) Coordenadas do tutor, se for usar o Maps real
    // private double latitude;
    // private double longitude;
}