package com.petone.petone.animal;

//arrumar erros
import com.petone.petone.animal.Animal;
import com.petone.petone.animal.AnimalRepository;
import com.petone.petone.animal.AnimalDTO;
import com.petone.petone.animal.AnimalFilterDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository repository;

    public Animal cadastrarAnimal(AnimalDTO dto) {
        if (dto.getNome() == null || dto.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome do animal é obrigatório.");
        }

        Animal animal = new Animal();
        animal.setNome(dto.getNome());
        animal.setTipo(dto.getTipo());
        animal.setRaca(dto.getRaca());
        animal.setSexo(dto.getSexo());
        animal.setCastrado(dto.isCastrado());
        animal.setIdade(dto.getIdade());

        return repository.save(animal);
    }
    
    //revisar filtro com erro
    public List<Animal> filtrarAnimais(AnimalFilterDTO filtro) {
        List<Animal> todos = repository.findAll();

        return todos.stream()
                .filter(a -> filtro.getTipo() == null || a.getTipo().equalsIgnoreCase(filtro.getTipo()))
                .filter(a -> filtro.getRaca() == null || a.getRaca().equalsIgnoreCase(filtro.getRaca()))
                .filter(a -> filtro.getSexo() == null || a.getSexo().equalsIgnoreCase(filtro.getSexo()))
                .filter(a -> filtro.getCastrado() == null || a.isCastrado() == filtro.getCastrado())
                .filter(a -> filtro.getIdadeMinima() == null || a.getIdade() >= filtro.getIdadeMinima())
                .collect(Collectors.toList());
    }
}
