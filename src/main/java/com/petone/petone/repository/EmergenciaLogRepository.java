package com.petone.petone.repository;

import com.petone.petone.model.EmergenciaLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

//Repositório para a entidade EmergenciaLog.
public interface EmergenciaLogRepository extends MongoRepository<EmergenciaLog, String> {
    
    //Busca todos os logs de emergência associados a um ID de hospital específico.
    List<EmergenciaLog> findByIdHospital(String idHospital);

    //Busca todos os logs de emergência associados a um ID de tutor específico.
    List<EmergenciaLog> findByIdTutor(String idTutor);

    //Busca um log de emergência pelo seu token único.
    Optional<EmergenciaLog> findByTokenEmergencia(String tokenEmergencia);
}