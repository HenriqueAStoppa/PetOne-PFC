package com.petone.petone.animal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutors/{tutorId}/animals")
@Tag(name = "Animals")
public class AnimalController {

    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @GetMapping
    public List<Animal> list(@PathVariable String tutorId,
                             @ParameterObject @Valid AnimalFilterDTO filtro) {
        return service.list(
            tutorId,
            filtro.getTipo(),
            filtro.getRaca(),
            filtro.getSexo(),
            filtro.getCastrado(),
            filtro.getIdadeMinima()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Animal create(@PathVariable String tutorId, @Valid @RequestBody AnimalDTO dto) {
        return service.create(tutorId, dto);
    }

    @GetMapping("/{id}")
    public Animal get(@PathVariable String tutorId, @PathVariable String id) {
        return service.get(tutorId, id);
    }

    @PutMapping("/{id}")
    public Animal update(@PathVariable String tutorId,
                         @PathVariable String id,
                         @Valid @RequestBody AnimalDTO dto) {
        return service.update(tutorId, id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String tutorId, @PathVariable String id) {
        service.delete(tutorId, id);
    }
}
