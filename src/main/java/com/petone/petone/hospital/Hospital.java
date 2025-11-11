<<<<<<< Updated upstream:src/main/java/com/petone/petone/hospital/Hospital.java
package com.petone.petone.hospital;
=======
package com.petone.petone.model;
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Model/Hospital.java

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * Modelo para a entidade Hospital Veterinário. Usa Lombok.
 * (Versão completa com senhaHash)
 */
@Data
@Document(collection = "hospitais")
public class Hospital {
    @Id
    private String idHospital;
    private String nomeFantasia;
    private String emailHospital;
    private String telefoneHospital;
    private String endereco; // Para uso com a API do Google Maps
    private String cnpj; // Dado sensível
    private int classificacaoServico; // 1 a 4
    private String veterinarioResponsavel;
    private String crmvVeterinario;
    private boolean emailVerificado = false;
    private String senhaHash; // Armazena o hash da senha (BCrypt)
}