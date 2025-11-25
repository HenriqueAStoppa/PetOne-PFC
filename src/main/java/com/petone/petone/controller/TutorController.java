package com.petone.petone.controller;

import com.petone.petone.dto.TutorPerfilDTO;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Tutor;
import com.petone.petone.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tutor")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    @GetMapping("/me")
    public ResponseEntity<Tutor> getMeuPerfil(Principal principal) {
        Tutor tutor = tutorService.getMeuPerfil(principal.getName());
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/me")
    public ResponseEntity<Tutor> updateMeuPerfil(@Valid @RequestBody TutorPerfilDTO dto, Principal principal) {
        Tutor tutorAtualizado = tutorService.updateMeuPerfil(principal.getName(), dto);
        return ResponseEntity.ok(tutorAtualizado);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMeuPerfil(Principal principal) {
        tutorService.deleteMeuPerfil(principal.getName());
        return ResponseEntity.ok("Perfil deletado com sucesso.");
    }

    @GetMapping("/logs")
    public ResponseEntity<List<EmergenciaLog>> getMeusLogs(Principal principal) {
        return ResponseEntity.ok(tutorService.getMeusLogs(principal.getName()));
    }
}