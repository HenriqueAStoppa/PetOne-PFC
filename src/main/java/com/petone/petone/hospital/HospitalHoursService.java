package com.petone.petone.hospital;

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

    private void ensureHospitalExists(String hospitalId) {
        if (!hospitalRepo.existsById(hospitalId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hospital não encontrado");
        }
    }

    public List<HospitalHours> listByHospital(String hospitalId, Integer diaSemana) {
        ensureHospitalExists(hospitalId);
        return (diaSemana == null)
                ? repo.findByHospitalId(hospitalId)
                : repo.findByHospitalIdAndDiaSemana(hospitalId, diaSemana);
    }

    public HospitalHours create(String hospitalId, HospitalHours dto) {
        ensureHospitalExists(hospitalId);
        dto.setId(null);
        dto.setHospitalId(hospitalId);
        return repo.save(dto);
    }

    public HospitalHours get(String hospitalId, String id) {
        ensureHospitalExists(hospitalId);
        return repo.findById(id)
                .filter(h -> h.getHospitalId().equals(hospitalId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado"));
    }

    public HospitalHours update(String hospitalId, String id, HospitalHours dto) {
        HospitalHours existing = get(hospitalId, id);
        if (dto.getDiaSemana() != 0) existing.setDiaSemana(dto.getDiaSemana());
        if (dto.getAbre() != null) existing.setAbre(dto.getAbre());
        if (dto.getFecha() != null) existing.setFecha(dto.getFecha());
        return repo.save(existing);
    }

    public void delete(String hospitalId, String id) {
        HospitalHours existing = get(hospitalId, id);
        repo.delete(existing);
    }
}
