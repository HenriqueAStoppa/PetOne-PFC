package com.petone.petone.tutor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutors")
public class TutorController {

  private final TutorService service;

  public TutorController(TutorService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Tutor create(@Valid @RequestBody Tutor tutor) {
    return service.create(tutor);
  }

  @GetMapping("/{id}")
  public Tutor getById(@PathVariable String id) {
    return service.getById(id);
  }

  @GetMapping
  public List<Tutor> listAll() {
    return service.listAll();
  }

  @PutMapping("/{id}")
  public Tutor update(@PathVariable String id, @Valid @RequestBody Tutor tutor) {
    return service.update(id, tutor);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    service.delete(id);
  }
}
