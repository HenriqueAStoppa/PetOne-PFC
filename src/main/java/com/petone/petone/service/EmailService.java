package com.petone.petone.service;

import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${petone.mail.from}")
    private String remetente;

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

    //email reset de senha

    public void enviarEmailResetSenha(String email, String token) {
        String assunto = "[PetOne] Redefinição de senha";

        String corpo = """
                Olá!

                Recebemos uma solicitação para redefinir sua senha no PetOne.

                Use o seguinte token para concluir a redefinição de senha:

                %s

                Este token é válido por 1 hora.

                Se você não fez essa solicitação, apenas ignore este e-mail.

                Atenciosamente,
                Equipe PetOne
                """.formatted(token);

        try {
            System.out.println("[EmailService] Preparando envio de email de reset...");
            System.out.println("[EmailService] Remetente: " + remetente);
            System.out.println("[EmailService] Destinatário: " + email);

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(email);
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);

            mailSender.send(mensagem);

            System.out.println("[EmailService] Email de reset ENVIADO para: " + email);
        } catch (Exception e) {
            System.out.println("[EmailService] ERRO ao enviar email de reset:");
            e.printStackTrace();
        }
    }
}
