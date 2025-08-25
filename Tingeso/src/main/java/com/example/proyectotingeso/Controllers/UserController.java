package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.UserEntity;
import com.example.proyectotingeso.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        UserEntity newUserEntity = userServices.saveUser(user);
        return ResponseEntity.ok(newUserEntity);
    }

    @GetMapping("/Alluser")
    public ResponseEntity<List<UserEntity>> getAllUser() {
        List<UserEntity> users = userServices.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/UpdateUser")
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user) {
        UserEntity newUserEntity = userServices.updateUser(user);
        return ResponseEntity.ok(newUserEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) throws Exception {
        var isDelete =  userServices.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody UserEntity user){
        var login = userServices.login(user);
        return ResponseEntity.ok(login);
    }
}
