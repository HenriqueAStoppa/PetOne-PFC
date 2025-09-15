package com.petone.petone.animal;


import com.petone.petone.animal.Animal;
import com.petone.petone.animal.AnimalService;
import com.petone.petone.animal.AnimalRepository;
import com.petone.petone.animal.AnimalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    @Autowired
    private AnimalService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<Animal> cadastrar(@RequestBody AnimalDTO dto) {
        return ResponseEntity.ok(service.cadastrarAnimal(dto));
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<Animal>> filtrar(@RequestBody AnimalFilterDTO filtro) {
        return ResponseEntity.ok(service.filtrarAnimais(filtro));
    }
}
