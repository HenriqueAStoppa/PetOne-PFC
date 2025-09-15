package com.petone.petone.animal;


public class AnimalDTO {
    private String nome;
    private String tipo;
    private String raca;
    private String sexo;
    private boolean castrado;
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
