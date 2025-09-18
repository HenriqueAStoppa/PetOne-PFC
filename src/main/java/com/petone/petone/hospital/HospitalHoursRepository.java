package com.petone.petone.hospital;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HospitalHoursRepository extends MongoRepository<HospitalHours, String> {
    List<HospitalHours> findByHospitalId(String hospitalId);
    List<HospitalHours> findByHospitalIdAndDiaSemana(String hospitalId, int diaSemana);
}
