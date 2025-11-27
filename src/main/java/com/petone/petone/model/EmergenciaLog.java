package com.petone.petone.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

//Modelo para o Log de Emergência. Criado quando uma emergência é iniciada.
@Data
@Builder
@Document(collection = "logs_emergencia")
public class EmergenciaLog {
    @Id
    private String idLog;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraRegistro;
    private String tokenEmergencia; // Token único da emergência

    // Dados do Hospital
    private String idHospital;
    private String nomeFantasiaHospital;
    private String telefoneHospital;
    private String emailHospital;

    // Dados do Tutor
    private String idTutor;
    private String nomeCompletoTutor;
    private String telefoneTutor;
    private String emailTutor;

    // Dados do Animal
    private String idAnimal;
    private String nomeAnimal;
    private int idadeAnimal;
    private String especieAnimal;
    private String racaAnimal;
    private String sexoAnimal;

    // Dados da Emergência (Formulário)
    private String tipoEmergencia;

    // Dados de Finalização (Preenchidos pelo Hospital)
    private LocalDateTime dataHoraFim;
    private String relatorio;
    private String prescricao;
    private String veterinarioResponsavelFinalizacao;
    private String crmvVeterinarioFinalizacao;
    private String status;
}