package com.petone.petone.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AnimalDTO {
    @NotBlank
    @Size(min = 2, max = 60)
    private String nome;

    @NotBlank
    private String tipo;

    @Size(max = 50)
    private String raca;

    private String sexo;

    private boolean castrado;

    @Min(0)
    private int idade;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public boolean isCastrado() { return castrado; }
    public void setCastrado(boolean castrado) { this.castrado = castrado; }
    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }
}
