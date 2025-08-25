package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.UserEntity;
import com.example.proyectotingeso.Repository.RoleRepository;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import com.example.proyectotingeso.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServices {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StateUsersRepository stateUsersRepository;

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
        // Si no viene con rol → asignar Employer por defecto
        if (user.getRole() == null) {
            var defaultRole = roleRepository.findByName("Employer");
            if (defaultRole == null) {
                throw new IllegalStateException("El rol por defecto 'Employer' no está inicializado en la base de datos");
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




