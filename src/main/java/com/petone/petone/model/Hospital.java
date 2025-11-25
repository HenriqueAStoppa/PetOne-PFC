package com.petone.petone.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//Modelo para a entidade Hospital Veterinário. (Versão completa com senhaHash)
@Getter
@Setter
@Data
@Document(collection = "hospitais")
public class Hospital {
    @Id
    private String idHospital;
    private String nomeFantasia;
    private String emailHospital;
    private String telefoneHospital;
    private String endereco; //Uso com a API do Google Maps
    private String cnpj; 
    private int classificacaoServico; 
    private String veterinarioResponsavel;
    private String crmvVeterinario;
    private boolean emailVerificado = false;
    private String senhaHash; 
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
}