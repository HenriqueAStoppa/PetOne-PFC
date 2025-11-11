package com.petone.petone.controller;

import com.petone.petone.dto.HospitalPerfilDTO;
import com.petone.petone.model.Hospital;
import com.petone.petone.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller protegido para o gerenciamento de perfil do Hospital.
 * (Requer Token JWT de Hospital).
 */
@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * (GET /api/hospital/me) - Busca os dados do perfil do hospital logado.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMeuPerfil(Principal principal) {
        try {
            // O email (username) vem do token JWT
            Hospital hospital = hospitalService.getMeuPerfil(principal.getName());
            return ResponseEntity.ok(hospital);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * (PUT /api/hospital/me) - Atualiza os dados do perfil do hospital logado.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMeuPerfil(@Valid @RequestBody HospitalPerfilDTO dto, Principal principal) {
        try {
            Hospital hospitalAtualizado = hospitalService.updateMeuPerfil(principal.getName(), dto);
            return ResponseEntity.ok(hospitalAtualizado);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // (O endpoint DELETE /me pode ser adicionado aqui se necessário)
}