package com.petone.petone.service;

import com.petone.petone.dto.AuthResponseDTO;
import com.petone.petone.dto.TutorCadastroDTO;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.AnimalRepository;
import com.petone.petone.repository.EmergenciaLogRepository;
import com.petone.petone.repository.TutorRepository;
import com.petone.petone.util.JwtUtil;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private EmergenciaLogRepository emergenciaLogRepository;

    @InjectMocks
    private TutorService tutorService;

    @Test
    void naoDevePermitirCadastroDeTutorMenorDe18Anos() {

        TutorCadastroDTO dto = new TutorCadastroDTO();
        dto.setNomeCompleto("Menor de Idade");
        dto.setCpf("000.000.000-00");
        dto.setEmailTutor("menor@teste.com");
        dto.setTelefoneTutor("11999999999");
        // 17 anos
        dto.setDataNascimento(LocalDate.now().minusYears(17));
        dto.setSenha("senha123");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> tutorService.cadastrarTutor(dto));

        assertEquals("O tutor deve ter pelo menos 18 anos.", ex.getMessage());
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void naoDevePermitirCadastroComEmailJaExistenteIgnorandoCase() {
        // arrange
        TutorCadastroDTO dto = new TutorCadastroDTO();
        dto.setNomeCompleto("Usuário Teste");
        dto.setCpf("111.111.111-11");
        dto.setEmailTutor("Teste@Email.com"); // misturado maiúsculo/minúsculo
        dto.setTelefoneTutor("11999999999");
        dto.setDataNascimento(LocalDate.now().minusYears(25));
        dto.setSenha("senha123");

        String emailNormalizado = "teste@email.com";

        Tutor existente = new Tutor();
        existente.setIdTutor("123");
        existente.setEmailTutor(emailNormalizado);

        when(tutorRepository.findByEmailTutor(emailNormalizado))
                .thenReturn(Optional.of(existente));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> tutorService.cadastrarTutor(dto));

        assertEquals("Email já cadastrado.", ex.getMessage());
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void deveCadastrarTutorComEmailNormalizadoParaMinusculo() {
        TutorCadastroDTO dto = new TutorCadastroDTO();
        dto.setNomeCompleto("Usuário Teste");
        dto.setCpf("111.111.111-11");
        dto.setEmailTutor("Teste@Email.com");
        dto.setTelefoneTutor("11999999999");
        dto.setDataNascimento(LocalDate.now().minusYears(25));
        dto.setSenha("senha123");

        when(tutorRepository.findByEmailTutor("teste@email.com"))
                .thenReturn(Optional.empty());

        when(tutorRepository.save(any(Tutor.class))).thenAnswer(invocation -> {
            Tutor t = invocation.getArgument(0);
            t.setIdTutor("abc123");
            return t;
        });

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("teste@email.com"))
                .thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        AuthResponseDTO resp = tutorService.cadastrarTutor(dto);

        assertEquals("abc123", resp.getIdTutor());
        assertEquals("teste@email.com", resp.getEmail());
        assertEquals("Usuário Teste", resp.getNomeCompleto());
        assertEquals("fake-jwt-token", resp.getToken());

        ArgumentCaptor<Tutor> captor = ArgumentCaptor.forClass(Tutor.class);
        verify(tutorRepository).save(captor.capture());
        Tutor salvo = captor.getValue();
        assertEquals("teste@email.com", salvo.getEmailTutor());
    }
}
