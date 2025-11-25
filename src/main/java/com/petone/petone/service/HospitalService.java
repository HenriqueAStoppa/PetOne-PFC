package com.petone.petone.service; 

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.HospitalCadastroDTO;
import com.petone.petone.dto.HospitalPerfilDTO; 
import com.petone.petone.model.EmergenciaLog; 
import com.petone.petone.model.Hospital;
import com.petone.petone.repository.EmergenciaLogRepository; 
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
import java.util.List; 

//Serviço para a lógica de negócio do Hospital.
@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmergenciaLogRepository emergenciaLogRepository; 

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, 
                           JwtUtil jwtUtil, 
                           AuthenticationManager authenticationManager,
                           EmergenciaLogRepository emergenciaLogRepository) { 
        this.hospitalRepository = hospitalRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emergenciaLogRepository = emergenciaLogRepository; 
    }

    //Cadastra um novo hospital.
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

    //Autentica um hospital usando o AuthenticationManager.
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
                .idTutor(hospital.getIdHospital()) 
                .email(hospital.getEmailHospital())
                .nomeCompleto(hospital.getNomeFantasia())
                .build();
    }

    public Hospital getMeuPerfil(String hospitalEmail) {
        return hospitalRepository.findByEmailHospital(hospitalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital não encontrado: " + hospitalEmail));
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