package com.petone.petone.controller;

import com.petone.petone.dto.TutorPerfilDTO;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Tutor;
import com.petone.petone.service.TutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorControllerTest {

    @Mock
    private TutorService tutorService;

    @InjectMocks
    private TutorController tutorController;

    private Principal principal;
    private final String EMAIL_TUTOR = "tutor@exemplo.com";

    @BeforeEach
    void setUp() {
        principal = () -> EMAIL_TUTOR;
    }

    @Test
    void getMeuPerfil_deveRetornarPerfilDoTutor() {
        Tutor tutor = new Tutor();
        tutor.setIdTutor("t1");
        tutor.setNomeCompleto("Tutor Teste");
        tutor.setEmailTutor(EMAIL_TUTOR);
        tutor.setTelefoneTutor("11999999999");
        tutor.setDataNascimento(LocalDate.of(1990, 1, 1));

        when(tutorService.getMeuPerfil(EMAIL_TUTOR)).thenReturn(tutor);

        ResponseEntity<Tutor> response = tutorController.getMeuPerfil(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("t1", response.getBody().getIdTutor());
        assertEquals("Tutor Teste", response.getBody().getNomeCompleto());
        assertEquals(EMAIL_TUTOR, response.getBody().getEmailTutor());

        verify(tutorService, times(1)).getMeuPerfil(EMAIL_TUTOR);
    }

    @Test
    void updateMeuPerfil_deveAtualizarEDeverRetornarTutorAtualizado() {
        TutorPerfilDTO dto = new TutorPerfilDTO();
        dto.setNomeCompleto("Novo Nome");
        dto.setTelefoneTutor("11988887777");
        dto.setDataNascimento(LocalDate.of(1995, 5, 20));

        //Tutor que o serviço retorna após atualizar
        Tutor tutorAtualizado = new Tutor();
        tutorAtualizado.setIdTutor("t1");
        tutorAtualizado.setNomeCompleto(dto.getNomeCompleto());
        tutorAtualizado.setTelefoneTutor(dto.getTelefoneTutor());
        tutorAtualizado.setDataNascimento(dto.getDataNascimento());
        tutorAtualizado.setEmailTutor(EMAIL_TUTOR);

        when(tutorService.updateMeuPerfil(EMAIL_TUTOR, dto)).thenReturn(tutorAtualizado);

        ResponseEntity<Tutor> response = tutorController.updateMeuPerfil(dto, principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("t1", response.getBody().getIdTutor());
        assertEquals("Novo Nome", response.getBody().getNomeCompleto());
        assertEquals("11988887777", response.getBody().getTelefoneTutor());
        assertEquals(dto.getDataNascimento(), response.getBody().getDataNascimento());

        verify(tutorService, times(1)).updateMeuPerfil(EMAIL_TUTOR, dto);
    }

    @Test
    void deleteMeuPerfil_deveChamarServiceEDevolverMensagemDeSucesso() {
        ResponseEntity<?> response = tutorController.deleteMeuPerfil(principal);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Perfil deletado com sucesso.", response.getBody());

        verify(tutorService, times(1)).deleteMeuPerfil(EMAIL_TUTOR);
    }

    @Test
    void getMeusLogs_deveRetornarListaDeLogsDaEmergenciaDoTutor() {
        EmergenciaLog log1 = EmergenciaLog.builder()
                .idLog("log1")
                .tokenEmergencia("TOK123")
                .nomeAnimal("Rex")
                .nomeFantasiaHospital("Hospital PetOne")
                .nomeCompletoTutor("Tutor Teste")
                .build();

        EmergenciaLog log2 = EmergenciaLog.builder()
                .idLog("log2")
                .tokenEmergencia("TOK456")
                .nomeAnimal("Mia")
                .nomeFantasiaHospital("Clínica X")
                .nomeCompletoTutor("Tutor Teste")
                .build();

        List<EmergenciaLog> lista = List.of(log1, log2);

        when(tutorService.getMeusLogs(EMAIL_TUTOR)).thenReturn(lista);

        ResponseEntity<List<EmergenciaLog>> response = tutorController.getMeusLogs(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("log1", response.getBody().get(0).getIdLog());
        assertEquals("log2", response.getBody().get(1).getIdLog());

        verify(tutorService, times(1)).getMeusLogs(EMAIL_TUTOR);
    }
}
