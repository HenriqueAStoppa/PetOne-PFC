<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/AnimalRepository.java
package com.petone.petone.animal;
=======
package com.petone.petone.repository;
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Repository/AnimalRepository.java

import com.petone.petone.model.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;
<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/AnimalRepository.java
import org.springframework.stereotype.Repository;

=======
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Repository/AnimalRepository.java
import java.util.List;

/**
 * Repositório para a entidade Animal.
 */
public interface AnimalRepository extends MongoRepository<Animal, String> {
    
    /**
     * Busca todos os animais que pertencem a um tutor específico.
     * @param idTutor O ID do Tutor.
     * @return Uma lista de Animais.
     */
    List<Animal> findByIdTutor(String idTutor);

    /**
     * Deleta todos os animais que pertencem a um tutor específico.
     * (Usado quando o perfil do tutor é deletado).
     * @param idTutor O ID do Tutor.
     */
    void deleteByIdTutor(String idTutor);
}