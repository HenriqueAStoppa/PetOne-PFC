package com.petone.petone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double[] getCoordinates(String logradouro, String numero, String cidade, String uf) {
        try {
            // Monta o endereço para busca (Ex: Av Paulista, 1000, Sao Paulo, SP)
            String query = String.format("%s, %s, %s, %s", logradouro, numero, cidade, uf);
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            
            // Usa o Nominatim (OpenStreetMap)
            String url = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery + "&format=json&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "PetOne-TCC-Project") 
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.isArray() && root.size() > 0) {
                    JsonNode local = root.get(0);
                    double lat = local.get("lat").asDouble();
                    double lon = local.get("lon").asDouble();
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao geocodificar: " + e.getMessage());
        }
        // Retorna 0,0 se falhar (para não travar o cadastro)
        return new double[]{0.0, 0.0};
    }
}