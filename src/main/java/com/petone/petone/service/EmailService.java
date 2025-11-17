package com.petone.petone.service;

import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Tutor;
import org.springframework.stereotype.Service;

/**
 * SERVIÇO SIMULADO (STUB)
 * ... (comentários existentes) ...
 */
@Service
public class EmailService {

    public void enviarEmailTokenParaTutor(Tutor tutor, EmergenciaLog log) {
// ... (código existente) ...
        System.out.println("Assunto: [PetOne] Token da sua Emergência");
        System.out.println("Seu token é: " + log.getTokenEmergencia());
// ... (código existente) ...
    }

    public void enviarEmailAlertaParaHospital(EmergenciaLog log) {
        System.out.println("==================================================");
// ... (código existente) ...
        System.out.println("Assunto: [ALERTA DE EMERGÊNCIA] Paciente a caminho!");
        System.out.println("Token: " + log.getTokenEmergencia());
        
        // [CORREÇÃO AQUI] Renomear o método para o novo nome do campo
        System.out.println("Sintoma: " + log.getTipoEmergencia()); // Antes era getSintomaCausa()
        
        System.out.println("Tutor: " + log.getNomeCompletoTutor());
// ... (código existente) ...
    }

    /**
     * ... (método enviarEmailResetSenha existente) ...
     */
    public void enviarEmailResetSenha(String email, String token) {
// ... (código existente) ...
    }
}