package com.petone.petone.controller;

import com.petone.petone.dto.EmergenciaRequestDTO;
import com.petone.petone.dto.EmergenciaResponseDTO;
import com.petone.petone.dto.FinalizacaoRequestDTO; // NOVO IMPORT
import com.petone.petone.model.EmergenciaLog; // NOVO IMPORT
import com.petone.petone.service.EmergenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // NOVO IMPORT
import org.springframework.web.bind.annotation.*; // NOVO IMPORT

import java.security.Principal;
import java.util.NoSuchElementException; 

@RestController
@RequestMapping("/api/emergencia")
public class EmergenciaController {

    private final EmergenciaService emergenciaService;

    @Autowired
    public EmergenciaController(EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
    }

    /**
     * Endpoint para iniciar um novo registro de emergência.
     * Requer que o Tutor esteja logado (autenticado via JWT).
     */
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarEmergencia(@Valid @RequestBody EmergenciaRequestDTO dto, Principal principal) {
        try {
            // principal.getName() é o email do tutor logado
            EmergenciaResponseDTO response = emergenciaService.iniciarEmergencia(dto, principal.getName());
            return ResponseEntity.ok(response);
            
        } catch (NoSuchElementException e) {
            // Se o Animal ou o Hospital não forem encontrados
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Outros erros
            return new ResponseEntity<>("Erro interno ao processar emergência: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- [NOVO ENDPOINT DE FINALIZAÇÃO] ---

    /**
     * (PUT /api/emergencia/finalizar/{token}) - Finaliza um atendimento (Hospital).
     * @param token O token da emergência (passado na URL).
     * @param dto Os dados do relatório e prescrição (do DTO FinalizacaoRequestDTO).
     * @param principal O hospital logado (do JWT).
     */
    @PutMapping("/finalizar/{token}")
    public ResponseEntity<?> finalizarEmergencia(@PathVariable String token,
                                                 @Valid @RequestBody FinalizacaoRequestDTO dto,
                                                 Principal principal) {
        try {
            // principal.getName() é o email do hospital logado
            String hospitalEmail = principal.getName();
            
            EmergenciaLog logAtualizado = emergenciaService.finalizarEmergencia(token, dto, hospitalEmail);
            return ResponseEntity.ok(logAtualizado);

        } catch (NoSuchElementException | UsernameNotFoundException e) {
            // Se o Log ou o Hospital não forem encontrados
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        }
    }
}