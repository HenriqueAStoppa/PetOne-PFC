package com.petone.petone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDate;

/**
 * Modelo para a entidade Tutor (Usuário). Usa Lombok.
 */
@Data
@Document(collection = "tutores")
public class Tutor {
    @Id
    private String idTutor;
    private String nomeCompleto;
    private String cpf; // Dado sensível
    private String emailTutor;
    private String telefoneTutor;
    private LocalDate dataNascimento;
    private String senhaHash; // Armazena o hash da senha (BCrypt)
    private boolean emailVerificado = false;
}