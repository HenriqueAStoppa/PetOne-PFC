package com.petone.petone.controller;

import com.petone.petone.dto.EmergenciaRequestDTO;
import com.petone.petone.dto.EmergenciaResponseDTO;
import com.petone.petone.dto.FinalizacaoRequestDTO; 
import com.petone.petone.model.EmergenciaLog; 
import com.petone.petone.service.EmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.web.bind.annotation.*; 

import java.security.Principal;
import java.util.NoSuchElementException; 

@RestController
@RequestMapping("/api/emergencia")
public class EmergenciaController {

    private final EmergenciaService emergenciaService;

    public EmergenciaController(EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
    }

    //Inicia um novo registro de emergência. Requer que o Tutor esteja logado (autenticado via JWT).
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarEmergencia(@Valid @RequestBody EmergenciaRequestDTO dto, Principal principal) {
        try {
            EmergenciaResponseDTO response = emergenciaService.iniciarEmergencia(dto, principal.getName());
            return ResponseEntity.ok(response);
            
        } catch (NoSuchElementException e) {
            //Se o Animal ou o Hospital não forem encontrados
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno ao processar emergência: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //ENDPOINT DE FINALIZAÇÃO]

    @PutMapping("/finalizar/{token}")
    public ResponseEntity<?> finalizarEmergencia(@PathVariable String token,
                                                 @Valid @RequestBody FinalizacaoRequestDTO dto,
                                                 Principal principal) {
        try {
            //principal.getName() é o email do hospital logado
            String hospitalEmail = principal.getName();
            
            EmergenciaLog logAtualizado = emergenciaService.finalizarEmergencia(token, dto, hospitalEmail);
            return ResponseEntity.ok(logAtualizado);

        } catch (NoSuchElementException | UsernameNotFoundException e) {
            //Se o Log ou o Hospital não forem encontrados
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); //404
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); //400
        }
    }
}