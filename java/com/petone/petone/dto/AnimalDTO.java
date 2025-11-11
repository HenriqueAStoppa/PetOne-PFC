package com.petone.petone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para cadastro e atualização de Animal.
 */
@Data
public class AnimalDTO {
    @NotBlank(message = "O nome do animal é obrigatório.")
    private String nomeAnimal;

    @NotNull(message = "A idade é obrigatória.")
    @Min(value = 0, message = "Idade não pode ser negativa.")
    private int idade;

    @NotBlank(message = "A espécie é obrigatória.")
    private String especie;

    @NotBlank(message = "A raça é obrigatória.")
    private String raca;

    @NotBlank(message = "O sexo é obrigatório.")
    private String sexo;

    @NotNull(message = "Informar se é castrado é obrigatório.")
    private boolean castrado;

    @NotNull(message = "Informar se usa medicação é obrigatório.")
    private boolean usaMedicacao;

    private String qualMedicacao; // Opcional
}