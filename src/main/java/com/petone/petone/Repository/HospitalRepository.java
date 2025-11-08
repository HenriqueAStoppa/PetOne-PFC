package com.petone.petone.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.petone.petone.Model.Hospital;

import java.util.List;

public interface HospitalRepository extends MongoRepository<Hospital, String> {
    List<Hospital> findByAtivoTrue();
    List<Hospital> findByAtivoTrueAndParceiroTrue();
}
