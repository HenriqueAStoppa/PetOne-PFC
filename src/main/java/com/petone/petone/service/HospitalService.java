package com.petone.petone.service; // Pacote em minúsculo

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.HospitalCadastroDTO;
import com.petone.petone.dto.HospitalPerfilDTO; // Import DTO Perfil
import com.petone.petone.model.EmergenciaLog; // Import Log
import com.petone.petone.model.Hospital;
import com.petone.petone.repository.EmergenciaLogRepository; // Import Log Repo
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

import java.util.List; // Import List

/**
 * Serviço para a lógica de negócio do Hospital.
 * (Versão completa e corrigida com Gerenciamento de Perfil e Logs)
 */
@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmergenciaLogRepository emergenciaLogRepository; // Repositório de Logs

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, 
                           JwtUtil jwtUtil, 
                           AuthenticationManager authenticationManager,
                           EmergenciaLogRepository emergenciaLogRepository) { // Injeção
        this.hospitalRepository = hospitalRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emergenciaLogRepository = emergenciaLogRepository; // Atribuição
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
     * Autentica um hospital usando o AuthenticationManager.
     */
    public AuthResponseDTO authenticateHospital(AuthRequestDTO dto) throws Exception {
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais inválidas", e);
        }

        Hospital hospital = hospitalRepository.findByEmailHospital(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + dto.getEmail()));

        final String token = jwtUtil.generateToken(hospital.getEmailHospital());

        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(hospital.getIdHospital()) // Reutilizando o DTO
                .email(hospital.getEmailHospital())
                .nomeCompleto(hospital.getNomeFantasia()) // Reutilizando o DTO
                .build();
    }

    // --- MÉTODOS DE PERFIL (OS QUE FALTAVAM) ---

    /**
     * [MÉTODO FALTANTE]
     * Busca os dados do perfil do hospital logado.
     * @param hospitalEmail Email do principal (usuário logado).
     * @return O objeto Hospital.
     */
    public Hospital getMeuPerfil(String hospitalEmail) {
        return hospitalRepository.findByEmailHospital(hospitalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital não encontrado: " + hospitalEmail));
    }

    /**
     * [MÉTODO FALTANTE]
     * Atualiza os dados do perfil do hospital logado.
     * @param hospitalEmail Email do principal.
     * @param dto DTO com os novos dados.
     * @return O Hospital com dados atualizados.
     */
    public Hospital updateMeuPerfil(String hospitalEmail, HospitalPerfilDTO dto) {
        Hospital hospital = getMeuPerfil(hospitalEmail); // Reusa o método anterior

        // Atualiza os campos permitidos
        hospital.setNomeFantasia(dto.getNomeFantasia());
        hospital.setTelefoneHospital(dto.getTelefoneHospital());
        hospital.setEndereco(dto.getEndereco());
        hospital.setClassificacaoServico(dto.getClassificacaoServico());
        hospital.setVeterinarioResponsavel(dto.getVeterinarioResponsavel());
        hospital.setCrmvVeterinario(dto.getCrmvVeterinario());

        return hospitalRepository.save(hospital);
    }

    /**
     * [MÉTODO FALTANTE]
     * Busca os logs de emergência que foram encaminhados para o hospital logado.
     * @param hospitalEmail O email do hospital logado (do JWT).
     * @return Lista de logs.
     */
    public List<EmergenciaLog> getMeusLogs(String hospitalEmail) {
        // 1. Busca o hospital
        Hospital hospital = getMeuPerfil(hospitalEmail);
        
        // 2. Busca os logs pelo ID do hospital
        return emergenciaLogRepository.findByIdHospitalEncaminhado(hospital.getIdHospital());
    }
}