package com.petone.petone.hospital;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HospitalHoursService {

  private final HospitalHoursRepository repo;
  private final HospitalRepository hospitalRepo;

  public HospitalHoursService(HospitalHoursRepository repo, HospitalRepository hospitalRepo) {
    this.repo = repo;
    this.hospitalRepo = hospitalRepo;
  }

  public HospitalHours create(String hospitalId, @Valid HospitalHours body) {
    if (hospitalId == null || hospitalId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hospitalId é obrigatório");
    }
    // (opcional quando Mongo estiver ativo) valida se hospital existe:
    // hospitalRepo.findById(hospitalId).orElseThrow(
    //     () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hospital não encontrado"));

    body.setHospitalId(hospitalId);
    return repo.save(body);
  }

  public List<HospitalHours> listAllByHospital(String hospitalId) {
    if (hospitalId == null || hospitalId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hospitalId é obrigatório");
    }
    return repo.findByHospitalId(hospitalId);
  }

  public HospitalHours get(String id) {
    return repo.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado"));
  }

  public HospitalHours update(String id, @Valid HospitalHours in) {
    HospitalHours h = get(id);
    h.setDiaSemana(in.getDiaSemana());
    h.setAbre(in.getAbre());
    h.setFecha(in.getFecha());
    return repo.save(h);
  }

  public void delete(String id) {
    HospitalHours h = get(id);
    repo.delete(h);
  }
}
