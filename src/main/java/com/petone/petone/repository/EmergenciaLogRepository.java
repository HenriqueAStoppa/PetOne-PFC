package com.petone.petone.repository;

import com.petone.petone.model.EmergenciaLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

/**
 * [ARQUIVO ATUALIZADO]
 * Repositório para a entidade EmergenciaLog.
 * (Agora inclui busca por idTutor e tokenEmergencia).
 */
public interface EmergenciaLogRepository extends MongoRepository<EmergenciaLog, String> {
    
    /**
     * Busca todos os logs de emergência associados a um ID de hospital específico.
     */
    List<EmergenciaLog> findByIdHospital(String idHospital);

    /**
     * Busca todos os logs de emergência associados a um ID de tutor específico.
     */
    List<EmergenciaLog> findByIdTutor(String idTutor);

    /**
     * [MÉTODO SOLICITADO]
     * Busca um log de emergência pelo seu token único.
     * @param tokenEmergencia O token (ex: "VET-ABC-123")
     * @return Um Optional contendo o log.
     */
    Optional<EmergenciaLog> findByTokenEmergencia(String tokenEmergencia);
}