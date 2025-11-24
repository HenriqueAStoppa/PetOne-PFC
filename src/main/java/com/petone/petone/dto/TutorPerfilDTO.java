package com.petone.petone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TutorPerfilDTO {
    @NotBlank(message = "O nome completo é obrigatório.")
    private String nomeCompleto;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefoneTutor;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "Data de nascimento deve estar no passado.")
    private LocalDate dataNascimento;
}