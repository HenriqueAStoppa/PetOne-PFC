package com.petone.petone.service;

import com.petone.petone.dto.ResetarSenhaRequestDTO;
import com.petone.petone.model.Hospital;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.repository.TutorRepository;
import com.petone.petone.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Novo Serviço dedicado à lógica de "Esqueci minha senha"
 * para Tutores e Hospitais.
 */
@Service
public class PasswordResetService {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Etapa 1: Solicita a recuperação.
     * Encontra o usuário (Tutor ou Hospital) e envia o token.
     *
     * @param email Email do usuário.
     */
    public void solicitarReset(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Token expira em 1 hora

        // Tenta encontrar como Tutor
        var tutorOptional = tutorRepository.findByEmailTutor(email);
        if (tutorOptional.isPresent()) {
            Tutor tutor = tutorOptional.get();
            tutor.setResetToken(token);
            tutor.setResetTokenExpiry(expiryDate);
            tutorRepository.save(tutor);
            
            emailService.enviarEmailResetSenha(email, token);
            return; // Encontrou e processou como Tutor
        }

        // Se não for Tutor, tenta como Hospital
        var hospitalOptional = hospitalRepository.findByEmailHospital(email);
        if (hospitalOptional.isPresent()) {
            Hospital hospital = hospitalOptional.get();
            hospital.setResetToken(token);
            hospital.setResetTokenExpiry(expiryDate);
            hospitalRepository.save(hospital);

            emailService.enviarEmailResetSenha(email, token);
            return; // Encontrou e processou como Hospital
        }

        // Se não encontrou nenhum, lança erro
        throw new NoSuchElementException("Nenhum usuário (Tutor ou Hospital) encontrado com o email: " + email);
    }

    /**
     * Etapa 2: Reseta a senha usando o token.
     *
     * @param dto DTO contendo o token e a nova senha.
     */
    public void resetarSenha(ResetarSenhaRequestDTO dto) {
        String token = dto.getToken();

        // Tenta encontrar Tutor com o token
        var tutorOptional = tutorRepository.findByResetToken(token); // (Precisamos adicionar este método)
        if (tutorOptional.isPresent()) {
            Tutor tutor = tutorOptional.get();
            
            // Valida o token (não expirado)
            if (tutor.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token expirado.");
            }

            // Atualiza a senha
            tutor.setSenhaHash(PasswordUtil.encode(dto.getNovaSenha()));
            // Limpa o token
            tutor.setResetToken(null);
            tutor.setResetTokenExpiry(null);
            tutorRepository.save(tutor);
            return;
        }

        // Tenta encontrar Hospital com o token
        var hospitalOptional = hospitalRepository.findByResetToken(token); // (Precisamos adicionar este método)
        if (hospitalOptional.isPresent()) {
            Hospital hospital = hospitalOptional.get();

            // Valida o token (não expirado)
            if (hospital.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token expirado.");
            }

            // Atualiza a senha
            hospital.setSenhaHash(PasswordUtil.encode(dto.getNovaSenha()));
            // Limpa o token
            hospital.setResetToken(null);
            hospital.setResetTokenExpiry(null);
            hospitalRepository.save(hospital);
            return;
        }

        // Se não encontrou, o token é inválido
        throw new NoSuchElementException("Token inválido ou já utilizado.");
    }
}