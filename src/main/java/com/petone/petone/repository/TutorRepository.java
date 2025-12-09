package com.petone.petone.repository;

import com.petone.petone.model.Tutor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

//Repositório para a entidade Tutor.
public interface TutorRepository extends MongoRepository<Tutor, String> {
    
    Optional<Tutor> findByEmailTutor(String emailTutor);

    Optional<Tutor> findByEmailTutorIgnoreCase(String emailTutor);

    boolean existsByEmailTutorIgnoreCase(String emailTutor);

    Optional<Tutor> findByResetToken(String resetToken);
}
