package com.petone.petone.service;

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.TutorCadastroDTO;
import com.petone.petone.dto.TutorPerfilDTO;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.AnimalRepository;
import com.petone.petone.repository.EmergenciaLogRepository;
import com.petone.petone.repository.TutorRepository;
import com.petone.petone.util.JwtUtil;
import com.petone.petone.util.PasswordUtil;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AnimalRepository animalRepository;
    private final EmergenciaLogRepository emergenciaLogRepository;

    @Autowired
    public TutorService(TutorRepository tutorRepository,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtUtil jwtUtil,
            AnimalRepository animalRepository,
            EmergenciaLogRepository emergenciaLogRepository) {
        this.tutorRepository = tutorRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.animalRepository = animalRepository;
        this.emergenciaLogRepository = emergenciaLogRepository;
    }

    public AuthResponseDTO cadastrarTutor(TutorCadastroDTO dto) {
        // 1) Regra de idade mínima 18 anos
        LocalDate hoje = LocalDate.now();
        int idade = Period.between(dto.getDataNascimento(), hoje).getYears();
        if (idade < 18) {
            throw new ValidationException("O tutor deve ter pelo menos 18 anos.");
        }

        // 2) Normalizar e-mail (remove espaços e deixa tudo minúsculo)
        String emailNormalizado = dto.getEmailTutor()
                .trim()
                .toLowerCase();

        // 3) Verificar duplicado usando o email normalizado
        if (tutorRepository.findByEmailTutor(emailNormalizado).isPresent()) {
            throw new ValidationException("Email já cadastrado.");
        }

        Tutor tutor = new Tutor();
        tutor.setNomeCompleto(dto.getNomeCompleto());
        tutor.setCpf(dto.getCpf());
        tutor.setEmailTutor(emailNormalizado); // sempre em minúsculo
        tutor.setTelefoneTutor(dto.getTelefoneTutor());
        dto.setEmailTutor(emailNormalizado); // garante consistência no DTO também
        tutor.setDataNascimento(dto.getDataNascimento());
        tutor.setSenhaHash(PasswordUtil.encode(dto.getSenha()));
        tutor.setEmailVerificado(true);

        Tutor tutorSalvo = tutorRepository.save(tutor);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(emailNormalizado);
        final String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(tutorSalvo.getIdTutor())
                .email(tutorSalvo.getEmailTutor())
                .nomeCompleto(tutorSalvo.getNomeCompleto())
                .build();
    }

    public AuthResponseDTO authenticate(AuthRequestDTO dto) throws Exception {
        String emailNormalizado = dto.getEmail()
                .trim()
                .toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNormalizado, dto.getSenha()));
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais inválidas", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(emailNormalizado);
        final String token = jwtUtil.generateToken(userDetails);

        Tutor tutor = tutorRepository.findByEmailTutorIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(tutor.getIdTutor())
                .email(tutor.getEmailTutor())
                .nomeCompleto(tutor.getNomeCompleto())
                .build();
    }

    public Tutor getMeuPerfil(String tutorEmail) {
        String emailNormalizado = tutorEmail.trim().toLowerCase(Locale.ROOT);
        return tutorRepository.findByEmailTutorIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + tutorEmail));
    }

    public Tutor updateMeuPerfil(String tutorEmail, TutorPerfilDTO dto) {
        Tutor tutor = getMeuPerfil(tutorEmail);
        tutor.setNomeCompleto(dto.getNomeCompleto());
        tutor.setTelefoneTutor(dto.getTelefoneTutor());
        tutor.setDataNascimento(dto.getDataNascimento());
        return tutorRepository.save(tutor);
    }

    @Transactional
    public void deleteMeuPerfil(String tutorEmail) {
        Tutor tutor = getMeuPerfil(tutorEmail);
        String idTutor = tutor.getIdTutor();
        animalRepository.deleteByIdTutor(idTutor);
        tutorRepository.delete(tutor);
    }

    public List<EmergenciaLog> getMeusLogs(String tutorEmail) {
        Tutor tutor = getMeuPerfil(tutorEmail);
        return emergenciaLogRepository.findByIdTutor(tutor.getIdTutor());
    }
}