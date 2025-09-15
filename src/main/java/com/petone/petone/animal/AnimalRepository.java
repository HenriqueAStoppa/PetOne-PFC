package com.petone.petone.animal;


import com.petone.petone.animal.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

//preciso do banco logo
@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
}
