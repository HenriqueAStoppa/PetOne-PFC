<<<<<<< Updated upstream:src/main/java/com/petone/petone/hospital/HospitalRepository.java
package com.petone.petone.hospital;
=======
package com.petone.petone.repository;

import com.petone.petone.model.Hospital;
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Repository/HospitalRepository.java

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

<<<<<<< Updated upstream:src/main/java/com/petone/petone/hospital/HospitalRepository.java
import java.util.List;

=======
/**
 * Repositório para a entidade Hospital.
 */
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Repository/HospitalRepository.java
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