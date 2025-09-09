package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.KardexEntity;
import com.example.proyectotingeso.Entity.StateToolsEntity;
import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Entity.UserEntity;
import com.example.proyectotingeso.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ToolServices {

    @Autowired
    ToolRepository toolRepository;

    @Autowired
    StateToolsRepository stateToolsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    KardexRepository kardexRepository;

    @Autowired
    LoanToolsRepository loanToolsRepository;

    public ToolEntity save(ToolEntity toolEntity) {
        LocalDate date = LocalDate.now();
        if (toolEntity.getStates() == null) {
            var availableState = stateToolsRepository.findByName("Available");
            if (availableState == null) {
                throw new IllegalStateException("El estado 'Available' no está inicializado en la base de datos");
            }

            toolEntity.setStates(availableState.getId());
        }


        // Guardar primero la herramienta para que genere ID
        ToolEntity savedTool = toolRepository.save(toolEntity);
        return savedTool;
    }

    public int inventory(ToolEntity toolEntity) {
        System.out.println(">>> DEBUG - Iniciando inventory()");

        // Buscar estado "Available"
        var state = stateToolsRepository.findByName("Available");
        if (state == null) {
            System.out.println(">>> ERROR - No existe un estado con nombre 'Available'");
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

    public ToolEntity unsubscribeToolAdmin(Long iduser, Long idTool) throws Exception{
        ToolEntity tool = toolRepository.findById(idTool).get();
        //KardexEntity kardex = new KardexEntity(null, loanToolsRepository.findByToolid(idTool).get().getId(), tool.getId());
        if(userRepository.findById(iduser).isPresent() && toolRepository.findById(idTool).isPresent()){
            if(roleRepository.findById(userRepository.findById(iduser).get().getRole()).get().getName().equals("Admin")){
                tool.setStates(stateToolsRepository.findByName("Discharged").getId());
                //kardexRepository.save(kardex);
                return toolRepository.save(tool);
            }
            new IllegalArgumentException("No eres administrador");
            return toolRepository.save(tool);
        }
        new IllegalArgumentException("No existe el usuario o la herramienta");
        return toolRepository.save(tool);
    }

    public ToolEntity borrowedTool(Long idTool) throws Exception{
        ToolEntity tool = toolRepository.findById(idTool).get();
        //KardexEntity kardex = new KardexEntity(null, loanToolsRepository.findByToolid(idTool).get().getId(), tool.getId());
        if(toolRepository.findById(idTool).isPresent()){
            tool.setStates(stateToolsRepository.findByName("Borrowed").getId());
            //kardexRepository.save(kardex);
            return toolRepository.save(tool);
        }
        new IllegalArgumentException("No existe la herramienta");
        return toolRepository.save(tool);

    }

    public ToolEntity inrepair(Long idTool) throws Exception{
        ToolEntity tool = toolRepository.findById(idTool).get();
        //KardexEntity kardex = new KardexEntity(null, loanToolsRepository.findByToolid(idTool).get().getId(), tool.getId());
        if(toolRepository.findById(idTool).isPresent()){
            tool.setStates(stateToolsRepository.findByName("InRepair").getId());
            //kardexRepository.save(kardex);
            return toolRepository.save(tool);
        }
        new IllegalArgumentException("No existe la herramienta");
        return toolRepository.save(tool);
    }

    public ToolEntity updateTool(ToolEntity toolEntity) {
        ToolEntity tool = toolRepository.findById(toolEntity.getId()).get();
        ToolEntity newtool = new ToolEntity(tool.getId(), toolEntity.getName(), toolEntity.getCategory(), toolEntity.getReplacement_cost(), tool.getStates());
        return toolRepository.save(newtool);
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
}
