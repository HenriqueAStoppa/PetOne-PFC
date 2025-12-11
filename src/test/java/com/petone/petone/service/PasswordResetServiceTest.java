package com.petone.petone.service;

import com.petone.petone.dto.ResetarSenhaRequestDTO;
import com.petone.petone.model.Hospital;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    // solicitar Reset

    @Test
    void solicitarReset_deveGerarTokenParaTutorEEnviarEmail() {
        String email = "tutor@teste.com";
        Tutor tutor = new Tutor();
        tutor.setEmailTutor(email);

        when(tutorRepository.findByEmailTutor(email))
                .thenReturn(Optional.of(tutor));

        passwordResetService.solicitarReset(email);

        assertNotNull(tutor.getResetToken(), "Token deve ser gerado para o tutor");
        assertNotNull(tutor.getResetTokenExpiry(), "Data de expiração deve ser gerada para o tutor");

        verify(tutorRepository).save(tutor);

        // captura o token usado no envio do e-mail
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviarEmailResetSenha(eq(email), tokenCaptor.capture());

        String tokenEnviado = tokenCaptor.getValue();
        assertEquals(tutor.getResetToken(), tokenEnviado,
                "Token enviado por e-mail deve ser o mesmo salvo no tutor");

        // não deve procurar hospital quando já achou tutor
        verify(hospitalRepository, never()).findByEmailHospital(anyString());
    }

    @Test
    void solicitarReset_deveGerarTokenParaHospitalQuandoNaoForTutor() {
        String email = "hospital@teste.com";
        when(tutorRepository.findByEmailTutor(email))
                .thenReturn(Optional.empty());

        Hospital hospital = new Hospital();
        hospital.setEmailHospital(email);

        when(hospitalRepository.findByEmailHospital(email))
                .thenReturn(Optional.of(hospital));

        passwordResetService.solicitarReset(email);

        assertNotNull(hospital.getResetToken(), "Token deve ser gerado para o hospital");
        assertNotNull(hospital.getResetTokenExpiry(), "Data de expiração deve ser gerada para o hospital");

        verify(hospitalRepository).save(hospital);

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviarEmailResetSenha(eq(email), tokenCaptor.capture());
        String tokenEnviado = tokenCaptor.getValue();
        assertEquals(hospital.getResetToken(), tokenEnviado,
                "Token enviado por e-mail deve ser o mesmo salvo no hospital");
    }

    @Test
    void solicitarReset_emailNaoEncontradoDeveLancarNoSuchElementException() {
        String email = "naoexiste@teste.com";
        when(tutorRepository.findByEmailTutor(email))
                .thenReturn(Optional.empty());
        when(hospitalRepository.findByEmailHospital(email))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> passwordResetService.solicitarReset(email),
                "Deve lançar NoSuchElementException se não encontrar tutor nem hospital");

        verify(emailService, never()).enviarEmailResetSenha(anyString(), anyString());
    }

    // resetarSenha Tutor

    @Test
    void resetarSenha_deveAtualizarSenhaDoTutorQuandoTokenValido() {
        String token = "token-tutor";
        String novaSenha = "novaSenha123";

        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken(token);
        dto.setNovaSenha(novaSenha);

        Tutor tutor = new Tutor();
        tutor.setResetToken(token);
        tutor.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        tutor.setSenhaHash("senhaVelha");

        when(tutorRepository.findByResetToken(token))
                .thenReturn(Optional.of(tutor));

        passwordResetService.resetarSenha(dto);

        assertNotNull(tutor.getSenhaHash(), "Senha do tutor não pode ficar nula");
        assertNotEquals("senhaVelha", tutor.getSenhaHash(),
                "Senha do tutor deve ser diferente da senha antiga");
        assertNull(tutor.getResetToken(), "Token deve ser limpo após resetar senha");
        assertNull(tutor.getResetTokenExpiry(), "Data de expiração deve ser limpa após resetar senha");

        verify(tutorRepository).save(tutor);
        verify(hospitalRepository, never()).findByResetToken(anyString());
    }

    @Test
    void resetarSenha_deveLancarExcecaoQuandoTokenTutorExpirado() {
        String token = "token-expirado";
        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken(token);
        dto.setNovaSenha("qualquerSenha");

        Tutor tutor = new Tutor();
        tutor.setResetToken(token);
        tutor.setResetTokenExpiry(LocalDateTime.now().minusMinutes(10));

        when(tutorRepository.findByResetToken(token))
                .thenReturn(Optional.of(tutor));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetarSenha(dto),
                "Deve lançar RuntimeException para token expirado (tutor)");

        assertEquals("Token expirado.", ex.getMessage());

        verify(tutorRepository, never()).save(any());
        verify(hospitalRepository, never()).findByResetToken(anyString());
    }

    // resetarSenha Hospital

    @Test
    void resetarSenha_deveAtualizarSenhaDoHospitalQuandoTokenValido() {
        String token = "token-hospital";
        String novaSenha = "novaSenha456";

        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken(token);
        dto.setNovaSenha(novaSenha);

        when(tutorRepository.findByResetToken(token))
                .thenReturn(Optional.empty());

        Hospital hospital = new Hospital();
        hospital.setResetToken(token);
        hospital.setResetTokenExpiry(LocalDateTime.now().plusMinutes(45));
        hospital.setSenhaHash("senhaAntiga");

        when(hospitalRepository.findByResetToken(token))
                .thenReturn(Optional.of(hospital));

        passwordResetService.resetarSenha(dto);

        assertNotNull(hospital.getSenhaHash(), "Senha do hospital não pode ficar nula");
        assertNotEquals("senhaAntiga", hospital.getSenhaHash(),
                "Senha do hospital deve ser diferente da senha antiga");
        assertNull(hospital.getResetToken(), "Token deve ser limpo após resetar senha");
        assertNull(hospital.getResetTokenExpiry(), "Data de expiração deve ser limpa após resetar senha");

        verify(hospitalRepository).save(hospital);
    }

    @Test
    void resetarSenha_deveLancarExcecaoQuandoTokenHospitalExpirado() {
        String token = "token-hosp-expirado";
        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken(token);
        dto.setNovaSenha("novaSenha");

        when(tutorRepository.findByResetToken(token))
                .thenReturn(Optional.empty());

        Hospital hospital = new Hospital();
        hospital.setResetToken(token);
        hospital.setResetTokenExpiry(LocalDateTime.now().minusMinutes(5));

        when(hospitalRepository.findByResetToken(token))
                .thenReturn(Optional.of(hospital));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetarSenha(dto),
                "Deve lançar RuntimeException para token expirado (hospital)");

        assertEquals("Token expirado.", ex.getMessage());

        verify(hospitalRepository, never()).save(any());
    }

    @Test
    void resetarSenha_deveLancarNoSuchElementQuandoTokenNaoEncontrado() {
        String token = "token-invalido";
        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken(token);
        dto.setNovaSenha("novaSenha");

        when(tutorRepository.findByResetToken(token))
                .thenReturn(Optional.empty());
        when(hospitalRepository.findByResetToken(token))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> passwordResetService.resetarSenha(dto),
                "Deve lançar NoSuchElementException quando token não for encontrado");

        verify(tutorRepository, never()).save(any());
        verify(hospitalRepository, never()).save(any());
    }
}
