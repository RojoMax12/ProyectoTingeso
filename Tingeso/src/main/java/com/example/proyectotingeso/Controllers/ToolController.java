package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Services.ToolServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Tools")
public class ToolController {

    @Autowired
    ToolServices toolServices;

    @PostMapping("/")
    public ResponseEntity<ToolEntity> addtool(@RequestBody ToolEntity toolEntity) {
        ToolEntity newToolsEntity = toolServices.save(toolEntity);
        return ResponseEntity.ok(newToolsEntity);
    }

    @GetMapping("/alltools")
    public ResponseEntity<List<ToolEntity>> getAlltools() {
        List<ToolEntity> tools = toolServices.getAlltool();
        return ResponseEntity.ok(tools);
    }

    @GetMapping("tool/{id}")
    public ResponseEntity<ToolEntity> gettool(@PathVariable Long id) {
        ToolEntity tool = toolServices.getTool(id);
        return ResponseEntity.ok(tool);
    }

    @PutMapping("/UpdateTool")
    public ResponseEntity<ToolEntity> updatetool(@RequestBody ToolEntity toolEntity) {
        ToolEntity newtoolEntity = toolServices.updateTool(toolEntity);
        return ResponseEntity.ok(newtoolEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletetool(@PathVariable Long id) throws Exception {
        var isDelete = toolServices.deletetoolbyid(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/inventory")
    public ResponseEntity<Integer> getinvetory(@RequestBody ToolEntity toolEntity) {
        int inv = toolServices.inventory(toolEntity);
        return ResponseEntity.ok(inv);
    }

    @DeleteMapping("/{iduser}/{idtool}")
    public ResponseEntity<Boolean> deletetoolAdmin(@PathVariable Long iduser, @PathVariable Long idtool) throws Exception {
        var isDelete = toolServices.deleteToolAdmin(iduser,idtool);
        return ResponseEntity.noContent().build();
    }
}
