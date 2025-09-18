package com.petone.petone.hospital;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HospitalRepository extends MongoRepository<Hospital, String> {
    List<Hospital> findByAtivoTrue();
    List<Hospital> findByAtivoTrueAndParceiroTrue();
}
