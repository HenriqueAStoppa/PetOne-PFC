package com.petone.petone.tutor;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TutorRepository extends MongoRepository<Tutor, String> {
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
  Optional<Tutor> findByEmail(String email);
}
