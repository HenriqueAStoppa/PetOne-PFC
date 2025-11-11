package com.petone.petone.dto;

import com.petone.petone.model.Hospital;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para enviar a resposta ao Tutor após iniciar uma emergência.
 */
@Data
@Builder
public class EmergenciaResponseDTO {
    private String tokenEmergencia;
    private Hospital hospitalEncontrado;
    private String mensagem; // Ex: "Emergência registrada. Siga para..."
}