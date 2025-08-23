package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    public UserEntity findByRut(String rut);

    public UserEntity findByEmail(String email);

}
