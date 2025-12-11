package com.petone.petone.controller;

import com.petone.petone.dto.*;
import com.petone.petone.model.Hospital;
import com.petone.petone.service.HospitalService;
import com.petone.petone.service.PasswordResetService;
import com.petone.petone.service.TutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private TutorService tutorService;

    @Mock
    private HospitalService hospitalService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    private TutorCadastroDTO tutorCadastroDTO;
    private HospitalCadastroDTO hospitalCadastroDTO;
    private AuthRequestDTO authRequestDTO;

    @BeforeEach
    void setUp() {
        tutorCadastroDTO = new TutorCadastroDTO();
        tutorCadastroDTO.setNomeCompleto("Tutor Teste");
        tutorCadastroDTO.setEmailTutor("tutor@teste.com");
        tutorCadastroDTO.setSenha("123456");

        hospitalCadastroDTO = new HospitalCadastroDTO();
        hospitalCadastroDTO.setNomeFantasia("Hospital Teste");
        hospitalCadastroDTO.setEmailHospital("hospital@teste.com");
        hospitalCadastroDTO.setSenha("123456");

        authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("user@teste.com");
        authRequestDTO.setSenha("123456");
    }

    //cadastro/tutor

    @Test
    void cadastrarTutor_deveRetornarCreatedQuandoSucesso() {
        AuthResponseDTO respostaService = AuthResponseDTO.builder()
                .token("token-tutor")
                .idTutor("id-tutor-123")
                .email("tutor@teste.com")
                .nomeCompleto("Tutor Teste")
                .build();

        when(tutorService.cadastrarTutor(tutorCadastroDTO)).thenReturn(respostaService);

        ResponseEntity<?> response = authController.cadastrarTutor(tutorCadastroDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponseDTO);

        AuthResponseDTO body = (AuthResponseDTO) response.getBody();
        assertEquals("token-tutor", body.getToken());
        assertEquals("tutor@teste.com", body.getEmail());

        verify(tutorService, times(1)).cadastrarTutor(tutorCadastroDTO);
    }

    @Test
    void cadastrarTutor_deveRetornarBadRequestQuandoServiceLancarRuntime() {
        when(tutorService.cadastrarTutor(tutorCadastroDTO))
                .thenThrow(new RuntimeException("Email já cadastrado."));

        ResponseEntity<?> response = authController.cadastrarTutor(tutorCadastroDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email já cadastrado.", response.getBody());
        verify(tutorService, times(1)).cadastrarTutor(tutorCadastroDTO);
    }

    //login/tutor

    @Test
    void loginTutor_deveRetornarOkQuandoCredenciaisValidas() throws Exception {
        AuthResponseDTO respostaService = AuthResponseDTO.builder()
                .token("token-tutor-login")
                .email("tutor@teste.com")
                .nomeCompleto("Tutor Teste")
                .build();

        when(tutorService.authenticate(authRequestDTO)).thenReturn(respostaService);

        ResponseEntity<?> response = authController.loginTutor(authRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponseDTO);

        AuthResponseDTO body = (AuthResponseDTO) response.getBody();
        assertEquals("token-tutor-login", body.getToken());

        verify(tutorService, times(1)).authenticate(authRequestDTO);
    }

    @Test
    void loginTutor_deveRetornarUnauthorizedQuandoCredenciaisInvalidas() throws Exception {
        when(tutorService.authenticate(authRequestDTO))
                .thenThrow(new Exception("Credenciais inválidas"));

        ResponseEntity<?> response = authController.loginTutor(authRequestDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
        verify(tutorService, times(1)).authenticate(authRequestDTO);
    }

    //cadastro/hospital

    @Test
    void cadastrarHospital_deveRetornarCreatedQuandoSucesso() {
        Hospital hospitalSalvo = new Hospital();
        hospitalSalvo.setIdHospital("id-hosp-123");
        hospitalSalvo.setNomeFantasia("Hospital Teste");
        hospitalSalvo.setEmailHospital("hospital@teste.com");

        when(hospitalService.cadastrarHospital(hospitalCadastroDTO)).thenReturn(hospitalSalvo);

        ResponseEntity<?> response = authController.cadastrarHospital(hospitalCadastroDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Hospital);

        Hospital body = (Hospital) response.getBody();
        assertEquals("id-hosp-123", body.getIdHospital());
        assertEquals("hospital@teste.com", body.getEmailHospital());

        verify(hospitalService, times(1)).cadastrarHospital(hospitalCadastroDTO);
    }

    @Test
    void cadastrarHospital_deveRetornarBadRequestQuandoServiceLancarRuntime() {
        when(hospitalService.cadastrarHospital(hospitalCadastroDTO))
                .thenThrow(new RuntimeException("CNPJ já cadastrado."));

        ResponseEntity<?> response = authController.cadastrarHospital(hospitalCadastroDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CNPJ já cadastrado.", response.getBody());
        verify(hospitalService, times(1)).cadastrarHospital(hospitalCadastroDTO);
    }

    //login/hospital

    @Test
    void loginHospital_deveRetornarOkQuandoCredenciaisValidas() throws Exception {
        AuthResponseDTO respostaService = AuthResponseDTO.builder()
                .token("token-hosp-login")
                .email("hospital@teste.com")
                .nomeCompleto("Hospital Teste")
                .build();

        when(hospitalService.authenticateHospital(authRequestDTO)).thenReturn(respostaService);

        ResponseEntity<?> response = authController.loginHospital(authRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponseDTO);

        AuthResponseDTO body = (AuthResponseDTO) response.getBody();
        assertEquals("token-hosp-login", body.getToken());

        verify(hospitalService, times(1)).authenticateHospital(authRequestDTO);
    }

    @Test
    void loginHospital_deveRetornarUnauthorizedQuandoCredenciaisInvalidas() throws Exception {
        when(hospitalService.authenticateHospital(authRequestDTO))
                .thenThrow(new Exception("Credenciais inválidas"));

        ResponseEntity<?> response = authController.loginHospital(authRequestDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
        verify(hospitalService, times(1)).authenticateHospital(authRequestDTO);
    }

    //recuperar-senha/solicitar

    @Test
    void solicitarRecuperacao_deveRetornarOkMesmoQuandoSucesso() {
        RecuperarSenhaRequestDTO dto = new RecuperarSenhaRequestDTO();
        dto.setEmail("user@teste.com");

        ResponseEntity<?> response = authController.solicitarRecuperacao(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Se o email estiver cadastrado, um link de recuperação foi enviado.", response.getBody());

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordResetService, times(1)).solicitarReset(emailCaptor.capture());
        assertEquals("user@teste.com", emailCaptor.getValue());
    }

    @Test
    void solicitarRecuperacao_deveRetornarOkMesmoQuandoEmailNaoExiste() {
        RecuperarSenhaRequestDTO dto = new RecuperarSenhaRequestDTO();
        dto.setEmail("naoexiste@teste.com");

        doThrow(new NoSuchElementException("Nenhum usuário encontrado"))
                .when(passwordResetService).solicitarReset(dto.getEmail());

        ResponseEntity<?> response = authController.solicitarRecuperacao(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Se o email estiver cadastrado, um link de recuperação foi enviado.", response.getBody());
        verify(passwordResetService, times(1)).solicitarReset(dto.getEmail());
    }

    @Test
    void solicitarRecuperacao_deveRetornarInternalServerErrorQuandoOutraExcecao() {
        RecuperarSenhaRequestDTO dto = new RecuperarSenhaRequestDTO();
        dto.setEmail("user@teste.com");

        doThrow(new RuntimeException("Erro SMTP"))
                .when(passwordResetService).solicitarReset(dto.getEmail());

        ResponseEntity<?> response = authController.solicitarRecuperacao(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro SMTP", response.getBody());
        verify(passwordResetService, times(1)).solicitarReset(dto.getEmail());
    }

    //recuperar-senha/resetar 

    @Test
    void resetarSenha_deveRetornarOkQuandoSucesso() {
        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken("token-123");
        dto.setNovaSenha("novaSenha123");

        ResponseEntity<?> response = authController.resetarSenha(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Senha atualizada com sucesso.", response.getBody());
        verify(passwordResetService, times(1)).resetarSenha(dto);
    }

    @Test
    void resetarSenha_deveRetornarBadRequestQuandoTokenInvalido() {
        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken("token-invalido");
        dto.setNovaSenha("novaSenha123");

        doThrow(new NoSuchElementException("Token inválido ou já utilizado."))
                .when(passwordResetService).resetarSenha(dto);

        ResponseEntity<?> response = authController.resetarSenha(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token inválido ou já utilizado.", response.getBody());
        verify(passwordResetService, times(1)).resetarSenha(dto);
    }

    @Test
    void resetarSenha_deveRetornarBadRequestQuandoTokenExpiradoOuOutraRuntime() {
        ResetarSenhaRequestDTO dto = new ResetarSenhaRequestDTO();
        dto.setToken("token-expirado");
        dto.setNovaSenha("novaSenha123");

        doThrow(new RuntimeException("Token expirado."))
                .when(passwordResetService).resetarSenha(dto);

        ResponseEntity<?> response = authController.resetarSenha(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token expirado.", response.getBody());
        verify(passwordResetService, times(1)).resetarSenha(dto);
    }
}
