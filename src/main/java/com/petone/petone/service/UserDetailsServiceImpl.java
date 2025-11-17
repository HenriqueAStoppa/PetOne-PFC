package com.petone.petone.service;

import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * [ARQUIVO CORRIGIDO]
 * Serviço que o Spring Security usa para carregar os detalhes de um usuário.
 * Agora entende tanto Tutores QUANTO Hospitais.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final TutorRepository tutorRepository;
    private final HospitalRepository hospitalRepository;

    /**
     * [CORREÇÃO 1: Injeção de Dependência via Construtor]
     * Injeta ambos os repositórios (Tutor e Hospital) para que não fiquem nulos.
     */
    @Autowired
    public UserDetailsServiceImpl(TutorRepository tutorRepository, HospitalRepository hospitalRepository) {
        this.tutorRepository = tutorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * [CORREÇÃO 2: Lógica Unificada]
     * Carrega o usuário (seja Tutor ou Hospital) pelo email em um único método.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Tenta encontrar como Tutor
        var tutorOptional = tutorRepository.findByEmailTutor(email);
        if (tutorOptional.isPresent()) {
            var tutor = tutorOptional.get();
            // Retorna um User do Spring Security
            return new User(tutor.getEmailTutor(), tutor.getSenhaHash(), new ArrayList<>());
        }

        // 2. Se não for Tutor, tenta encontrar como Hospital
        var hospitalOptional = hospitalRepository.findByEmailHospital(email);
        if (hospitalOptional.isPresent()) {
            var hospital = hospitalOptional.get();
            // Retorna um User do Spring Security
            return new User(hospital.getEmailHospital(), hospital.getSenhaHash(), new ArrayList<>());
        }

        // 3. Se não for nenhum dos dois, lança o erro
        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }
}