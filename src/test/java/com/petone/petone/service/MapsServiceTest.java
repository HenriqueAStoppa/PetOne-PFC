package com.petone.petone.service;

import com.petone.petone.model.Hospital;
import com.petone.petone.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapsServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private MapsService mapsService;

    @Test
    void encontrarHospitalMaisProximo_deveRetornarHospitalComMenorDistancia() {
        //Tutor SP
        double tutorLat = -23.5505;
        double tutorLon = -46.6333;

        Hospital h1 = new Hospital();
        h1.setIdHospital("H1");
        h1.setNomeFantasia("Hospital A");
        h1.setLatitude(-23.55);
        h1.setLongitude(-46.63);

        Hospital h2 = new Hospital();
        h2.setIdHospital("H2");
        h2.setNomeFantasia("Hospital B");
        h2.setLatitude(-22.90);
        h2.setLongitude(-43.17); //Tutor RJ 

        List<Hospital> hospitais = Arrays.asList(h1, h2);
        when(hospitalRepository.findAll()).thenReturn(hospitais);

        Hospital resultado = mapsService.encontrarHospitalMaisProximo(tutorLat, tutorLon);

        assertNotNull(resultado);
        assertEquals("H1", resultado.getIdHospital(), "Deve retornar o hospital mais próximo");
    }

    @Test
    void encontrarHospitalMaisProximo_deveIgnorarHospitaisSemCoordenadas() {
        double tutorLat = -23.5505;
        double tutorLon = -46.6333;

        Hospital semCoords = new Hospital();
        semCoords.setIdHospital("SEM");
        semCoords.setNomeFantasia("Sem Coordenadas");
        semCoords.setLatitude(null);
        semCoords.setLongitude(null);

        Hospital comCoords = new Hospital();
        comCoords.setIdHospital("COM");
        comCoords.setNomeFantasia("Com Coordenadas");
        comCoords.setLatitude(-23.55);
        comCoords.setLongitude(-46.63);

        when(hospitalRepository.findAll()).thenReturn(Arrays.asList(semCoords, comCoords));

        Hospital resultado = mapsService.encontrarHospitalMaisProximo(tutorLat, tutorLon);

        assertNotNull(resultado);
        assertEquals("COM", resultado.getIdHospital(), "Deve escolher o hospital que tem coordenadas");
    }

    @Test
    void encontrarHospitalMaisProximo_deveLancarExcecaoQuandoNaoExistirHospital() {
        when(hospitalRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mapsService.encontrarHospitalMaisProximo(0.0, 0.0),
                "Deve lançar exceção quando não houver hospitais");

        assertEquals("Nenhum hospital parceiro encontrado.", ex.getMessage());
    }

    @Test
    void encontrarHospitalMaisProximo_quandoNenhumTemCoordenadas_deveRetornarPrimeiroDaLista() {
        Hospital h1 = new Hospital();
        h1.setIdHospital("H1");
        h1.setNomeFantasia("Hospital 1");
        h1.setLatitude(null);
        h1.setLongitude(null);

        Hospital h2 = new Hospital();
        h2.setIdHospital("H2");
        h2.setNomeFantasia("Hospital 2");
        h2.setLatitude(null);
        h2.setLongitude(null);

        List<Hospital> hospitais = Arrays.asList(h1, h2);
        when(hospitalRepository.findAll()).thenReturn(hospitais);

        Hospital resultado = mapsService.encontrarHospitalMaisProximo(0.0, 0.0);

        assertNotNull(resultado);
        assertSame(h1, resultado,
                "Quando nenhum hospital tiver coordenadas, deve retornar o primeiro da lista");
    }
}
