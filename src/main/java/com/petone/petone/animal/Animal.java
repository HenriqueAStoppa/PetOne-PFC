package com.petone.petone.animal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "animals")
public class Animal {
 @Id
 private String id;
 @NotBlank
 private String tutorId;   
 @NotBlank
 @Size(min = 2, max = 60)
 private String nomeanimal;
 @NotBlank
 @Size(max = 20)
 private String tipoanimal;  
 @Size(max = 50)
 private String raca;
 @Size(max = 10)
 private String sexoanimal;        
 private Boolean castrado;  
 @Min(0)
 private Integer idadeanimal;      
 public String getId() { return id; }
 public void setId(String id) { this.id = id; }
 public String getTutorId() { return tutorId; }
 public void setTutorId(String tutorId) { this.tutorId = tutorId; }
 public String getNome() { return nomeanimal; }
 public void setNome(String nome) { this.nomeanimal = nome; }
 public String getTipo() { return tipoanimal; }
 public void setTipo(String tipo) { this.tipoanimal = tipo; }
 public String getRaca() { return raca; }
 public void setRaca(String raca) { this.raca = raca; }
 public String getSexo() { return sexoanimal; }
 public void setSexo(String sexo) { this.sexoanimal = sexo; }
 public Boolean getCastrado() { return castrado; }
 public void setCastrado(Boolean castrado) { this.castrado = castrado; }
 public Integer getIdade() { return idadeanimal; }
 public void setIdade(Integer idade) { this.idadeanimal = idade; }
}