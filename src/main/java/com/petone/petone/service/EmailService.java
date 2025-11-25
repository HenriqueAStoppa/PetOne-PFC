package com.petone.petone.service;

import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Tutor;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarEmailTokenParaTutor(Tutor tutor, EmergenciaLog log) {
        System.out.println("Assunto: [PetOne] Token da sua Emergência");
        System.out.println("Seu token é: " + log.getTokenEmergencia());
    }

    public void enviarEmailAlertaParaHospital(EmergenciaLog log) {
        System.out.println("==================================================");
        System.out.println("Assunto: [ALERTA DE EMERGÊNCIA] Paciente a caminho!");
        System.out.println("Token: " + log.getTokenEmergencia());
        System.out.println("Sintoma: " + log.getTipoEmergencia()); 
        System.out.println("Tutor: " + log.getNomeCompletoTutor());
    }
    public void enviarEmailResetSenha(String email, String token) {
    }
}