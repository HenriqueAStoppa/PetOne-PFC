package com.petone.petone.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HospitalCadastroDTO {

    @NotBlank(message = "O nome fantasia é obrigatório.")
    private String nomeFantasia;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email inválido.")
    private String emailHospital;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefoneHospital;

    // --- NOVOS CAMPOS (Vêm do Front-end) ---
    @NotBlank(message = "O CEP é obrigatório.")
    private String cep;

    @NotBlank(message = "O logradouro (rua) é obrigatório.")
    private String logradouro;

    @NotBlank(message = "O número é obrigatório.")
    private String numero;

    @NotBlank(message = "O bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "O estado (UF) é obrigatório.")
    private String uf;
    
    private String complemento;
    // ---------------------------------------

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(regexp = "^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$", message = "CNPJ inválido.")
    private String cnpj;

    @NotNull(message = "A classificação de serviço é obrigatória.")
    @Min(value = 1, message = "Mínimo 1") @Max(value = 4, message = "Máximo 4")
    private int classificacaoServico;

    @NotBlank(message = "O nome do veterinário é obrigatório.")
    private String veterinarioResponsavel;

    @NotBlank(message = "O CRMV é obrigatório.")
    private String crmvVeterinario;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;
}