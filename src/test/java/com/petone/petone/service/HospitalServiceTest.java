package com.petone.petone.service;

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.HospitalCadastroDTO;
import com.petone.petone.model.Hospital;
import com.petone.petone.repository.EmergenciaLogRepository;
import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.util.JwtUtil;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmergenciaLogRepository emergenciaLogRepository;

    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private HospitalService hospitalService;

    @Test
    void naoDevePermitirCadastroComEmailDuplicadoIgnorandoCase() {
        HospitalCadastroDTO dto = new HospitalCadastroDTO();
        dto.setNomeFantasia("Clínica Teste");
        dto.setEmailHospital("Clinica@Teste.com");  // misto maiúsculo/minúsculo
        dto.setTelefoneHospital("1133334444");
        dto.setLogradouro("Rua A");
        dto.setNumero("100");
        dto.setBairro("Centro");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setCnpj("12.345.678/0001-00");
        dto.setClassificacaoServico(2);
        dto.setVeterinarioResponsavel("Dr. Vet");
        dto.setCrmvVeterinario("CRMV-SP 0000");
        dto.setSenha("senha123");

        String emailNormalizado = "clinica@teste.com";

        when(hospitalRepository.existsByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(true);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> hospitalService.cadastrarHospital(dto)
        );

        assertEquals("Email já cadastrado.", ex.getMessage());
        verify(hospitalRepository, never()).save(any(Hospital.class));
        verify(geocodingService, never()).getCoordinates(any(), any(), any(), any());
    }

    @Test
    void naoDevePermitirCadastroComCnpjJaCadastrado() {
        HospitalCadastroDTO dto = new HospitalCadastroDTO();
        dto.setNomeFantasia("Clínica Teste");
        dto.setEmailHospital("clinica@teste.com");
        dto.setTelefoneHospital("1133334444");
        dto.setLogradouro("Rua A");
        dto.setNumero("100");
        dto.setBairro("Centro");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setCnpj("12.345.678/0001-00");
        dto.setClassificacaoServico(2);
        dto.setVeterinarioResponsavel("Dr. Vet");
        dto.setCrmvVeterinario("CRMV-SP 0000");
        dto.setSenha("senha123");

        when(hospitalRepository.existsByEmailHospitalIgnoreCase("clinica@teste.com"))
                .thenReturn(false);

        Hospital existente = new Hospital();
        existente.setCnpj(dto.getCnpj());

        when(hospitalRepository.findByCnpj(dto.getCnpj()))
                .thenReturn(Optional.of(existente));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> hospitalService.cadastrarHospital(dto)
        );

        assertEquals("CNPJ já cadastrado.", ex.getMessage());
        verify(hospitalRepository, never()).save(any(Hospital.class));
        verify(geocodingService, never()).getCoordinates(any(), any(), any(), any());
    }

    @Test
    void deveCadastrarHospitalComEmailNormalizadoECoordenadas() {
        HospitalCadastroDTO dto = new HospitalCadastroDTO();
        dto.setNomeFantasia("Clínica Teste");
        dto.setEmailHospital("Clinica@Teste.com");
        dto.setTelefoneHospital("1133334444");
        dto.setLogradouro("Rua A");
        dto.setNumero("100");
        dto.setBairro("Centro");
        dto.setCidade("São Paulo");
        dto.setUf("SP");
        dto.setCnpj("12.345.678/0001-00");
        dto.setClassificacaoServico(3);
        dto.setVeterinarioResponsavel("Dr. Vet");
        dto.setCrmvVeterinario("CRMV-SP 0000");
        dto.setSenha("senha123");

        String emailNormalizado = "clinica@teste.com";

        when(hospitalRepository.existsByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(false);
        when(hospitalRepository.findByCnpj(dto.getCnpj()))
                .thenReturn(Optional.empty());

        when(geocodingService.getCoordinates("Rua A", "100", "São Paulo", "SP"))
                .thenReturn(new double[]{-23.0, -46.0});

        when(hospitalRepository.save(any(Hospital.class))).thenAnswer(invocation -> {
            Hospital h = invocation.getArgument(0);
            h.setIdHospital("HOSP123");
            return h;
        });

        Hospital salvo = hospitalService.cadastrarHospital(dto);

        assertEquals("HOSP123", salvo.getIdHospital());
        assertEquals(emailNormalizado, salvo.getEmailHospital());
        assertEquals("Clínica Teste", salvo.getNomeFantasia());
        assertEquals("Rua A, 100 - Centro, São Paulo - SP", salvo.getEndereco());
        assertEquals(-23.0, salvo.getLatitude());
        assertEquals(-46.0, salvo.getLongitude());

        verify(geocodingService).getCoordinates("Rua A", "100", "São Paulo", "SP");

        ArgumentCaptor<Hospital> captor = ArgumentCaptor.forClass(Hospital.class);
        verify(hospitalRepository).save(captor.capture());
        Hospital enviado = captor.getValue();
        assertEquals(emailNormalizado, enviado.getEmailHospital());
    }

    @Test
    void deveAutenticarHospitalNormalizandoEmail() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("CLINICA@TESTE.COM");
        dto.setSenha("senha123");

        String emailNormalizado = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        Hospital hospital = new Hospital();
        hospital.setIdHospital("HOSP321");
        hospital.setEmailHospital(emailNormalizado);
        hospital.setNomeFantasia("Clínica Teste");

        when(hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(Optional.of(hospital));

        when(jwtUtil.generateToken(anyString())).thenReturn("token-hospital");


        AuthResponseDTO resp = hospitalService.authenticateHospital(dto);

        assertEquals("token-hospital", resp.getToken());
        assertEquals("HOSP321", resp.getIdTutor());
        assertEquals(emailNormalizado, resp.getEmail());
        assertEquals("Clínica Teste", resp.getNomeCompleto());
    }

    @Test
    void getMeuPerfilDeveBuscarIgnorandoCase() {
        String emailEntrada = "CLINICA@TESTE.COM";
        String emailNormalizado = emailEntrada.trim().toLowerCase(Locale.ROOT);

        Hospital hospital = new Hospital();
        hospital.setIdHospital("HOSP555");
        hospital.setEmailHospital(emailNormalizado);
        hospital.setNomeFantasia("Clínica Teste 555");

        when(hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(Optional.of(hospital));

        Hospital resultado = hospitalService.getMeuPerfil(emailEntrada);

        assertEquals("HOSP555", resultado.getIdHospital());
        assertEquals(emailNormalizado, resultado.getEmailHospital());
        assertEquals("Clínica Teste 555", resultado.getNomeFantasia());
    }

    @Test
    void deleteMeuPerfilDeveExcluirIgnorandoCase() {
        String emailEntrada = "CLINICA@TESTE.COM";
        String emailNormalizado = emailEntrada.trim().toLowerCase(Locale.ROOT);

        Hospital hospital = new Hospital();
        hospital.setIdHospital("HOSP999");
        hospital.setEmailHospital(emailNormalizado);

        when(hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(Optional.of(hospital));

        hospitalService.deleteMeuPerfil(emailEntrada);

        verify(hospitalRepository).delete(hospital);
    }
}
