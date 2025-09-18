package com.petone.petone.hospital;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals/{hospitalId}/hours")
public class HospitalHoursController {

  private final HospitalHoursService service;

  public HospitalHoursController(HospitalHoursService service) {
    this.service = service;
  }

  @GetMapping
  public List<HospitalHours> list(@PathVariable String hospitalId,
                                  @RequestParam(required = false) Integer diaSemana) {
    return service.listByHospital(hospitalId, diaSemana);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HospitalHours create(@PathVariable String hospitalId,
                              @Valid @RequestBody HospitalHours body) {
    return service.create(hospitalId, body);
  }

  @GetMapping("/{id}")
  public HospitalHours get(@PathVariable String hospitalId, @PathVariable String id) {
    return service.get(hospitalId, id);
  }

  @PutMapping("/{id}")
  public HospitalHours update(@PathVariable String hospitalId,
                              @PathVariable String id,
                              @Valid @RequestBody HospitalHours body) {
    return service.update(hospitalId, id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String hospitalId, @PathVariable String id) {
    service.delete(hospitalId, id);
  }
}
