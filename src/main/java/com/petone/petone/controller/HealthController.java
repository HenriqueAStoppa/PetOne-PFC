package com.petone.petone.controller;

import com.petone.petone.dto.HospitalPerfilDTO;
import com.petone.petone.model.EmergenciaLog;
import com.petone.petone.model.Hospital;
import com.petone.petone.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/me")
    public ResponseEntity<?> getMeuPerfil(Principal principal) {
        try {
            Hospital hospital = hospitalService.getMeuPerfil(principal.getName());
            return ResponseEntity.ok(hospital);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMeuPerfil(@Valid @RequestBody HospitalPerfilDTO dto, Principal principal) {
        try {
            Hospital hospitalAtualizado = hospitalService.updateMeuPerfil(principal.getName(), dto);
            return ResponseEntity.ok(hospitalAtualizado);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<EmergenciaLog>> getMeusLogs(Principal principal) {
        List<EmergenciaLog> logs = hospitalService.getMeusLogs(principal.getName());
        return ResponseEntity.ok(logs);
    }
}