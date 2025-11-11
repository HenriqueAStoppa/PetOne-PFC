package com.petone.petone.service;

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.HospitalCadastroDTO;
import com.petone.petone.model.Hospital;
import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.util.JwtUtil;
import com.petone.petone.util.PasswordUtil;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * [ARQUIVO CORRIGIDO]
 * Serviço para a lógica de negócio do Hospital.
 * (Versão completa e corrigida com login via AuthenticationManager)
 */
@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager; // Corrigido

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager) { // Corrigido
        this.hospitalRepository = hospitalRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager; // Corrigido
    }

    /**
     * Cadastra um novo hospital.
     */
    public Hospital cadastrarHospital(HospitalCadastroDTO dto) {
        if (hospitalRepository.findByEmailHospital(dto.getEmailHospital()).isPresent()) {
            throw new ValidationException("Email já cadastrado.");
        }
        if (hospitalRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new ValidationException("CNPJ já cadastrado.");
        }

        Hospital hospital = new Hospital();
        hospital.setNomeFantasia(dto.getNomeFantasia());
        hospital.setEmailHospital(dto.getEmailHospital());
        hospital.setTelefoneHospital(dto.getTelefoneHospital());
        hospital.setEndereco(dto.getEndereco());
        hospital.setCnpj(dto.getCnpj());
        hospital.setClassificacaoServico(dto.getClassificacaoServico());
        hospital.setVeterinarioResponsavel(dto.getVeterinarioResponsavel());
        hospital.setCrmvVeterinario(dto.getCrmvVeterinario());
        hospital.setSenhaHash(PasswordUtil.encode(dto.getSenha()));
        hospital.setEmailVerificado(true); // Simulação TCC

        return hospitalRepository.save(hospital);
    }

    /**
     * [MÉTODO CORRIGIDO]
     * Autentica um hospital usando o AuthenticationManager.
     */
    public AuthResponseDTO authenticateHospital(AuthRequestDTO dto) throws Exception {
        
        // 1. Autenticar com o Spring Security
        // (O UserDetailsServiceImpl agora entende Hospitais)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais inválidas", e);
        }

        // 2. Se a autenticação passou, buscar o hospital
        Hospital hospital = hospitalRepository.findByEmailHospital(dto.getEmail())
                // NOTA: Esta linha foi atualizada para lançar a exceção correta
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + dto.getEmail()));

        // 3. Gerar o token JWT
        final String token = jwtUtil.generateToken(hospital.getEmailHospital());

        // 4. Retornar a resposta
        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(hospital.getIdHospital()) // Reutilizando o DTO
                .email(hospital.getEmailHospital())
                .nomeCompleto(hospital.getNomeFantasia()) // Reutilizando o DTO
                .build();
    }
}