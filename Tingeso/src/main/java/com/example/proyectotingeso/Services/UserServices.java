package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.UserEntity;
import com.example.proyectotingeso.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServices {

    @Autowired
    UserRepository userRepository;

    public ArrayList<UserEntity> getAllUsers() {

        return (ArrayList<UserEntity>) userRepository.findAll();
    }

    public UserEntity getUserByRut(String rut) {

        return userRepository.findByRut(rut);
    }

    public UserEntity saveUser(UserEntity user) {
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




