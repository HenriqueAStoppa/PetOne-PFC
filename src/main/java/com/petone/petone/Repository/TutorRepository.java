package com.petone.petone.repository;

import com.petone.petone.model.Tutor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repositório para a entidade Tutor.
 */
public interface TutorRepository extends MongoRepository<Tutor, String> {
    
    /**
     * Busca um Tutor pelo email.
     * @param emailTutor O email de login.
     * @return Um Optional contendo o Tutor, se encontrado.
     */
    Optional<Tutor> findByEmailTutor(String emailTutor);

    Optional<Tutor> findByResetToken(String resetToken);
}