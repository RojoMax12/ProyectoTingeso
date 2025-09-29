package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ClientEntity;
import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Entity.StateUsersEntity;
import com.example.proyectotingeso.Repository.ClientRepository;
import com.example.proyectotingeso.Repository.LoanToolsRepository;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServices {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    StateUsersRepository stateUsersRepository;


    public ClientEntity createClient(ClientEntity clientEntity) {
        if (clientEntity.getState() == null) {
            clientEntity.setState(stateUsersRepository.findByName("Active").getId());
            return clientRepository.save(clientEntity);
        }
        return clientRepository.save(clientEntity);
    }

    public List<ClientEntity> getAllClients() {
        return clientRepository.findAll();
    }

    public ClientEntity getClientByRut(String rut) {
        return clientRepository.findByRut(rut);
    }

    public ClientEntity updateClient(ClientEntity clientEntity) {
        return clientRepository.save(clientEntity);
    }

    public boolean deleteClient(Long id) throws Exception {
        try {
            clientRepository.deleteById(id);
            return true;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ClientEntity getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

}

