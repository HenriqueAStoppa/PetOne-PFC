package com.petone.petone.service;

import com.petone.petone.dto.AnimalDTO;
import com.petone.petone.model.Animal;
import com.petone.petone.model.Tutor;
import com.petone.petone.repository.AnimalRepository;
import com.petone.petone.repository.TutorRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

//Serviço para a lógica de negócio do Animal (CRUD).
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;

    public AnimalService(AnimalRepository animalRepository, TutorRepository tutorRepository) {
        this.animalRepository = animalRepository;
        this.tutorRepository = tutorRepository;
    }

    //Método auxiliar para buscar o Tutor pelo email (do token).
    private Tutor getTutorFromEmail(String email) {
        return tutorRepository.findByEmailTutor(email)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor não encontrado com o email: " + email));
    }

    //Cria um novo animal para o tutor logado.
    public Animal createAnimal(AnimalDTO dto, String tutorEmail) {
        Tutor tutor = getTutorFromEmail(tutorEmail);

        Animal animal = new Animal();
        animal.setIdTutor(tutor.getIdTutor());
        animal.setNomeAnimal(dto.getNomeAnimal());
        animal.setIdade(dto.getIdade());
        animal.setEspecie(dto.getEspecie());
        animal.setRaca(dto.getRaca());
        animal.setSexo(dto.getSexo());
        animal.setCastrado(dto.isCastrado());
        animal.setUsaMedicacao(dto.isUsaMedicacao());
        animal.setQualMedicacao(dto.getQualMedicacao());

        return animalRepository.save(animal);
    }

    //Busca todos os animais do tutor logado.
    public List<Animal> getAnimalsByTutor(String tutorEmail) {
        Tutor tutor = getTutorFromEmail(tutorEmail);
        return animalRepository.findByIdTutor(tutor.getIdTutor());
    }

    //Atualiza um animal, verificando se pertence ao tutor logado.
    public Animal updateAnimal(String animalId, AnimalDTO dto, String tutorEmail) throws AccessDeniedException {
        Tutor tutor = getTutorFromEmail(tutorEmail);
        
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com ID: " + animalId));

        //Verificação de Propriedade
        if (!animal.getIdTutor().equals(tutor.getIdTutor())) {
            throw new AccessDeniedException("Este animal não pertence ao tutor logado.");
        }

        animal.setNomeAnimal(dto.getNomeAnimal());
        animal.setIdade(dto.getIdade());
        animal.setEspecie(dto.getEspecie());
        animal.setRaca(dto.getRaca());
        animal.setSexo(dto.getSexo());
        animal.setCastrado(dto.isCastrado());
        animal.setUsaMedicacao(dto.isUsaMedicacao());
        animal.setQualMedicacao(dto.getQualMedicacao());

        return animalRepository.save(animal);
    }

    //Deleta um animal, verificando se pertence ao tutor logado.
    public void deleteAnimal(String animalId, String tutorEmail) throws AccessDeniedException {
        Tutor tutor = getTutorFromEmail(tutorEmail);
        
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new NoSuchElementException("Animal não encontrado com ID: " + animalId));

        if (!animal.getIdTutor().equals(tutor.getIdTutor())) {
            throw new AccessDeniedException("Este animal não pertence ao tutor logado.");
        }

        animalRepository.delete(animal);
    }
}