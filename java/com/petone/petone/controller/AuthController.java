package com.petone.petone.controller;

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.HospitalCadastroDTO; 
import com.petone.petone.dto.TutorCadastroDTO;
import com.petone.petone.dto.RecuperarSenhaRequestDTO; // NOVO IMPORT
import com.petone.petone.dto.ResetarSenhaRequestDTO; // NOVO IMPORT
import com.petone.petone.model.Hospital; 
import com.petone.petone.service.HospitalService; 
import com.petone.petone.service.PasswordResetService; // NOVO IMPORT
import com.petone.petone.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException; // NOVO IMPORT

/**
 * [ARQUIVO ATUALIZADO]
 * Controller público para Autenticação (Cadastro, Login e Recuperação de Senha).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TutorService tutorService;
    private final HospitalService hospitalService; 
    private final PasswordResetService passwordResetService; // NOVO CAMPO

    @Autowired
    public AuthController(TutorService tutorService, 
                          HospitalService hospitalService,
                          PasswordResetService passwordResetService) { // NOVO PARÂMETRO
        this.tutorService = tutorService;
        this.hospitalService = hospitalService; 
        this.passwordResetService = passwordResetService; // NOVA ATRIBUIÇÃO
    }

    // --- ENDPOINTS DO TUTOR (Cadastro e Login) ---
    
    @PostMapping("/cadastro/tutor")
    public ResponseEntity<?> cadastrarTutor(@Valid @RequestBody TutorCadastroDTO dto) {
        try {
            AuthResponseDTO response = tutorService.cadastrarTutor(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login/tutor")
    public ResponseEntity<?> loginTutor(@Valid @RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO response = tutorService.authenticate(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


    // --- ENDPOINTS DO HOSPITAL (Cadastro e Login) ---
    
    @PostMapping("/cadastro/hospital")
    public ResponseEntity<?> cadastrarHospital(@Valid @RequestBody HospitalCadastroDTO dto) {
        try {
            Hospital hospital = hospitalService.cadastrarHospital(dto);
            return new ResponseEntity<>(hospital, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login/hospital")
    public ResponseEntity<?> loginHospital(@Valid @RequestBody AuthRequestDTO request) {
        try {
            AuthResponseDTO response = hospitalService.authenticateHospital(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // --- [NOVOS ENDPOINTS DE RECUPERAÇÃO DE SENHA] ---

    /**
     * (POST /api/auth/recuperar-senha/solicitar)
     * Etapa 1: Solicita um token de reset para um email.
     */
    @PostMapping("/recuperar-senha/solicitar")
    public ResponseEntity<?> solicitarRecuperacao(@Valid @RequestBody RecuperarSenhaRequestDTO dto) {
        try {
            passwordResetService.solicitarReset(dto.getEmail());
            // Retorna OK mesmo se o email não existir, para não vazar dados de usuários
            return ResponseEntity.ok("Se o email estiver cadastrado, um link de recuperação foi enviado.");
        } catch (NoSuchElementException e) {
            // Captura o erro, mas retorna OK por segurança
            return ResponseEntity.ok("Se o email estiver cadastrado, um link de recuperação foi enviado.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * (POST /api/auth/recuperar-senha/resetar)
     * Etapa 2: Reseta a senha usando o token e a nova senha.
     */
    @PostMapping("/recuperar-senha/resetar")
    public ResponseEntity<?> resetarSenha(@Valid @RequestBody ResetarSenhaRequestDTO dto) {
        try {
            passwordResetService.resetarSenha(dto);
            return ResponseEntity.ok("Senha atualizada com sucesso.");
        } catch (NoSuchElementException e) {
            // Ex: Token inválido ou já utilizado
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Ex: Token expirado
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}