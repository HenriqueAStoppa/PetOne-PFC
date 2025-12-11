package com.petone.petone.service;

import com.petone.petone.dto.EmergenciaRequestDTO;
import com.petone.petone.dto.EmergenciaResponseDTO;
import com.petone.petone.dto.FinalizacaoRequestDTO;
import com.petone.petone.model.Animal;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Hospital;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.AnimalRepository;
import com.petone.petone.repository.EmergenciaLogRepository;
import com.petone.petone.repository.HospitalRepository;
import com.petone.petone.repository.TutorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergenciaServiceTest {

    @Mock
    private EmergenciaLogRepository emergenciaLogRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private MapsService mapsService;

    @Mock
    private EmailService emailService;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private EmergenciaService emergenciaService;

    private static final String EMAIL_TUTOR = "tutor@teste.com";
    private static final String EMAIL_HOSPITAL = "hospital@teste.com";

    private Tutor criarTutor() {
        Tutor t = new Tutor();
        t.setIdTutor("T1");
        t.setNomeCompleto("Levi Tutor");
        t.setEmailTutor(EMAIL_TUTOR);
        t.setTelefoneTutor("1199999-9999");
        return t;
    }

    private Animal criarAnimal(String idTutor) {
        Animal a = new Animal();
        a.setIdAnimal("A1");
        a.setIdTutor(idTutor);
        a.setNomeAnimal("Bob");
        a.setEspecie("Cão");
        a.setRaca("Poodle");
        return a;
    }

    private Hospital criarHospital() {
        Hospital h = new Hospital();
        h.setIdHospital("H1");
        h.setNomeFantasia("Hospital VetOne");
        h.setEmailHospital(EMAIL_HOSPITAL);
        h.setTelefoneHospital("113333-3333");
        h.setEndereco("Rua X, 123 - Centro, SP - SP");
        h.setVeterinarioResponsavel("Dr. Vet");
        h.setCrmvVeterinario("CRMV-12345");
        return h;
    }

    private EmergenciaRequestDTO criarEmergenciaRequest() {
        EmergenciaRequestDTO dto = new EmergenciaRequestDTO();
        dto.setIdAnimal("A1");
        dto.setLatitudeTutor(-23.0);
        dto.setLongitudeTutor(-46.0);
        dto.setTipoEmergencia("Convulsão");
        return dto;
    }

    private FinalizacaoRequestDTO criarFinalizacaoRequest() {
        FinalizacaoRequestDTO dto = new FinalizacaoRequestDTO();
        dto.setRelatorio("Animal recuperado, alta com medicação.");
        dto.setPrescricao("Dipirona 5 dias");
        dto.setVeterinarioResponsavelFinalizacao("Dr. João");
        dto.setCrmvVeterinarioFinalizacao("CRMV-9999");
        return dto;
    }

    // Testes de iniciarEmergencia

    @Test
    @DisplayName("iniciarEmergencia deve criar log, salvar no repositório e enviar e-mails")
    void iniciarEmergencia_deveCriarLogEEnviarEmails() {
        Tutor tutor = criarTutor();
        Animal animal = criarAnimal(tutor.getIdTutor());
        Hospital hospital = criarHospital();
        EmergenciaRequestDTO dto = criarEmergenciaRequest();

        when(tutorRepository.findByEmailTutor(EMAIL_TUTOR)).thenReturn(Optional.of(tutor));
        when(animalRepository.findById(dto.getIdAnimal())).thenReturn(Optional.of(animal));
        when(mapsService.encontrarHospitalMaisProximo(dto.getLatitudeTutor(), dto.getLongitudeTutor()))
                .thenReturn(hospital);

        // captura o log salvo para poder inspecionar depois
        ArgumentCaptor<EmergenciaLog> logCaptor = ArgumentCaptor.forClass(EmergenciaLog.class);
        when(emergenciaLogRepository.save(any(EmergenciaLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmergenciaResponseDTO response = emergenciaService.iniciarEmergencia(dto, EMAIL_TUTOR);

        assertNotNull(response);
        assertNotNull(response.getTokenEmergencia(), "Token não pode ser nulo");
        assertTrue(response.getTokenEmergencia().startsWith("VET-"));
        assertEquals(hospital.getNomeFantasia(), response.getHospitalNome());
        assertEquals(hospital.getEndereco(), response.getHospitalEndereco());
        assertEquals("Emergência registrada. Siga para o hospital.", response.getMensagem());
        assertNotNull(response.getDataHoraRegistro());

        verify(emergenciaLogRepository).save(logCaptor.capture());
        EmergenciaLog salvo = logCaptor.getValue();

        assertEquals(response.getTokenEmergencia(), salvo.getTokenEmergencia());
        assertEquals("Encaminhado", salvo.getStatus());
        assertEquals(dto.getTipoEmergencia(), salvo.getTipoEmergencia());

        assertEquals(tutor.getIdTutor(), salvo.getIdTutor());
        assertEquals(tutor.getNomeCompleto(), salvo.getNomeCompletoTutor());
        assertEquals(tutor.getTelefoneTutor(), salvo.getTelefoneTutor());
        assertEquals(tutor.getEmailTutor(), salvo.getEmailTutor());

        assertEquals(animal.getIdAnimal(), salvo.getIdAnimal());
        assertEquals(animal.getNomeAnimal(), salvo.getNomeAnimal());
        assertEquals(animal.getEspecie(), salvo.getEspecieAnimal());
        assertEquals(animal.getRaca(), salvo.getRacaAnimal());

        assertEquals(hospital.getIdHospital(), salvo.getIdHospital());
        assertEquals(hospital.getNomeFantasia(), salvo.getNomeFantasiaHospital());
        assertEquals(hospital.getEmailHospital(), salvo.getEmailHospital());
        assertEquals(hospital.getTelefoneHospital(), salvo.getTelefoneHospital());

        // emails enviados
        verify(emailService).enviarEmailTokenParaTutor(tutor, salvo);
        verify(emailService).enviarEmailAlertaParaHospital(salvo);
    }

    @Test
    @DisplayName("iniciarEmergencia deve lançar AccessDenied quando o animal não pertence ao tutor logado")
    void iniciarEmergencia_deveLancarAccessDeniedQuandoAnimalNaoPertenceTutor() {
        Tutor tutor = criarTutor();
        Animal animal = criarAnimal("OUTRO_TUTOR");
        EmergenciaRequestDTO dto = criarEmergenciaRequest();

        when(tutorRepository.findByEmailTutor(EMAIL_TUTOR)).thenReturn(Optional.of(tutor));
        when(animalRepository.findById(dto.getIdAnimal())).thenReturn(Optional.of(animal));

        assertThrows(AccessDeniedException.class,
                () -> emergenciaService.iniciarEmergencia(dto, EMAIL_TUTOR));

        // não deve chamar hospital, salvar log nem enviar emails
        verifyNoInteractions(mapsService);
        verify(emergenciaLogRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("iniciarEmergencia deve lançar UsernameNotFoundException quando tutor não existe")
    void iniciarEmergencia_deveLancarQuandoTutorNaoExiste() {
        EmergenciaRequestDTO dto = criarEmergenciaRequest();
        when(tutorRepository.findByEmailTutor(EMAIL_TUTOR)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> emergenciaService.iniciarEmergencia(dto, EMAIL_TUTOR));

        verifyNoInteractions(animalRepository, mapsService, emailService, emergenciaLogRepository);
    }

    // Testes de finalizarEmergencia

    @Test
    @DisplayName("finalizarEmergencia deve atualizar log quando hospital é dono do registro")
    void finalizarEmergencia_deveAtualizarLogQuandoHospitalCorreto() {
        Hospital hospital = criarHospital();

        EmergenciaLog log = EmergenciaLog.builder()
                .idHospital(hospital.getIdHospital())
                .status("Encaminhado")
                .build();

        FinalizacaoRequestDTO dto = criarFinalizacaoRequest();

        when(hospitalRepository.findByEmailHospital(EMAIL_HOSPITAL)).thenReturn(Optional.of(hospital));
        when(emergenciaLogRepository.findByTokenEmergencia("TOKEN123")).thenReturn(Optional.of(log));
        when(emergenciaLogRepository.save(any(EmergenciaLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmergenciaLog atualizado = emergenciaService.finalizarEmergencia("TOKEN123", dto, EMAIL_HOSPITAL);

        assertEquals("Finalizado", atualizado.getStatus());
        assertEquals(dto.getRelatorio(), atualizado.getRelatorio());
        assertEquals(dto.getPrescricao(), atualizado.getPrescricao());
        assertEquals(dto.getVeterinarioResponsavelFinalizacao(),
                atualizado.getVeterinarioResponsavelFinalizacao());
        assertEquals(dto.getCrmvVeterinarioFinalizacao(),
                atualizado.getCrmvVeterinarioFinalizacao());
        assertNotNull(atualizado.getDataHoraFim());
    }

    @Test
    @DisplayName("finalizarEmergencia deve lançar AccessDenied quando hospital não é dono do log")
    void finalizarEmergencia_deveLancarAccessDeniedQuandoHospitalNaoDonoDoLog() {
        Hospital hospital = criarHospital();
        hospital.setIdHospital("H1");

        EmergenciaLog log = EmergenciaLog.builder()
                .idHospital("OUTRO_HOSPITAL")
                .status("Encaminhado")
                .build();

        FinalizacaoRequestDTO dto = criarFinalizacaoRequest();

        when(hospitalRepository.findByEmailHospital(EMAIL_HOSPITAL)).thenReturn(Optional.of(hospital));
        when(emergenciaLogRepository.findByTokenEmergencia("TOKEN123")).thenReturn(Optional.of(log));

        assertThrows(AccessDeniedException.class,
                () -> emergenciaService.finalizarEmergencia("TOKEN123", dto, EMAIL_HOSPITAL));

        verify(emergenciaLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("finalizarEmergencia deve lançar NoSuchElementException quando token não existe")
    void finalizarEmergencia_deveLancarQuandoTokenNaoExiste() {
        Hospital hospital = criarHospital();
        FinalizacaoRequestDTO dto = criarFinalizacaoRequest();

        when(hospitalRepository.findByEmailHospital(EMAIL_HOSPITAL)).thenReturn(Optional.of(hospital));
        when(emergenciaLogRepository.findByTokenEmergencia("TOKEN_INEXISTENTE"))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> emergenciaService.finalizarEmergencia("TOKEN_INEXISTENTE", dto, EMAIL_HOSPITAL));

        verify(emergenciaLogRepository, never()).save(any());
    }
}
