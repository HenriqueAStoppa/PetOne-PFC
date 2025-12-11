package com.petone.petone.service;

import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.repository.TutorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final TutorRepository tutorRepository;
    private final HospitalRepository hospitalRepository;

    public UserDetailsServiceImpl(TutorRepository tutorRepository, HospitalRepository hospitalRepository) {
        this.tutorRepository = tutorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String emailNormalizado = email.trim().toLowerCase();

        var tutorOptional = tutorRepository.findByEmailTutorIgnoreCase(emailNormalizado);
        if (tutorOptional.isPresent()) {
            var tutor = tutorOptional.get();
            return new User(
                    tutor.getEmailTutor(),
                    tutor.getSenhaHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_TUTOR")));
        }

        var hospitalOptional = hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado);
        if (hospitalOptional.isPresent()) {
            var hospital = hospitalOptional.get();
            return new User(
                    hospital.getEmailHospital(),
                    hospital.getSenhaHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_HOSPITAL")));
        }

        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }
}