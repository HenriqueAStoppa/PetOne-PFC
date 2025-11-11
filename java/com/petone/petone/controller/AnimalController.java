package com.petone.petone.controller;

import com.petone.petone.dto.AnimalDTO;
import com.petone.petone.model.Animal;
import com.petone.petone.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller protegido para o CRUD de Animais.
 * (Requer Token JWT de Tutor).
 */
@RestController
@RequestMapping("/api/animais")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    /**
     * Busca o email do usuário logado (Tutor) a partir do token.
     */
    private String getEmailFromPrincipal(Principal principal) {
        return principal.getName();
    }

    /**
     * (POST /api/animais) - Cria um novo animal.
     */
    @PostMapping
    public ResponseEntity<Animal> createAnimal(@Valid @RequestBody AnimalDTO dto, Principal principal) {
        String emailTutor = getEmailFromPrincipal(principal);
        Animal novoAnimal = animalService.createAnimal(dto, emailTutor);
        return new ResponseEntity<>(novoAnimal, HttpStatus.CREATED);
    }

    /**
     * (GET /api/animais) - Lista todos os animais do tutor logado.
     */
    @GetMapping
    public ResponseEntity<List<Animal>> getMeusAnimais(Principal principal) {
        String emailTutor = getEmailFromPrincipal(principal);
        List<Animal> animais = animalService.getAnimalsByTutor(emailTutor);
        return ResponseEntity.ok(animais);
    }

    /**
     * (PUT /api/animais/{id}) - Atualiza um animal.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnimal(@PathVariable String id, @Valid @RequestBody AnimalDTO dto, Principal principal) {
        try {
            String emailTutor = getEmailFromPrincipal(principal);
            Animal animalAtualizado = animalService.updateAnimal(id, dto, emailTutor);
            return ResponseEntity.ok(animalAtualizado);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN); // 403
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        }
    }

    /**
     * (DELETE /api/animais/{id}) - Deleta um animal.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnimal(@PathVariable String id, Principal principal) {
        try {
            String emailTutor = getEmailFromPrincipal(principal);
            animalService.deleteAnimal(id, emailTutor);
            return ResponseEntity.noContent().build(); // 204
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN); // 403
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        }
    }
}