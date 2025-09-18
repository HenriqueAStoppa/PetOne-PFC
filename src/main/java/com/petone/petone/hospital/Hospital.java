package com.petone.petone.hospital;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("hospitals")
public class Hospital {
  @Id
  private String id;

  @NotBlank @Size(min=2,max=120)
  private String nomeFantasia;

  @Indexed(unique = true)
  @NotBlank
  private String email;

  private String telefone;
  private String endereco;
  private Double lat; 
  private Double lng;

  private List<String> tiposAtendidos;

  private boolean parceiro = true;
  private boolean ativo = true;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getNomeFantasia() { return nomeFantasia; }
  public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getTelefone() { return telefone; }
  public void setTelefone(String telefone) { this.telefone = telefone; }
  public String getEndereco() { return endereco; }
  public void setEndereco(String endereco) { this.endereco = endereco; }
  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }
  public Double getLng() { return lng; }
  public void setLng(Double lng) { this.lng = lng; }
  public List<String> getTiposAtendidos() { return tiposAtendidos; }
  public void setTiposAtendidos(List<String> tiposAtendidos) { this.tiposAtendidos = tiposAtendidos; }
  public boolean isParceiro() { return parceiro; }
  public void setParceiro(boolean parceiro) { this.parceiro = parceiro; }
  public boolean isAtivo() { return ativo; }
  public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
