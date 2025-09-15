package com.petone.petone.animal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
 List<Animal> findByTutorId(String tutorId);
 List<Animal> findByTutorIdAndTipoIgnoreCase(String tutorId, String tipo);
 List<Animal> findByTutorIdAndRacaIgnoreCase(String tutorId, String raca);
 List<Animal> findByTutorIdAndSexoIgnoreCase(String tutorId, String sexo);
 List<Animal> findByTutorIdAndCastrado(String tutorId, boolean castrado);
 List<Animal> findByTutorIdAndIdadeGreaterThanEqual(String tutorId, int idadeMin);
}