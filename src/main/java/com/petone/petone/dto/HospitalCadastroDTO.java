package com.petone.petone.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para receber dados do frontend durante o cadastro de um novo Hospital.
 */
@Data
public class HospitalCadastroDTO {

    @NotBlank(message = "O nome fantasia é obrigatório.")
    private String nomeFantasia;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email deve ser um endereço de email válido.")
    private String emailHospital;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefoneHospital;

    @NotBlank(message = "O endereço é obrigatório.")
    private String endereco; // Ex: "Rua Exemplo, 123, Bairro, Cidade - UF"

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$", message = "CNPJ inválido.")
    private String cnpj;

    @NotNull(message = "A classificação de serviço é obrigatória.")
    @Min(value = 1, message = "Classificação deve ser no mínimo 1.")
    @Max(value = 4, message = "Classificação deve ser no máximo 4.")
    private int classificacaoServico;

    @NotBlank(message = "O nome do veterinário responsável é obrigatório.")
    private String veterinarioResponsavel;

    @NotBlank(message = "O CRMV do veterinário é obrigatório.")
    private String crmvVeterinario;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;
}