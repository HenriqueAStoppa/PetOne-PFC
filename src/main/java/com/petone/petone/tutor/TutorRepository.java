<<<<<<< Updated upstream:src/main/java/com/petone/petone/tutor/TutorRepository.java
package com.petone.petone.tutor;
=======
package com.petone.petone.repository;
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Repository/TutorRepository.java

import com.petone.petone.model.Tutor;
import org.springframework.data.mongodb.repository.MongoRepository;
<<<<<<< Updated upstream:src/main/java/com/petone/petone/tutor/TutorRepository.java

=======
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Repository/TutorRepository.java
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