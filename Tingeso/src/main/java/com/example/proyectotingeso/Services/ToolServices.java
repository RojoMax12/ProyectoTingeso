package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolServices {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    StateToolsRepository stateToolsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public ToolEntity save(ToolEntity toolEntity) {
        if (toolEntity.getStates() == null){
            toolEntity.setStates(stateToolsRepository.findByName("Avaible").getId());
            return toolRepository.save(toolEntity);
        }
        return toolRepository.save(toolEntity);
    }

    public int inventory(ToolEntity toolEntity) {
        System.out.println(">>> DEBUG - Iniciando inventory()");

        // Buscar estado "Available"
        var state = stateToolsRepository.findByName("Avaible");
        if (state == null) {
            System.out.println(">>> ERROR - No existe un estado con nombre 'Avaible'");
            return 0;
        }
        System.out.println(">>> DEBUG - Estado encontrado: id=" + state.getId() + ", name=" + state.getName());

        // Buscar herramientas
        List<ToolEntity> tools = toolRepository.findAllByNameAndStates(toolEntity.getName(), state.getId());
        System.out.println(">>> DEBUG - Herramientas encontradas con name=" + toolEntity.getName() + ": " + tools.size());

        // Mostrar cada herramienta encontrada
        for (ToolEntity tool : tools) {
            System.out.println(">>> TOOL - id=" + tool.getId() +
                    ", name=" + tool.getName() +
                    ", category=" + tool.getCategory() +
                    ", states=" + tool.getStates());
        }

        return tools.size();
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

    public boolean deleteToolAdmin(Long iduser, Long idTool) throws Exception{
        if(userRepository.findById(iduser).isPresent() && toolRepository.findById(idTool).isPresent()){
            if(roleRepository.findById(userRepository.findById(iduser).get().getRole()).get().getName().equals("Admin")){
                deletetoolbyid(idTool);
                return true;
            }
            new IllegalArgumentException("No eres administrador");
            return false;
        }
        new IllegalArgumentException("No existe el usuario o la herramienta");
        return false;
    }

    public ToolEntity updateTool(ToolEntity toolEntity) {
        return toolRepository.save(toolEntity);
    }
}
