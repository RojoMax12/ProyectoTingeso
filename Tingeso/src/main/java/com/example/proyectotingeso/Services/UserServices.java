package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Entity.UserEntity;
import com.example.proyectotingeso.Repository.RoleRepository;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import com.example.proyectotingeso.Repository.ToolRepository;
import com.example.proyectotingeso.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StateUsersRepository stateUsersRepository;

    @Autowired

    ToolRepository toolRepository;

    public ArrayList<UserEntity> getAllUsers() {

        return (ArrayList<UserEntity>) userRepository.findAll();
    }

    public UserEntity getUserByRut(String rut) {

        return userRepository.findByRut(rut);
    }

    public boolean login(UserEntity user) {
        UserEntity existingUser = userRepository.findByRut(user.getRut());

        if (existingUser == null) {
            return false; // No existe el usuario
        }

        // Comparar email
        if (!existingUser.getEmail().equals(user.getEmail())) {
            return false;
        }

        // Comparar contraseña (versión insegura, solo para pruebas)
        if (!existingUser.getPassword().equals(user.getPassword())) {
            return false;
        }

        return true;
    }

    public UserEntity saveUser(UserEntity user) {
        // Si no viene con rol → asignar Client por defecto
        if (user.getRole() == null) {
            var defaultRole = roleRepository.findByName("Client");
            if (defaultRole == null) {
                throw new IllegalStateException("El rol por defecto 'Client' no está inicializado en la base de datos");
            }
            user.setRole(defaultRole.getId());

            // También estado por defecto
            var activeState = stateUsersRepository.findByName("Active");
            if (activeState == null) {
                throw new IllegalStateException("El estado por defecto 'Active' no está inicializado en la base de datos");
            }
            user.setState(activeState.getId());
        }
        else {
            // Si viene con rol, validar que existe
            if (!roleRepository.findById(user.getRole()).isPresent()) {
                throw new IllegalArgumentException("El rol con id " + user.getRole() + " no existe");
            }
        }
        // Guardar usuario (única llamada a save)
        return userRepository.save(user);
    }

    public void Changereplacement_costTool(String nametool, Long Userid, int cost) {
        UserEntity user = userRepository.findById(Userid).get();
        if (roleRepository.findById(user.getRole()).get().getName().equals("Admin")) {
            List<ToolEntity> tools = toolRepository.findAllByName(nametool);
            for (ToolEntity tool : tools) {
                tool.setReplacement_cost(cost);
            }
            toolRepository.saveAll(tools);
        }
        else {
            throw new IllegalArgumentException("No eres administrador");
        }
    }

    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }

    public boolean deleteUser(Long id) throws Exception {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}




