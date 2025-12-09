package com.petone.petone.repository;

import com.petone.petone.model.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

//Repositório para a entidade Hospital.
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    Optional<Hospital> findByEmailHospital(String emailHospital);

    Optional<Hospital> findByEmailHospitalIgnoreCase(String emailHospital);

    boolean existsByEmailHospitalIgnoreCase(String emailHospital);

    //Busca um Hospital pelo CNPJ.
    Optional<Hospital> findByCnpj(String cnpj);

    Optional<Hospital> findByResetToken(String resetToken);
}
