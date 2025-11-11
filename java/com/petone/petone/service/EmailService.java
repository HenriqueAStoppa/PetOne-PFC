package com.petone.petone.service;

import com.petone.petone.model.Hospital;
import com.petone.petone.model.Tutor;
import org.springframework.stereotype.Service;

/**
 * Serviço SIMULADO para envio de emails.
 * (Para o TCC, apenas loga no console).
 */
@Service
public class EmailService {

    /**
     * Simula o envio de email para o Tutor com o token.
     */
    public void enviarEmailTokenTutor(Tutor tutor, String tokenEmergencia, Hospital hospital) {
        String email = tutor.getEmailTutor();
        String nome = tutor.getNomeCompleto();
        String hospitalNome = hospital.getNomeFantasia();
        
        System.out.println("======================================================");
        System.out.println("== SIMULAÇÃO DE ENVIO DE EMAIL ==");
        System.out.println("Para: Tutor <" + email + ">");
        System.out.println("Assunto: [PetOne] Emergência Registrada (Token: " + tokenEmergencia + ")");
        System.out.println("Olá, " + nome + ". Sua emergência foi registrada.");
        System.out.println("Seu token é: " + tokenEmergencia);
        System.out.println("Dirija-se imediatamente ao " + hospitalNome);
        System.out.println("======================================================");
    }

    /**
     * Simula o envio de email para o Hospital com o token.
     */
    public void enviarEmailTokenHospital(Hospital hospital, String tokenEmergencia, Tutor tutor) {
        String email = hospital.getEmailHospital();
        String nomeHospital = hospital.getNomeFantasia();
        String nomeTutor = tutor.getNomeCompleto();
        
        System.out.println("======================================================");
        System.out.println("== SIMULAÇÃO DE ENVIO DE EMAIL ==");
        System.out.println("Para: Hospital <" + email + ">");
        System.out.println("Assunto: [PetOne] ALERTA DE EMERGÊNCIA (Token: " + tokenEmergencia + ")");
        System.out.println("Olá, " + nomeHospital + ".");
        System.out.println("Uma emergência foi registrada para o tutor: " + nomeTutor);
        System.out.println("O token de acesso ao log é: " + tokenEmergencia);
        System.out.println("Preparem-se para a chegada.");
        System.out.println("======================================================");
    }
}