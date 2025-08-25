package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.StateToolsEntity;
import com.example.proyectotingeso.Repository.StateToolsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateToolsServices {

    @Autowired
    StateToolsRepository StateToolsRepository;

    public String createStateTools() {
        boolean created = false;
        if (StateToolsRepository.findByName("Avaible") == null) {
            StateToolsEntity stateToolsEntity = new StateToolsEntity(null, "Avaible");
            StateToolsRepository.save(stateToolsEntity);
            created = true;
        }
        if(StateToolsRepository.findByName("Borrowed") == null) {
            StateToolsEntity stateToolsEntity = new StateToolsEntity(null, "Borrowed");
            StateToolsRepository.save(stateToolsEntity);
            created = true;
        }
        if(StateToolsRepository.findByName("In repair") == null) {
            StateToolsEntity stateToolsEntity = new StateToolsEntity(null, "In repair");
            StateToolsRepository.save(stateToolsEntity);
            created = true;
        }
        if(StateToolsRepository.findByName("Discharged") == null) {
            StateToolsEntity stateToolsEntity = new StateToolsEntity(null, "Discharged");
            StateToolsRepository.save(stateToolsEntity);
            created = true;
        }
        if(created) {
            return "Estados de herramientas creado";
        }
        else {
            return "Estados de herramientas ya iniciados";
        }

    }

    public StateToolsEntity getStateToolsEntityById(Long id) {
        return StateToolsRepository.findById(id).get();
    }

    public StateToolsEntity updateStateToolsEntity(StateToolsEntity stateToolsEntity) {
        return StateToolsRepository.save(stateToolsEntity);
    }

    public boolean deleteStateToolsById(Long id) throws Exception {
        try {
            StateToolsRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }
}
