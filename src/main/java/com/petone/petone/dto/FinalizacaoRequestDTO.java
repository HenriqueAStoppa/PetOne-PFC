package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para receber os dados do Hospital ao finalizar um atendimento.
 */
@Data
public class FinalizacaoRequestDTO {

    @NotBlank(message = "O relatório médico é obrigatório.")
    private String relatorio;

    private String prescricao; // Pode ser opcional (ex: "Nenhuma medicação")

    @NotBlank(message = "O nome do veterinário que finalizou é obrigatório.")
    private String veterinarioResponsavelFinalizacao;

    @NotBlank(message = "O CRMV do veterinário que finalizou é obrigatório.")
    private String crmvVeterinarioFinalizacao;
}