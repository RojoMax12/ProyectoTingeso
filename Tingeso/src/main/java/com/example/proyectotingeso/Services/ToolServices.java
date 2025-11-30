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
import java.util.Optional;

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


    public ToolEntity save(ToolEntity toolEntity) {
        // 1. Validar datos de entrada
        if (toolEntity.getName() == null || toolEntity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la herramienta es obligatorio");
        }
        if (toolEntity.getCategory() == null || toolEntity.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría de la herramienta es obligatoria");
        }
        if (toolEntity.getReplacement_cost() <= 0) {
            throw new IllegalArgumentException("El costo de reemplazo debe ser mayor a 0");
        }

        // 2. Crear nueva herramienta con datos base
        ToolEntity newTool = new ToolEntity();
        newTool.setName(toolEntity.getName().trim());
        newTool.setCategory(toolEntity.getCategory().trim());
        newTool.setReplacement_cost(toolEntity.getReplacement_cost());

        // 3. Buscar herramienta existente con el mismo nombre (OPCIONAL - para autocompletado)
        Optional<ToolEntity> existingToolOpt = toolRepository.findFirstByNameOrderByName(toolEntity.getName().trim());

        if (existingToolOpt.isPresent()) {
            ToolEntity existingTool = existingToolOpt.get();
            System.out.println("💡 Herramienta similar encontrada: " + existingTool.getName());

            // Opcional: Si el frontend no envió costo y existe una herramienta similar, usar su costo
            if (toolEntity.getReplacement_cost() == 0) {
                newTool.setReplacement_cost(existingTool.getReplacement_cost());
                System.out.println("🔄 Autocompletando costo: $" + existingTool.getReplacement_cost());
            }

            // Opcional: Si el frontend no envió categoría y existe una herramienta similar, usar su categoría
            if (toolEntity.getCategory() == null || toolEntity.getCategory().trim().isEmpty()) {
                newTool.setCategory(existingTool.getCategory());
                System.out.println("🔄 Autocompletando categoría: " + existingTool.getCategory());
            }
        } else {
            System.out.println("ℹ️ Nueva herramienta única: " + toolEntity.getName());
        }

        // 4. Configurar estado de la herramienta
        if (toolEntity.getStates() != null) {
            newTool.setStates(toolEntity.getStates());
        } else {
            // Estado por defecto: Available
            StateToolsEntity availableState = stateToolsRepository.findByName("Available");
            if (availableState == null) {
                throw new IllegalStateException("El estado 'Available' no está inicializado en la base de datos");
            }
            newTool.setStates(availableState.getId());
        }

        // 5. Guardar la nueva herramienta
        ToolEntity savedTool = toolRepository.save(newTool);

        System.out.println("✅ Herramienta guardada exitosamente: " +
                "ID=" + savedTool.getId() +
                ", Nombre=" + savedTool.getName() +
                ", Categoría=" + savedTool.getCategory() +
                ", Costo=$" + savedTool.getReplacement_cost());

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

    public ToolEntity unsubscribeToolAdmin(Long idTool) throws Exception {
        // Buscar la herramienta por ID
        ToolEntity tool = toolRepository.findById(idTool)
                .orElseThrow(() -> new IllegalArgumentException("No existe la herramienta"));

        // Buscar el estado "Discharged" en el repositorio de estados
        StateToolsEntity dischargedState = stateToolsRepository.findByName("Discharged");
        if (dischargedState == null) {
            throw new IllegalStateException("El estado 'Discharged' no está configurado en la base de datos");
        }

        // Actualizar el estado de la herramienta a "Discharged"
        tool.setStates(dischargedState.getId());

        // Guardar la herramienta con el nuevo estado
        return toolRepository.save(tool);
    }



    public ToolEntity borrowedTool(Long idTool) throws Exception {
        // Verificar si la herramienta existe
        ToolEntity tool = toolRepository.findById(idTool)
                .orElseThrow(() -> new IllegalArgumentException("No existe la herramienta"));

        // Buscar el estado "Borrowed" en el repositorio
        StateToolsEntity borrowedState = stateToolsRepository.findByName("Borrowed");
        if (borrowedState == null) {
            throw new IllegalStateException("El estado 'Borrowed' no está configurado en la base de datos");
        }

        // Actualizar el estado de la herramienta a "Borrowed"
        tool.setStates(borrowedState.getId());

        // Guardar la herramienta actualizada
        return toolRepository.save(tool);
    }


    public ToolEntity inrepair(Long idTool) throws Exception{
        ToolEntity tool = toolRepository.findById(idTool).get();
        //KardexEntity kardex = new KardexEntity(null, loanToolsRepository.findByToolid(idTool).get().getId(), tool.getId());
        if(toolRepository.findById(idTool).isPresent()){
            tool.setStates(stateToolsRepository.findByName("In repair").getId());
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
