package com.petone.petone.animal;

import jakarta.validation.constraints.Min;

public class AnimalFilterDTO {
    private String tipo;
    private String raca;
    private String sexo;
    private Boolean castrado;

    @Min(0)
    private Integer idadeMinima;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public Boolean getCastrado() { return castrado; }
    public void setCastrado(Boolean castrado) { this.castrado = castrado; }
    public Integer getIdadeMinima() { return idadeMinima; }
    public void setIdadeMinima(Integer idadeMinima) { this.idadeMinima = idadeMinima; }
}