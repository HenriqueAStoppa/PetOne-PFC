package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergenciaRequestDTO {
    @NotBlank(message = "O ID do animal é obrigatório.")
    private String idAnimal;

    @NotBlank(message = "O tipo de emergência é obrigatório.")
    private String tipoEmergencia;

}