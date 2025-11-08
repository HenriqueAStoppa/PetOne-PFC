package com.petone.petone.Service;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.petone.petone.Model.Tutor;
import com.petone.petone.Repository.TutorRepository;

import java.util.List;

@Service
public class TutorService {

  private final TutorRepository repository;

  public TutorService(TutorRepository repository) {
    this.repository = repository;
  }

  private String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }

  public Tutor create(@Valid Tutor tutor) {
    tutor.setEmail(normalizeEmail(tutor.getEmail()));

    if (repository.existsByEmail(tutor.getEmail()) || repository.existsByCpf(tutor.getCpf())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ou CPF já cadastrado");
    }
    return repository.save(tutor);
  }

  public Tutor getById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado"));
  }

  public List<Tutor> listAll() {
    return repository.findAll();
  }

  public Tutor update(String id, @Valid Tutor incoming) {
    Tutor current = getById(id);

    String newEmail = normalizeEmail(incoming.getEmail());
    incoming.setEmail(newEmail);

    if (!current.getEmail().equals(newEmail) && repository.existsByEmail(newEmail)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
    }
    if (!current.getCpf().equals(incoming.getCpf()) && repository.existsByCpf(incoming.getCpf())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
    }

    current.setNome(incoming.getNome());
    current.setEmail(newEmail);
    current.setCpf(incoming.getCpf());
    current.setSenhaHash(incoming.getSenhaHash()); // aplicar hash aqui
    current.setDataNasc(incoming.getDataNasc());
    current.setAtivo(incoming.isAtivo());
    current.setEmailVerificado(incoming.isEmailVerificado());

    return repository.save(current);
  }

  public void delete(String id) {
    Tutor t = getById(id);
    repository.delete(t);
  }
}
