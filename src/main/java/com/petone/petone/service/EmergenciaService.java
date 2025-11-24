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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
// Import correto para segurança
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class EmergenciaService {

    private final EmergenciaLogRepository emergenciaLogRepository;
    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;
    private final MapsService mapsService;
    private final EmailService emailService;
    private final HospitalRepository hospitalRepository; 

    @Autowired
    public EmergenciaService(EmergenciaLogRepository emergenciaLogRepository, 
                             TutorRepository tutorRepository, 
                             AnimalRepository animalRepository, 
                             MapsService mapsService, 
                             EmailService emailService,
                             HospitalRepository hospitalRepository) {
        this.emergenciaLogRepository = emergenciaLogRepository;
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
        this.mapsService = mapsService;
        this.emailService = emailService;
        this.hospitalRepository = hospitalRepository; 
    }

    public EmergenciaResponseDTO iniciarEmergencia(EmergenciaRequestDTO dto, String tutorEmail) {
        
        Tutor tutor = tutorRepository.findByEmailTutor(tutorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor não encontrado: " + tutorEmail));
        
        Animal animal = animalRepository.findById(dto.getIdAnimal())
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado: " + dto.getIdAnimal()));

        if (!animal.getIdTutor().equals(tutor.getIdTutor())) {
            throw new AccessDeniedException("Este animal não pertence ao tutor logado.");
        }

        Hospital hospital = mapsService.encontrarHospitalMaisProximo("localizacao_simulada"); 
        String token = "VET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Criação do Log usando Builder (garante que os campos batem com o Modelo)
        EmergenciaLog log = EmergenciaLog.builder()
                .tokenEmergencia(token)
                .dataHoraRegistro(LocalDateTime.now())
                .status("Encaminhado")
                .tipoEmergencia(dto.getTipoEmergencia())
                .idAnimal(animal.getIdAnimal())
                .nomeAnimal(animal.getNomeAnimal())
                .especieAnimal(animal.getEspecie())
                .racaAnimal(animal.getRaca())
                .idTutor(tutor.getIdTutor())
                .nomeCompletoTutor(tutor.getNomeCompleto())
                .telefoneTutor(tutor.getTelefoneTutor())
                .emailTutor(tutor.getEmailTutor())
                .idHospitalEncaminhado(hospital.getIdHospital())
                .nomeFantasiaHospital(hospital.getNomeFantasia())
                .emailHospital(hospital.getEmailHospital())
                .telefoneHospital(hospital.getTelefoneHospital())
                .veterinarioResponsavel(hospital.getVeterinarioResponsavel())
                .crmvVeterinario(hospital.getCrmvVeterinario())
                .build();
        
        emergenciaLogRepository.save(log);

        // Chamada correta para os emails (2 argumentos)
        emailService.enviarEmailTokenParaTutor(tutor, log);
        emailService.enviarEmailAlertaParaHospital(log);

        return EmergenciaResponseDTO.builder()
                .tokenEmergencia(token)
                .hospitalNome(hospital.getNomeFantasia())
                .hospitalEndereco(hospital.getEndereco())
                .mensagem("Emergência registrada. Siga para o hospital.")
                .build();
    }

    public EmergenciaLog finalizarEmergencia(String tokenEmergencia, FinalizacaoRequestDTO dto, String hospitalEmail) {
        Hospital hospital = hospitalRepository.findByEmailHospital(hospitalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital não encontrado: " + hospitalEmail));

        EmergenciaLog log = emergenciaLogRepository.findByTokenEmergencia(tokenEmergencia)
                .orElseThrow(() -> new NoSuchElementException("Log não encontrado: " + tokenEmergencia));

        if (!log.getIdHospitalEncaminhado().equals(hospital.getIdHospital())) {
            throw new AccessDeniedException("Este log não pertence ao seu hospital.");
        }

        // Atualização dos campos finais
        log.setStatus("Finalizado");
        log.setRelatorioMedico(dto.getRelatorio());
        log.setPrescricaoMedicamento(dto.getPrescricao());
        log.setVeterinarioFinalizacao(dto.getVeterinarioResponsavelFinalizacao());
        log.setCrmvFinalizacao(dto.getCrmvVeterinarioFinalizacao());

        return emergenciaLogRepository.save(log);
    }
}