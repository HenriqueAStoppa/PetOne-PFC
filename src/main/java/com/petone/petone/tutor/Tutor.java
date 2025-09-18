package com.petone.petone.tutor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tutors")
public class Tutor {

  @Id
  private String id;

  @NotBlank(message = "Nome é obrigatório")
  @Size(min = 2, max = 100)
  private String nome;

  @NotBlank(message = "E-mail é obrigatório")
  @Email(message = "E-mail inválido")
  @Indexed(unique = true)
  private String email;

  @NotBlank(message = "CPF é obrigatório")
  @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos numéricos")
  @Indexed(unique = true)
  private String cpf;

  @NotBlank(message = "Senha/Hash é obrigatório")
  @JsonIgnore // ✅ não vaza no JSON de resposta
  private String senhaHash;

  @Size(max = 10)
  @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "dataNasc deve estar no formato YYYY-MM-DD")
  private String dataNasc;

  private boolean ativo = true;
  private boolean emailVerificado = false;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCpf() { return cpf; }
  public void setCpf(String cpf) { this.cpf = cpf; }
  public String getSenhaHash() { return senhaHash; }
  public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
  public String getDataNasc() { return dataNasc; }
  public void setDataNasc(String dataNasc) { this.dataNasc = dataNasc; }
  public boolean isAtivo() { return ativo; }
  public void setAtivo(boolean ativo) { this.ativo = ativo; }
  public boolean isEmailVerificado() { return emailVerificado; }
  public void setEmailVerificado(boolean emailVerificado) { this.emailVerificado = emailVerificado; }
}
