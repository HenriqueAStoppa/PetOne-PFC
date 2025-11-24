package com.petone.petone.controller;

import com.petone.petone.dto.TutorPerfilDTO;
import com.petone.petone.model.Tutor;
import com.petone.petone.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller protegido para o gerenciamento de perfil do Tutor.
 */
@RestController
@RequestMapping("/api/tutor")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    /**
     * (GET /api/tutor/me) - Busca os dados do perfil logado.
     */
    @PostMapping("/me")
    public ResponseEntity<Tutor> getMeuPerfil(Principal principal) {
        // O email (username) vem do token JWT
        Tutor tutor = tutorService.getMeuPerfil(principal.getName());
        return ResponseEntity.ok(tutor);
    }

    /**
     * (PUT /api/tutor/me) - Atualiza os dados do perfil logado.
     */
    @PutMapping("/me")
    public ResponseEntity<Tutor> updateMeuPerfil(@Valid @RequestBody TutorPerfilDTO dto, Principal principal) {
        Tutor tutorAtualizado = tutorService.updateMeuPerfil(principal.getName(), dto);
        return ResponseEntity.ok(tutorAtualizado);
    }

    /**
     * (DELETE /api/tutor/me) - Deleta o perfil logado.
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMeuPerfil(Principal principal) {
        tutorService.deleteMeuPerfil(principal.getName());
        return ResponseEntity.ok("Perfil deletado com sucesso.");
    }
}