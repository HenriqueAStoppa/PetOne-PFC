package com.petone.petone.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.petone.petone.Model.Tutor;

import java.util.Optional;

public interface TutorRepository extends MongoRepository<Tutor, String> {
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
  Optional<Tutor> findByEmail(String email);
}
