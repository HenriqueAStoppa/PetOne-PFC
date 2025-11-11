package com.petone.petone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * Modelo para a entidade Animal.
 * Vinculado ao tutor (idTutor). Usa Lombok.
 */
@Data
@Document(collection = "animais")
public class Animal {
    @Id
    private String idAnimal;
    private String idTutor; // Chave estrangeira para Tutor
    private String nomeAnimal;
    private int idade;
    private String especie;
    private String raca;
    private String sexo;
    private boolean castrado;
    private boolean usaMedicacao;
    private String qualMedicacao;
}