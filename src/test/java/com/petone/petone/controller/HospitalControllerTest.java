package com.petone.petone.controller;

import com.petone.petone.dto.HospitalPerfilDTO;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Hospital;
import com.petone.petone.service.HospitalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalControllerTest {

    @Mock
    private HospitalService hospitalService;

    @Mock
    private Principal principal;

    @InjectMocks
    private HospitalController hospitalController;

    private final String EMAIL_HOSPITAL = "hospital@teste.com";

    @BeforeEach
    void setUp() {
        when(principal.getName()).thenReturn(EMAIL_HOSPITAL);
    }

    //getMeuPerfil 

    @Test
    void getMeuPerfil_deveRetornarHospitalQuandoEncontrado() {
        Hospital hospital = new Hospital();
        hospital.setIdHospital("123");
        hospital.setNomeFantasia("Hospital Teste");

        when(hospitalService.getMeuPerfil(EMAIL_HOSPITAL)).thenReturn(hospital);

        ResponseEntity<?> response = hospitalController.getMeuPerfil(principal);

        assertEquals(200, response.getStatusCode().value());
        assertSame(hospital, response.getBody());
        verify(hospitalService).getMeuPerfil(EMAIL_HOSPITAL);
    }

    @Test
    void getMeuPerfil_deveRetornar404QuandoHospitalNaoEncontrado() {
        when(hospitalService.getMeuPerfil(EMAIL_HOSPITAL))
                .thenThrow(new UsernameNotFoundException("Hospital não encontrado"));

        ResponseEntity<?> response = hospitalController.getMeuPerfil(principal);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Hospital não encontrado", response.getBody());
        verify(hospitalService).getMeuPerfil(EMAIL_HOSPITAL);
    }

    //updateMeuPerfil 

    @Test
    void updateMeuPerfil_deveAtualizarEDevolverHospital() {
        HospitalPerfilDTO dto = new HospitalPerfilDTO();
        dto.setNomeFantasia("Novo Nome");
        dto.setTelefoneHospital("11999999999");
        dto.setEndereco("Rua A, 123");
        dto.setClassificacaoServico(2);
        dto.setVeterinarioResponsavel("Dr. Vet");
        dto.setCrmvVeterinario("CRMV-123");

        Hospital atualizado = new Hospital();
        atualizado.setIdHospital("123");
        atualizado.setNomeFantasia(dto.getNomeFantasia());

        when(hospitalService.updateMeuPerfil(EMAIL_HOSPITAL, dto)).thenReturn(atualizado);

        ResponseEntity<?> response = hospitalController.updateMeuPerfil(dto, principal);

        assertEquals(200, response.getStatusCode().value());
        assertSame(atualizado, response.getBody());
        verify(hospitalService).updateMeuPerfil(EMAIL_HOSPITAL, dto);
    }

    @Test
    void updateMeuPerfil_deveRetornar404QuandoHospitalNaoEncontrado() {
        HospitalPerfilDTO dto = new HospitalPerfilDTO();
        when(hospitalService.updateMeuPerfil(eq(EMAIL_HOSPITAL), any(HospitalPerfilDTO.class)))
                .thenThrow(new UsernameNotFoundException("Hospital não encontrado"));

        ResponseEntity<?> response = hospitalController.updateMeuPerfil(dto, principal);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Hospital não encontrado", response.getBody());
        verify(hospitalService).updateMeuPerfil(EMAIL_HOSPITAL, dto);
    }

    //getMeusLogs

    @Test
void getMeusLogs_deveRetornarListaDeLogs() {
    EmergenciaLog log = EmergenciaLog.builder()
            .idLog("log-1")
            .build();

    List<EmergenciaLog> lista = Collections.singletonList(log);
    when(hospitalService.getMeusLogs(EMAIL_HOSPITAL)).thenReturn(lista);

    ResponseEntity<List<EmergenciaLog>> response = hospitalController.getMeusLogs(principal);

    assertEquals(200, response.getStatusCode().value());
    assertEquals(1, response.getBody().size());
    assertEquals("log-1", response.getBody().get(0).getIdLog());
    verify(hospitalService).getMeusLogs(EMAIL_HOSPITAL);
}


    //deleteMeuPerfil

    @Test
    void deleteMeuPerfil_deveRetornar204QuandoSucesso() {
        doNothing().when(hospitalService).deleteMeuPerfil(EMAIL_HOSPITAL);

        ResponseEntity<?> response = hospitalController.deleteMeuPerfil(principal);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(hospitalService).deleteMeuPerfil(EMAIL_HOSPITAL);
    }

    @Test
    void deleteMeuPerfil_deveRetornar404QuandoHospitalNaoEncontrado() {
        doThrow(new UsernameNotFoundException("Hospital não encontrado para exclusão"))
                .when(hospitalService).deleteMeuPerfil(EMAIL_HOSPITAL);

        ResponseEntity<?> response = hospitalController.deleteMeuPerfil(principal);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Hospital não encontrado para exclusão", response.getBody());
        verify(hospitalService).deleteMeuPerfil(EMAIL_HOSPITAL);
    }

    @Test
    void deleteMeuPerfil_deveRetornar500QuandoErroGenerico() {
        doThrow(new RuntimeException("Erro inesperado"))
                .when(hospitalService).deleteMeuPerfil(EMAIL_HOSPITAL);

        ResponseEntity<?> response = hospitalController.deleteMeuPerfil(principal);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Erro ao excluir hospital: Erro inesperado", response.getBody());
        verify(hospitalService).deleteMeuPerfil(EMAIL_HOSPITAL);
    }
}
