package com.petone.petone.repository;

import com.petone.petone.model.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

//Repositório para a entidade Animal.
public interface AnimalRepository extends MongoRepository<Animal, String> {
    
    //Busca todos os animais que pertencem a um tutor específico.
    List<Animal> findByIdTutor(String idTutor);

    //Deleta todos os animais que pertencem a um tutor específico, quando o perfil do tutor é deletado.
    void deleteByIdTutor(String idTutor);
}