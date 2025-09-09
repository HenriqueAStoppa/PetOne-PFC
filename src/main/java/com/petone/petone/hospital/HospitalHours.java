package com.petone.petone.hospital;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("hospital_hours")
public class HospitalHours {
  @Id
  private String id;

  @NotNull
  private String hospitalId;

  // 0=Dom, 1=Seg, ... 6=Sáb
  private int diaSemana;

  // "08:00" / "18:00"
  private String abre;
  private String fecha;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getHospitalId() { return hospitalId; }
  public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
  public int getDiaSemana() { return diaSemana; }
  public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }
  public String getAbre() { return abre; }
  public void setAbre(String abre) { this.abre = abre; }
  public String getFecha() { return fecha; }
  public void setFecha(String fecha) { this.fecha = fecha; }
}
