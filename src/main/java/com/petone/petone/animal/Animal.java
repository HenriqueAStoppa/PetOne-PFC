<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/Animal.java
<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/Animal.java
package com.petone.petone.animal;
=======
package com.petone.petone.model;
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Model/Animal.java
=======
package com.petone.petone.model;
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Model/Animal.java

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

/**
 * Modelo para a entidade Animal.
 * Vinculado ao tutor (idTutor). Usa Lombok.
 */
@Data
@Document(collection = "animais")
public class Animal {
<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/Animal.java
<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/Animal.java
 @Id
 private String id;
 @NotBlank
 private String tutorId;   
 @NotBlank
 @Size(min = 2, max = 60)
 private String nome;
 @NotBlank
 @Size(max = 20)
 private String tipo;  
 @Size(max = 50)
 private String raca;
 @Size(max = 10)
 private String sexo;        
 private Boolean castrado;  
 @Min(0)
 private Integer idade;      
 public String getId() { return id; }
 public void setId(String id) { this.id = id; }
 public String getTutorId() { return tutorId; }
 public void setTutorId(String tutorId) { this.tutorId = tutorId; }
 public String getNome() { return nome; }
 public void setNome(String nome) { this.nome = nome; }
 public String getTipo() { return tipo; }
 public void setTipo(String tipo) { this.tipo = tipo; }
 public String getRaca() { return raca; }
 public void setRaca(String raca) { this.raca = raca; }
 public String getSexo() { return sexo; }
 public void setSexo(String sexo) { this.sexo = sexo; }
 public Boolean getCastrado() { return castrado; }
 public void setCastrado(Boolean castrado) { this.castrado = castrado; }
 public Integer getIdade() { return idade; }
 public void setIdade(Integer idade) { this.idade = idade; }
=======
=======
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Model/Animal.java
    @Id
    private String idAnimal;
    private String idTutor; // Chave estrangeira para Tutor
    private String nomeAnimal;
    private int idade;
    private String especie;
    private String raca;
    private String sexo;
    private boolean castrado;
    private boolean usaMedicacao;
    private String qualMedicacao;
<<<<<<< Updated upstream:src/main/java/com/petone/petone/animal/Animal.java
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Model/Animal.java
=======
>>>>>>> Stashed changes:src/main/java/com/petone/petone/Model/Animal.java
}