package com.petone.petone.hospital;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {

  private final HospitalService service;

  public HospitalController(HospitalService service) { this.service = service; }

  // RN-04: listar apenas ativos; se parceiro=true, apenas parceiros
  // RN-01: filtro por tipo animal (?tipo=cao|gato|...)
  // RN-06: ?abertoAgora=true filtra por horário de hoje
  @GetMapping
  public List<Hospital> list(
      @RequestParam(required = false) Boolean parceiro,
      @RequestParam(required = false) String tipo,
      @RequestParam(required = false) Boolean abertoAgora
  ) {
    return service.list(parceiro, tipo, abertoAgora);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Hospital create(@Valid @RequestBody Hospital h) { return service.create(h); }

  @GetMapping("/{id}")
  public Hospital get(@PathVariable String id) { return service.get(id); }

  @PutMapping("/{id}")
  public Hospital update(@PathVariable String id, @Valid @RequestBody Hospital h) {
    return service.update(id, h);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) { service.delete(id); }
}
