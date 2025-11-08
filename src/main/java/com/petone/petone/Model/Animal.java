package com.petone.petone.Model;

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
}