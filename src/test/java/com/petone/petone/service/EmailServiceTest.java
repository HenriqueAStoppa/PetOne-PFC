package com.petone.petone.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void enviarEmailResetSenha_deveEnviarEmailComTokenEAssuntoCorretos() throws Exception {
        String emailDestino = "teste@exemplo.com";
        String token = "abc123-token-teste";

        Field remetenteField = EmailService.class.getDeclaredField("remetente");
        remetenteField.setAccessible(true);
        remetenteField.set(emailService, "noreply@petone.com");

        emailService.enviarEmailResetSenha(emailDestino, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        //verifica que o JavaMailSender.send() foi chamado 1x
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage mensagemEnviada = messageCaptor.getValue();
        assertNotNull(mensagemEnviada, "A mensagem enviada não deve ser nula");

        assertEquals("noreply@petone.com", mensagemEnviada.getFrom(),
                "O remetente deve ser o configurado no campo 'remetente'");

        assertArrayEquals(
                new String[]{emailDestino},
                mensagemEnviada.getTo(),
                "O destinatário deve ser o email passado no método"
        );

        assertEquals("[PetOne] Redefinição de senha", mensagemEnviada.getSubject(),
                "O assunto do e-mail deve ser o esperado");

        String texto = mensagemEnviada.getText();
        assertNotNull(texto, "O corpo do e-mail não deve ser nulo");
        assertTrue(texto.contains(token),
                "O corpo do e-mail deve conter o token de reset");
        assertTrue(texto.contains("PetOne"),
                "O corpo do e-mail deve mencionar o sistema PetOne");
        assertTrue(texto.contains("1 hora"),
                "O corpo do e-mail deve informar a validade do token");
    }
}
