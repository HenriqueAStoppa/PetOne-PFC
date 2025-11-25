package com.petone.petone.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

//DTO para a atualização do perfil do Hospital (campos permitidos).Não inclui email, cnpj ou senha.
@Data
public class HospitalPerfilDTO {

    @NotBlank(message = "O nome fantasia é obrigatório.")
    private String nomeFantasia;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefoneHospital;

    @NotBlank(message = "O endereço é obrigatório.")
    private String endereco;

    @NotNull(message = "A classificação de serviço é obrigatória.")
    @Min(value = 1, message = "Classificação deve ser no mínimo 1.")
    @Max(value = 4, message = "Classificação deve ser no máximo 4.")
    private int classificacaoServico;

    @NotBlank(message = "O nome do veterinário responsável é obrigatório.")
    private String veterinarioResponsavel;

    @NotBlank(message = "O CRMV do veterinário é obrigatório.")
    private String crmvVeterinario;
}