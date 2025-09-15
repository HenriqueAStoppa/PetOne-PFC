package com.petone.petone.animal;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "animais")
public class Animal {
    @Id
    private String id;
    private String nome;
    private String tipo;   // tipo do animal ou espécie
    private String raca;
    private String sexo;
    private boolean castrado;
    private int idade;

    //procurar maneira de facilitar os codigos e arrumar endereçamento
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
