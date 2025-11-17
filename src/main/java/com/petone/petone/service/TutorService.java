package com.petone.petone.service;

import com.petone.petone.dto.AuthRequestDTO;
import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.TutorCadastroDTO;
import com.petone.petone.dto.TutorPerfilDTO;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.AnimalRepository;
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

/**
 * Serviço para a lógica de negócio do Tutor (Autenticação e Perfil).
 * (Versão completa e corrigida)
 */
@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AnimalRepository animalRepository;

    @Autowired
    public TutorService(TutorRepository tutorRepository, 
                        AuthenticationManager authenticationManager, 
                        UserDetailsService userDetailsService, 
                        JwtUtil jwtUtil, 
                        AnimalRepository animalRepository) {
        this.tutorRepository = tutorRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.animalRepository = animalRepository;
    }

    /**
     * Cadastra um novo tutor.
     */
    public AuthResponseDTO cadastrarTutor(TutorCadastroDTO dto) {
        if (tutorRepository.findByEmailTutor(dto.getEmailTutor()).isPresent()) {
            throw new ValidationException("Email já cadastrado.");
        }
        
        Tutor tutor = new Tutor();
        tutor.setNomeCompleto(dto.getNomeCompleto());
        tutor.setCpf(dto.getCpf());
        tutor.setEmailTutor(dto.getEmailTutor());
        tutor.setTelefoneTutor(dto.getTelefoneTutor());
        tutor.setDataNascimento(dto.getDataNascimento());
        tutor.setSenhaHash(PasswordUtil.encode(dto.getSenha()));
        tutor.setEmailVerificado(true); // Simulação TCC

        Tutor tutorSalvo = tutorRepository.save(tutor);

        // Gera token de login
        final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmailTutor());
        final String token = jwtUtil.generateToken(userDetails);
        
        // Retorna o DTO de resposta
        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(tutorSalvo.getIdTutor())
                .email(tutorSalvo.getEmailTutor())
                .nomeCompleto(tutorSalvo.getNomeCompleto())
                .build();
    }

    /**
     * Autentica um tutor.
     */
    public AuthResponseDTO authenticate(AuthRequestDTO dto) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciais inválidas", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        
        Tutor tutor = tutorRepository.findByEmailTutor(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        return AuthResponseDTO.builder()
                .token(token)
                .idTutor(tutor.getIdTutor())
                .email(tutor.getEmailTutor())
                .nomeCompleto(tutor.getNomeCompleto())
                .build();
    }

    // --- MÉTODOS (GERENCIAMENTO DE PERFIL) ---

    /**
     * Busca os dados do perfil do tutor logado.
     */
    public Tutor getMeuPerfil(String tutorEmail) {
        return tutorRepository.findByEmailTutor(tutorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + tutorEmail));
    }

    /**
     * Atualiza os dados do perfil do tutor logado.
     */
    public Tutor updateMeuPerfil(String tutorEmail, TutorPerfilDTO dto) {
        Tutor tutor = getMeuPerfil(tutorEmail); // Reusa o método anterior
        tutor.setNomeCompleto(dto.getNomeCompleto());
        tutor.setTelefoneTutor(dto.getTelefoneTutor());
        tutor.setDataNascimento(dto.getDataNascimento());
        return tutorRepository.save(tutor);
    }

    /**
     * Deleta o perfil do tutor logado e todos os seus animais.
     */
    @Transactional // Garante que tudo execute (ou falhe) em conjunto
    public void deleteMeuPerfil(String tutorEmail) {
        Tutor tutor = getMeuPerfil(tutorEmail);
        String idTutor = tutor.getIdTutor();

        // [CASCADE DELETE] Deleta os animais associados
        animalRepository.deleteByIdTutor(idTutor);

        // Deleta o tutor
        tutorRepository.delete(tutor);
    }
}