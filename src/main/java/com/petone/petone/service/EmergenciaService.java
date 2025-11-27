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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

                // Busca tutor logado
                Tutor tutor = tutorRepository.findByEmailTutor(tutorEmail)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Tutor não encontrado: " + tutorEmail));

                // Busca animal
                Animal animal = animalRepository.findById(dto.getIdAnimal())
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Animal não encontrado: " + dto.getIdAnimal()));

                // Garante que o animal é do tutor logado
                if (!animal.getIdTutor().equals(tutor.getIdTutor())) {
                        throw new AccessDeniedException("Este animal não pertence ao tutor logado.");
                }

                // Hospital mais próximo
                Hospital hospital = mapsService.encontrarHospitalMaisProximo("localizacao_simulada");

                // Gera token da emergência
                String token = "VET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                // Data e hora
                LocalDateTime agora = LocalDateTime.now();

                // Criação do Log usando Builder
                EmergenciaLog log = EmergenciaLog.builder()
                                .tokenEmergencia(token)
                                .dataHoraInicio(agora)
                                .dataHoraRegistro(agora)
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

                                .idHospital(hospital.getIdHospital())
                                .nomeFantasiaHospital(hospital.getNomeFantasia())
                                .emailHospital(hospital.getEmailHospital())
                                .telefoneHospital(hospital.getTelefoneHospital())

                                .veterinarioResponsavelFinalizacao(hospital.getVeterinarioResponsavel())
                                .crmvVeterinarioFinalizacao(hospital.getCrmvVeterinario())
                                .build();

                emergenciaLogRepository.save(log);

                // Envia email
                emailService.enviarEmailTokenParaTutor(tutor, log);
                emailService.enviarEmailAlertaParaHospital(log);

                // Monta o objeto de resposta para o front
                return EmergenciaResponseDTO.builder()
                                .tokenEmergencia(token)
                                .hospitalNome(hospital.getNomeFantasia())
                                .hospitalEndereco(hospital.getEndereco())
                                .mensagem("Emergência registrada. Siga para o hospital.")
                                .dataHoraRegistro(agora)
                                .build();
        }

        public EmergenciaLog finalizarEmergencia(String tokenEmergencia,
                        FinalizacaoRequestDTO dto,
                        String hospitalEmail) {

                Hospital hospital = hospitalRepository.findByEmailHospital(hospitalEmail)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Hospital não encontrado: " + hospitalEmail));

                EmergenciaLog log = emergenciaLogRepository.findByTokenEmergencia(tokenEmergencia)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Log não encontrado: " + tokenEmergencia));

                if (!log.getIdHospital().equals(hospital.getIdHospital())) {
                        throw new AccessDeniedException("Este log não pertence ao seu hospital.");
                }

                // Atualiza campos finais
                log.setStatus("Finalizado");
                log.setRelatorio(dto.getRelatorio());
                log.setPrescricao(dto.getPrescricao());
                log.setVeterinarioResponsavelFinalizacao(dto.getVeterinarioResponsavelFinalizacao());
                log.setCrmvVeterinarioFinalizacao(dto.getCrmvVeterinarioFinalizacao());
                log.setDataHoraFim(LocalDateTime.now());

                return emergenciaLogRepository.save(log);
        }
}
