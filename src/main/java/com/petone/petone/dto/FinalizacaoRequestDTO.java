package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FinalizacaoRequestDTO {

    @NotBlank(message = "O relatório médico é obrigatório.")
    private String relatorio;

    private String prescricao; //opcional

    @NotBlank(message = "O nome do veterinário que finalizou é obrigatório.")
    private String veterinarioResponsavelFinalizacao;

    @NotBlank(message = "O CRMV do veterinário que finalizou é obrigatório.")
    private String crmvVeterinarioFinalizacao;
}