package com.petone.petone.animal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository repository;

    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    public Animal create(String tutorId, @Valid AnimalDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do animal é obrigatório");
        }
        if (dto.getTipo() == null || dto.getTipo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo do animal é obrigatório");
        }

        Animal a = new Animal();
        a.setTutorId(tutorId);
        a.setNome(dto.getNome());
        a.setTipo(dto.getTipo());
        a.setRaca(dto.getRaca());
        a.setSexo(dto.getSexo());
        a.setCastrado(dto.isCastrado());
        a.setIdade(dto.getIdade());
        return repository.save(a);
    }

    public List<Animal> list(String tutorId, String tipo, String raca, String sexo, Boolean castrado, Integer idadeMinima) {
        List<Animal> base = repository.findByTutorId(tutorId);
        return base.stream()
                .filter(a -> tipo == null || (a.getTipo() != null && a.getTipo().equalsIgnoreCase(tipo)))
                .filter(a -> raca == null || (a.getRaca() != null && a.getRaca().equalsIgnoreCase(raca)))
                .filter(a -> sexo == null || (a.getSexo() != null && a.getSexo().equalsIgnoreCase(sexo)))
                .filter(a -> castrado == null || (a.getCastrado() != null && a.getCastrado() == castrado))
                .filter(a -> idadeMinima == null || (a.getIdade() != null && a.getIdade() >= idadeMinima))
                .toList();
    }

    public Animal get(String tutorId, String id) {
        Animal a = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não encontrado"));
        if (!a.getTutorId().equals(tutorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal não pertence ao tutor");
        }
        return a;
    }

    public Animal update(String tutorId, String id, @Valid AnimalDTO in) {
        Animal a = get(tutorId, id); // garante 404 se não for do tutor
        // PUT completo: substitui os campos
        a.setNome(in.getNome());
        a.setTipo(in.getTipo());
        a.setRaca(in.getRaca());
        a.setSexo(in.getSexo());
        a.setCastrado(in.isCastrado());
        a.setIdade(in.getIdade());
        return repository.save(a);
    }

    public void delete(String tutorId, String id) {
        Animal a = get(tutorId, id);
        repository.delete(a);
    }
}
