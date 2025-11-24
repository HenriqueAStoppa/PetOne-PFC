package com.petone.petone.dto;


import com.petone.petone.model.Hospital;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class EmergenciaResponseDTO {
    private String tokenEmergencia;
    private Hospital hospitalEncontrado;
    private String mensagem;
    private String hospitalNome;
    private String hospitalEndereco;
    private Data dataHoraRegistro;
}