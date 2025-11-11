package com.petone.petone.service;

import com.petone.petone.dto.EmergenciaRequestDTO;
import com.petone.petone.dto.EmergenciaResponseDTO;
import com.petone.petone.dto.FinalizacaoRequestDTO; // NOVO IMPORT
import com.petone.petone.model.Animal;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Hospital;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.AnimalRepository;
import com.petone.petone.repository.EmergenciaLogRepository;
import com.petone.petone.repository.HospitalRepository; // NOVO IMPORT
import com.petone.petone.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // NOVO IMPORT
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * [ARQUIVO ATUALIZADO]
 * Serviço que orquestra o fluxo de emergência (Início e Finalização).
 */
@Service
public class EmergenciaService {

    private final EmergenciaLogRepository emergenciaLogRepository;
    private final TutorRepository tutorRepository;
    private final AnimalRepository animalRepository;
    private final MapsService mapsService;
    private final EmailService emailService;
    private final HospitalRepository hospitalRepository; // NOVO CAMPO

    @Autowired
    public EmergenciaService(EmergenciaLogRepository emergenciaLogRepository,
                             TutorRepository tutorRepository,
                             AnimalRepository animalRepository,
                             MapsService mapsService,
                             EmailService emailService,
                             HospitalRepository hospitalRepository) { // NOVO PARÂMETRO
        this.emergenciaLogRepository = emergenciaLogRepository;
        this.tutorRepository = tutorRepository;
        this.animalRepository = animalRepository;
        this.mapsService = mapsService;
        this.emailService = emailService;
        this.hospitalRepository = hospitalRepository; // NOVA ATRIBUIÇÃO
    }

    /**
     * Inicia um novo registro de emergência.
     */
    public EmergenciaResponseDTO iniciarEmergencia(EmergenciaRequestDTO dto, String tutorEmail) throws AccessDeniedException {
        
        // 1. Validar Entidades (Tutor e Animal)
        Tutor tutor = tutorRepository.findByEmailTutor(tutorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor não encontrado: " + tutorEmail));
        
        Animal animal = animalRepository.findById(dto.getIdAnimal())
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado: " + dto.getIdAnimal()));

        // 2. Verificar Propriedade (Segurança)
        if (!animal.getIdTutor().equals(tutor.getIdTutor())) {
            throw new AccessDeniedException("Este animal não pertence ao tutor logado.");
        }

        // 3. Encontrar Hospital (Usando o serviço simulado)
        Hospital hospital = mapsService.encontrarHospitalMaisProximo(dto.getTipoEmergencia());

        // 4. Gerar Token e Criar o Log
        String token = "VET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EmergenciaLog log = EmergenciaLog.builder()
                .dataHoraInicio(LocalDateTime.now())
                .tokenEmergencia(token)
                .idHospital(hospital.getIdHospital())
                .nomeFantasiaHospital(hospital.getNomeFantasia())
                .telefoneHospital(hospital.getTelefoneHospital())
                .emailHospital(hospital.getEmailHospital())
                .idTutor(tutor.getIdTutor())
                .nomeCompletoTutor(tutor.getNomeCompleto())
                .telefoneTutor(tutor.getTelefoneTutor())
                .emailTutor(tutor.getEmailTutor())
                .idAnimal(animal.getIdAnimal())
                .nomeAnimal(animal.getNomeAnimal())
                .idadeAnimal(animal.getIdade())
                .especieAnimal(animal.getEspecie())
                .racaAnimal(animal.getRaca())
                .sexoAnimal(animal.getSexo())
                .tipoEmergencia(dto.getTipoEmergencia())
                .build();
        
        // 5. Salvar o Log no Banco
        emergenciaLogRepository.save(log);

        // 6. Enviar Emails (Usando o serviço simulado)
        emailService.enviarEmailTokenTutor(tutor, token, hospital);
        emailService.enviarEmailTokenHospital(hospital, token, tutor);

        // 7. Preparar Resposta para o Frontend
        return EmergenciaResponseDTO.builder()
                .tokenEmergencia(token)
                .hospitalEncontrado(hospital)
                .mensagem("Emergência registrada. Siga para " + hospital.getNomeFantasia())
                .build();
    }

    // --- [NOVO MÉTODO DE FINALIZAÇÃO] ---

    /**
     * Finaliza um atendimento de emergência, adicionando relatório e prescrição.
     * @param token O token da emergência (VET-ABC-123).
     * @param dto Os dados do formulário de finalização (do DTO FinalizacaoRequestDTO).
     * @param hospitalEmail O email do hospital logado (do JWT).
     * @return O Log atualizado.
     * @throws AccessDeniedException Se o log não pertencer ao hospital logado.
     */
    public EmergenciaLog finalizarEmergencia(String token, FinalizacaoRequestDTO dto, String hospitalEmail) throws AccessDeniedException {
        
        // 1. Buscar o hospital logado
        Hospital hospitalLogado = hospitalRepository.findByEmailHospital(hospitalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital (usuário) não encontrado: " + hospitalEmail));

        // 2. Buscar o log de emergência pelo token
        EmergenciaLog log = emergenciaLogRepository.findByTokenEmergencia(token)
                .orElseThrow(() -> new NoSuchElementException("Log de emergência não encontrado com o token: " + token));

        // 3. [SEGURANÇA] Verificar se o hospital logado é o dono deste log
        if (!log.getIdHospital().equals(hospitalLogado.getIdHospital())) {
            throw new AccessDeniedException("Este log de emergência não pertence ao seu hospital.");
        }

        // 4. Atualizar os campos do Log (usando os campos do EmergenciaLog.java)
        log.setRelatorio(dto.getRelatorio());
        log.setPrescricao(dto.getPrescricao());
        log.setVeterinarioResponsavelFinalizacao(dto.getVeterinarioResponsavelFinalizacao());
        log.setCrmvVeterinarioFinalizacao(dto.getCrmvVeterinarioFinalizacao());
        
        log.setDataHoraFim(LocalDateTime.now());
        // (Você pode adicionar um campo 'status' no modelo se quiser, ex: log.setStatus("Finalizado"))

        // 5. Salvar o log atualizado
        return emergenciaLogRepository.save(log);
    }
}