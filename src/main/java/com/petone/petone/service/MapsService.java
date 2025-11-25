package com.petone.petone.service;

import com.petone.petone.model.Hospital;
import com.petone.petone.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//Serviço SIMULADO para a API do Google Maps.
@Service
public class MapsService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital encontrarHospitalMaisProximo(String tipoEmergencia) {
        
        List<Hospital> hospitais = hospitalRepository.findAll();
        
        if (hospitais.isEmpty()) {
            // Se não houver hospitais cadastrados, lança um erro.
            throw new RuntimeException("Nenhum hospital de emergência encontrado no sistema.");
        }
        // Retorna o primeiro hospital da lista como simulação.
        return hospitais.get(0);
    }
}