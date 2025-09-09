package com.petone.petone.hospital;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
public class HospitalHoursController {

  private final HospitalHoursService service;

  public HospitalHoursController(HospitalHoursService service) {
    this.service = service;
  }

  // cria um slot de horário para um hospital
  @PostMapping("/{hospitalId}/hours")
  @ResponseStatus(HttpStatus.CREATED)
  public HospitalHours create(@PathVariable String hospitalId,
                              @Valid @RequestBody HospitalHours body) {
    return service.create(hospitalId, body);
  }

  // lista todos os horários de um hospital
  @GetMapping("/{hospitalId}/hours")
  public List<HospitalHours> listAll(@PathVariable String hospitalId) {
    return service.listAllByHospital(hospitalId);
  }

  // obtém um slot específico por ID
  @GetMapping("/hours/{id}")
  public HospitalHours get(@PathVariable String id) {
    return service.get(id);
  }

  // atualiza um slot específico
  @PutMapping("/hours/{id}")
  public HospitalHours update(@PathVariable String id,
                              @Valid @RequestBody HospitalHours body) {
    return service.update(id, body);
  }

  // remove um slot específico
  @DeleteMapping("/hours/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    service.delete(id);
  }
}
