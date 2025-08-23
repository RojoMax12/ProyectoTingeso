package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.RoleEntity;
import com.example.proyectotingeso.Services.RoleServices;
import com.example.proyectotingeso.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
public class RolesController {

    @Autowired
    RoleServices roleServices;

    @PostMapping("/")
    public ResponseEntity<RoleEntity> createRole(@RequestBody RoleEntity role) {
        RoleEntity newRole = roleServices.createRole(role);
        return ResponseEntity.ok(newRole);
    }
}
