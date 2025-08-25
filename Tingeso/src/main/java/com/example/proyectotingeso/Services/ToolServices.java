package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Repository.StateToolsRepository;
import com.example.proyectotingeso.Repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolServices {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    StateToolsRepository stateToolsRepository;

    public ToolEntity save(ToolEntity toolEntity) {
        if (toolEntity.getStates() == null){
            toolEntity.setStates(stateToolsRepository.findByName("Avaible").getId());
            return toolRepository.save(toolEntity);
        }
        return toolRepository.save(toolEntity);
    }

    public List<ToolEntity> getAlltool() {
        return toolRepository.findAll();
    }

    public ToolEntity getTool(Long id) {
        return toolRepository.findById(id).get();
    }

    public boolean deletetoolbyid(Long id) throws Exception{
        try {
            toolRepository.deleteById(id);
            return true;
        }
        catch(Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public ToolEntity updateTool(ToolEntity toolEntity) {
        return toolRepository.save(toolEntity);
    }
}
