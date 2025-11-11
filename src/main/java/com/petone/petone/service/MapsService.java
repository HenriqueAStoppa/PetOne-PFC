package com.petone.petone.service;

import com.petone.petone.model.Hospital;
import com.petone.petone.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço SIMULADO para a API do Google Maps.
 * (Para o TCC, apenas busca o primeiro hospital do banco).
 */
@Service
public class MapsService {

    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * Simula a busca pelo hospital mais próximo.
     * (A lógica real usaria geolocalização e a API do Maps).
     *
     * @param tipoEmergencia A classificação do serviço (1-4)
     * @return O primeiro hospital encontrado.
     */
    public Hospital encontrarHospitalMaisProximo(String tipoEmergencia) {
        
        // Simulação simples: Apenas busca todos os hospitais e retorna o primeiro.
        // A lógica real filtraria por 'classificacaoServico' e distância.
        
        List<Hospital> hospitais = hospitalRepository.findAll();
        
        if (hospitais.isEmpty()) {
            // Se não houver hospitais cadastrados, lança um erro.
            throw new RuntimeException("Nenhum hospital de emergência encontrado no sistema.");
        }

        // Retorna o primeiro hospital da lista como simulação.
        return hospitais.get(0);
    }
}