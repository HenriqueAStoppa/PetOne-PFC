package com.petone.petone.animal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
    List<Animal> findByTutorId(String tutorId);
}
