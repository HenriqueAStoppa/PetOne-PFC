package com.petone.petone.service;

import com.petone.petone.dto.*;
import com.petone.petone.model.*;
import com.petone.petone.repository.*;
import com.petone.petone.util.*;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmergenciaLogRepository emergenciaLogRepository;
    private final GeocodingService geocodingService; // <--- Injeção Nova

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, 
                           JwtUtil jwtUtil, 
                           AuthenticationManager authenticationManager,
                           EmergenciaLogRepository emergenciaLogRepository,
                           GeocodingService geocodingService) {
        this.hospitalRepository = hospitalRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emergenciaLogRepository = emergenciaLogRepository;
        this.geocodingService = geocodingService;
    }

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
        
        // 1. Monta o endereço completo para exibição
        String enderecoCompleto = String.format("%s, %s - %s, %s - %s", 
            dto.getLogradouro(), dto.getNumero(), dto.getBairro(), dto.getCidade(), dto.getUf());
        hospital.setEndereco(enderecoCompleto);

        // 2. Busca Latitude e Longitude automaticamente
        double[] coords = geocodingService.getCoordinates(
            dto.getLogradouro(), dto.getNumero(), dto.getCidade(), dto.getUf()
        );
        hospital.setLatitude(coords[0]);
        hospital.setLongitude(coords[1]);

        hospital.setCnpj(dto.getCnpj());
        hospital.setClassificacaoServico(dto.getClassificacaoServico());
        hospital.setVeterinarioResponsavel(dto.getVeterinarioResponsavel());
        hospital.setCrmvVeterinario(dto.getCrmvVeterinario());
        hospital.setSenhaHash(PasswordUtil.encode(dto.getSenha()));
        hospital.setEmailVerificado(true);

        return hospitalRepository.save(hospital);
    }

    // ... (Mantenha os outros métodos iguais: authenticateHospital, getMeuPerfil, etc.) ...
    public AuthResponseDTO authenticateHospital(AuthRequestDTO dto) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais inválidas", e);
        }
        Hospital hospital = hospitalRepository.findByEmailHospital(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        final String token = jwtUtil.generateToken(hospital.getEmailHospital());
        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(hospital.getIdHospital())
                .email(hospital.getEmailHospital())
                .nomeCompleto(hospital.getNomeFantasia())
                .build();
    }

    public Hospital getMeuPerfil(String hospitalEmail) {
        return hospitalRepository.findByEmailHospital(hospitalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital não encontrado"));
    }

    public Hospital updateMeuPerfil(String hospitalEmail, HospitalPerfilDTO dto) {
        Hospital hospital = getMeuPerfil(hospitalEmail);
        hospital.setNomeFantasia(dto.getNomeFantasia());
        hospital.setTelefoneHospital(dto.getTelefoneHospital());
        hospital.setEndereco(dto.getEndereco());
        hospital.setClassificacaoServico(dto.getClassificacaoServico());
        hospital.setVeterinarioResponsavel(dto.getVeterinarioResponsavel());
        hospital.setCrmvVeterinario(dto.getCrmvVeterinario());
        return hospitalRepository.save(hospital);
    }

    public List<EmergenciaLog> getMeusLogs(String hospitalEmail) {
        Hospital hospital = getMeuPerfil(hospitalEmail);
        return emergenciaLogRepository.findByIdHospital(hospital.getIdHospital());
    }
}