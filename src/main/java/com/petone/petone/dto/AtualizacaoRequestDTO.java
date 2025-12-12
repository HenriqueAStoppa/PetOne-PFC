package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizacaoRequestDTO {

    @NotBlank(message = "Descreva o procedimento ou estado atual.")
    private String relatorio;

    @NotBlank(message = "Veterinário responsável é obrigatório.")
    private String veterinarioResponsavel;

    @NotBlank(message = "CRMV é obrigatório.")
    private String crmvVeterinario;
}