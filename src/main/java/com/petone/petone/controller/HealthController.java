package com.petone.petone.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    @Hidden 
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache().mustRevalidate())
            .body(Map.of(
                "status", "OK",
                "timestamp", Instant.now().toString()
            ));
    }
}