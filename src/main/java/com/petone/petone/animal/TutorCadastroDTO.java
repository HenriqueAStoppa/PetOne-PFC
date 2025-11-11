package com.petone.petone.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO para receber dados do frontend durante o cadastro de um novo Tutor.
 */
@Data
public class TutorCadastroDTO {

    @NotBlank(message = "O nome completo é obrigatório.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "CPF inválido.")
    private String cpf;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email deve ser um endereço de email válido.")
    private String emailTutor;

    @NotBlank(message = "O telefone é obrigatório.")
    private String telefoneTutor;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "Data de nascimento deve estar no passado.")
    private LocalDate dataNascimento;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;
}