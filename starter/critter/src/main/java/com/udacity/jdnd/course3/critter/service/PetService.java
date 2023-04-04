package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepository;

    public Pet save(Pet pet){
        return petRepository.save(pet);
    }

    public Optional<Pet> getPet(Long id) { return petRepository.findById(id); }

    public List<Pet> getPets() { return petRepository.findAll(); }

    public List<Pet> getPetsByOwnerId(Long ownerid) { return petRepository.findByOwnerId(ownerid); }
}
