package com.petone.petone.repository;

import com.petone.petone.model.Hospital;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repositório para a entidade Hospital.
 */
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    /**
     * Busca um Hospital pelo email.
     * @param emailHospital O email de login.
     * @return Um Optional contendo o Hospital, se encontrado.
     */
    Optional<Hospital> findByEmailHospital(String emailHospital);

    /**
     * Busca um Hospital pelo CNPJ.
     * @param cnpj O CNPJ.
     * @return Um Optional contendo o Hospital, se encontrado.
     */
    Optional<Hospital> findByCnpj(String cnpj);

    Optional<Hospital> findByResetToken(String resetToken);
}