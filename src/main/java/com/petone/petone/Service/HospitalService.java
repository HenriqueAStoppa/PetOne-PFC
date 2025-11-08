package com.petone.petone.Service;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petone.petone.Model.Hospital;
import com.petone.petone.Model.HospitalHours;
import com.petone.petone.Repository.HospitalHoursRepository;
import com.petone.petone.Repository.HospitalRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HospitalService {

  private final HospitalRepository repo;
  private final HospitalHoursRepository hoursRepo;

  public HospitalService(HospitalRepository repo, HospitalHoursRepository hoursRepo) {
    this.repo = repo;
    this.hoursRepo = hoursRepo;
  }

  public Hospital create(@Valid Hospital h) { return repo.save(h); }

  public Hospital get(String id) {
    return repo.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Hospital não encontrado"));
  }

  public List<Hospital> list(Boolean parceiro, String tipo, Boolean abertoAgora) {
    // base: sempre ativos
    List<Hospital> base = Boolean.TRUE.equals(parceiro)
        ? repo.findByAtivoTrueAndParceiroTrue()
        : repo.findByAtivoTrue();

    // filtro por tipo (se enviado)
    if (tipo != null && !tipo.isBlank()) {
      String t = tipo.toLowerCase();
      base = base.stream()
          .filter(h -> h.getTiposAtendidos() != null &&
              h.getTiposAtendidos().stream().filter(Objects::nonNull)
                .map(String::toLowerCase).anyMatch(v -> v.equals(t)))
          .collect(Collectors.toList());
    }

    // filtro "aberto agora" usando HospitalHours (MVP: apenas dia da semana e intervalo abre-fecha)
    if (Boolean.TRUE.equals(abertoAgora)) {
      int hoje = LocalDate.now().getDayOfWeek().getValue() % 7; // Dom=0
      LocalTime agora = LocalTime.now();
      base = base.stream().filter(h -> {
        List<HospitalHours> hs = hoursRepo.findByHospitalIdAndDiaSemana(h.getId(), hoje);
        return hs.stream().anyMatch(slot ->
            isWithin(agora, slot.getAbre(), slot.getFecha()));
      }).collect(Collectors.toList());
    }

    return base;
  }

  private boolean isWithin(LocalTime now, String abre, String fecha) {
    try {
      LocalTime a = LocalTime.parse(abre);
      LocalTime f = LocalTime.parse(fecha);
      return !now.isBefore(a) && now.isBefore(f);
    } catch (Exception e) {
      return false;
    }
  }

  public Hospital update(String id, @Valid Hospital in) {
    Hospital h = get(id);
    h.setNomeFantasia(in.getNomeFantasia());
    h.setEmail(in.getEmail());
    h.setTelefone(in.getTelefone());
    h.setEndereco(in.getEndereco());
    h.setLat(in.getLat());
    h.setLng(in.getLng());
    h.setTiposAtendidos(in.getTiposAtendidos());
    h.setParceiro(in.isParceiro());
    h.setAtivo(in.isAtivo());
    return repo.save(h);
  }

  public void delete(String id) {
    Hospital h = get(id);
    repo.delete(h);
  }
}