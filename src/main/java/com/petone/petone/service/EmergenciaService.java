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
// [CORREÇÃO] Usar exceção de segurança em vez de arquivo
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Serviço principal para orquestrar o fluxo de emergência.
 * (Versão atualizada com Finalização)
 */
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

    /**
     * Inicia um novo registro de emergência.
     */
    public EmergenciaResponseDTO iniciarEmergencia(EmergenciaRequestDTO dto, String tutorEmail) throws AccessDeniedException {
        
        // 1. Validar Entidades
        Tutor tutor = tutorRepository.findByEmailTutor(tutorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor não encontrado: " + tutorEmail));
        
        Animal animal = animalRepository.findById(dto.getIdAnimal())
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado: " + dto.getIdAnimal()));

        // 2. Verificar Propriedade (Segurança)
        if (!animal.getIdTutor().equals(tutor.getIdTutor())) {
            throw new AccessDeniedException("Este animal não pertence ao tutor logado.");
        }

        // 3. Encontrar Hospital (Usando o serviço simulado)
        // [CORREÇÃO] O DTO não tem mais localização, passamos um valor simulado
        Hospital hospital = mapsService.encontrarHospitalMaisProximo("localizacao_simulada_do_tutor"); 

        // 4. Gerar Token e Criar o Log
        String token = "VET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Usando o Builder do Lombok para criar o objeto de forma segura e limpa
        EmergenciaLog log = EmergenciaLog.builder()
                .tokenEmergencia(token)
                .dataHoraRegistro(LocalDateTime.now())
                .status("Encaminhado")
                
                // [CORREÇÃO] Usar o campo correto do DTO
                .tipoEmergencia(dto.getTipoEmergencia())
                
                // Dados do Animal
                .idAnimal(animal.getIdAnimal())
                .nomeAnimal(animal.getNomeAnimal())
                .especieAnimal(animal.getEspecie())
                .racaAnimal(animal.getRaca())

                // Dados do Tutor
                .idTutor(tutor.getIdTutor())
                .nomeCompletoTutor(tutor.getNomeCompleto())
                .telefoneTutor(tutor.getTelefoneTutor())
                .emailTutor(tutor.getEmailTutor())
                
                // Dados do Hospital
                // [CORREÇÃO] O nome correto do campo no Model é 'idHospitalEncaminhado'
                .idHospitalEncaminhado(hospital.getIdHospital()) 
                .nomeFantasiaHospital(hospital.getNomeFantasia())
                .emailHospital(hospital.getEmailHospital())
                .telefoneHospital(hospital.getTelefoneHospital())
                .veterinarioResponsavel(hospital.getVeterinarioResponsavel())
                .crmvVeterinario(hospital.getCrmvVeterinario())
                .build();
        
        // 5. Salvar o Log no Banco
        emergenciaLogRepository.save(log);

        // 6. Enviar Emails (Usando o serviço simulado)
        emailService.enviarEmailTokenParaTutor(tutor, log);
        emailService.enviarEmailAlertaParaHospital(log);

        // 7. Preparar Resposta para o Frontend
        return EmergenciaResponseDTO.builder()
                .tokenEmergencia(token)
                .hospitalNome(hospital.getNomeFantasia()) // [CORREÇÃO] Usar hospitalNome em vez de hospitalEncontrado
                .hospitalEndereco(hospital.getEndereco()) // [CORREÇÃO] Adicionado endereço
                .mensagem("Emergência registrada. Siga para o hospital.")
                .build();
    }

    /**
     * Finaliza um atendimento de emergência (Ação do Hospital).
     */
    public EmergenciaLog finalizarEmergencia(String tokenEmergencia, FinalizacaoRequestDTO dto, String hospitalEmail) throws AccessDeniedException {
        // 1. Busca o hospital logado
        Hospital hospital = hospitalRepository.findByEmailHospital(hospitalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Hospital não encontrado: " + hospitalEmail));

        // 2. Busca o log de emergência
        EmergenciaLog log = emergenciaLogRepository.findByTokenEmergencia(tokenEmergencia)
                .orElseThrow(() -> new NoSuchElementException("Log de emergência não encontrado com o token: " + tokenEmergencia));

        // 3. Validação de Segurança: O log pertence a este hospital?
        if (!log.getIdHospitalEncaminhado().equals(hospital.getIdHospital())) {
            throw new AccessDeniedException("Este log de emergência não pertence ao seu hospital.");
        }

        // 4. Atualiza os campos do log
        log.setStatus("Finalizado");
        log.setRelatorioMedico(dto.getRelatorio());
        log.setPrescricaoMedicamento(dto.getPrescricao());
        log.setVeterinarioFinalizacao(dto.getVeterinarioResponsavelFinalizacao());
        log.setCrmvFinalizacao(dto.getCrmvVeterinarioFinalizacao());

        // 5. Salvar o log atualizado
        return emergenciaLogRepository.save(log);
    }
}