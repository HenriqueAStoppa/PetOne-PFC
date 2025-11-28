package com.petone.petone.service;

import com.petone.petone.model.Hospital;
import com.petone.petone.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//Serviço SIMULADO para a API do Google Maps.
//service/MapsService.java
@Service
public class MapsService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital encontrarHospitalMaisProximo(double tutorLat, double tutorLon) {

        List<Hospital> hospitais = hospitalRepository.findAll();

        if (hospitais.isEmpty()) {
            throw new RuntimeException("Nenhum hospital parceiro encontrado.");
        }

        Hospital maisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Hospital h : hospitais) {
            //só entra aqui quem tiver latitude/longitude preenchida
            if (h.getLatitude() != null && h.getLongitude() != null) {

                double distancia = calcularDistancia(
                        tutorLat, tutorLon,
                        h.getLatitude(), h.getLongitude()
                );

                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    maisProximo = h;
                }
            }
        }

        if (maisProximo == null) {
            return hospitais.get(0);
        }

        return maisProximo;
    }

    private double calcularDistancia(double lat1, double lon1,
                                     double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}