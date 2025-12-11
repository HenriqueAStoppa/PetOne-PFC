package com.petone.petone.service;

import com.petone.petone.model.Hospital;
import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_quandoEmailPertenceAoHospital_deveRetornarUserDetailsComRoleHospital() {
        String emailOriginal = "HOSPITAL@TESTE.COM";
        String emailNormalizado = "hospital@teste.com";

        Hospital hospital = new Hospital();
        hospital.setEmailHospital(emailNormalizado);
        hospital.setSenhaHash("hash-senha-hospital");

        when(tutorRepository.findByEmailTutorIgnoreCase(anyString()))
                .thenReturn(Optional.empty());
        when(hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(Optional.of(hospital));

        UserDetails userDetails = userDetailsService.loadUserByUsername(emailOriginal);

        assertNotNull(userDetails);
        assertEquals(emailNormalizado, userDetails.getUsername());
        assertEquals("hash-senha-hospital", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_HOSPITAL", authorities.iterator().next().getAuthority());

        verify(tutorRepository).findByEmailTutorIgnoreCase(emailNormalizado);
        verify(hospitalRepository).findByEmailHospitalIgnoreCase(emailNormalizado);
    }

    @Test
    void loadUserByUsername_quandoEmailNaoExiste_deveLancarUsernameNotFoundException() {
        String emailOriginal = "naoexiste@teste.com";
        String emailNormalizado = "naoexiste@teste.com";

        when(tutorRepository.findByEmailTutorIgnoreCase(emailNormalizado))
                .thenReturn(Optional.empty());
        when(hospitalRepository.findByEmailHospitalIgnoreCase(emailNormalizado))
                .thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(emailOriginal));

        assertTrue(ex.getMessage().contains(emailOriginal));

        verify(tutorRepository).findByEmailTutorIgnoreCase(emailNormalizado);
        verify(hospitalRepository).findByEmailHospitalIgnoreCase(emailNormalizado);
    }
}
