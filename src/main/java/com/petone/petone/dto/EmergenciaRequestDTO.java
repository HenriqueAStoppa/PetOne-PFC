package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmergenciaRequestDTO {
    @NotBlank(message = "O ID do animal é obrigatório.")
    private String idAnimal;

    @NotBlank(message = "O tipo de emergência é obrigatório.")
    private String tipoEmergencia;

    @NotNull(message = "Latitude do tutor é obrigatória.")
    private Double latitudeTutor;

    @NotNull(message = "Longitude do tutor é obrigatória.")
    private Double longitudeTutor;

}