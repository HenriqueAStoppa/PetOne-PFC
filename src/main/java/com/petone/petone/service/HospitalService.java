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
import java.util.Locale;

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

        String emailNormalizado = dto.getEmailHospital()
                .trim()
                .toLowerCase();

        if (hospitalRepository.existsByEmailHospitalIgnoreCase(emailNormalizado)) {
            throw new ValidationException("Email já cadastrado.");
        }
        if (hospitalRepository.findByCnpj(dto.getCnpj()).isPresent()) {
            throw new ValidationException("CNPJ já cadastrado.");
        }

        Hospital hospital = new Hospital();
        hospital.setNomeFantasia(dto.getNomeFantasia());
        hospital.setEmailHospital(emailNormalizado);
        hospital.setTelefoneHospital(dto.getTelefoneHospital());

        String enderecoCompleto = String.format("%s, %s - %s, %s - %s",
                dto.getLogradouro(), dto.getNumero(), dto.getBairro(), dto.getCidade(), dto.getUf());
        hospital.setEndereco(enderecoCompleto);

        double[] coords = geocodingService.getCoordinates(
                dto.getLogradouro(), dto.getNumero(), dto.getCidade(), dto.getUf());
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

    public AuthResponseDTO authenticateHospital(AuthRequestDTO dto) throws Exception {
        String emailNormalizado = dto.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNormalizado, dto.getSenha()));
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais inválidas", e);
        }

        Hospital hospital = hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado)
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
        String emailNormalizado = hospitalEmail.trim().toLowerCase(Locale.ROOT);
        return hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado)
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

    public void deleteMeuPerfil(String hospitalEmail) {
        String emailNormalizado = hospitalEmail
                .trim()
                .toLowerCase(Locale.ROOT);

        Hospital hospital = hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital não encontrado para exclusão"));

        hospitalRepository.delete(hospital);
    }
}