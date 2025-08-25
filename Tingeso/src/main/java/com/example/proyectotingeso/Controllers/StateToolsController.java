package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.StateToolsEntity;
import com.example.proyectotingeso.Services.StateToolsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statetools")
@CrossOrigin("*")
public class StateToolsController {

    @Autowired
    StateToolsServices stateToolsServices;

    @PostMapping("/")
    public ResponseEntity<String> createStateTools() {
        String menssage = stateToolsServices.createStateTools();
        return ResponseEntity.ok(menssage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateToolsEntity> getStateToolsEntityById(Long id) {
        StateToolsEntity stateToolsEntity = stateToolsServices.getStateToolsEntityById(id);
        return ResponseEntity.ok(stateToolsEntity);
    }

    @PutMapping("/")
    public ResponseEntity<StateToolsEntity> updateStateToolsEntity(@RequestBody StateToolsEntity stateToolsEntity) {
        StateToolsEntity newStateToolsEntity = stateToolsServices.updateStateToolsEntity(stateToolsEntity);
        return ResponseEntity.ok(newStateToolsEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteStateToolsEntityById(@PathVariable Long id) throws Exception {
        var isDelete = stateToolsServices.deleteStateToolsById(id);
        return ResponseEntity.noContent().build();
    }
}
