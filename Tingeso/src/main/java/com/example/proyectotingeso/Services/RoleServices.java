package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.RoleEntity;
import com.example.proyectotingeso.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServices {

    @Autowired
    private RoleRepository roleRepository;


    public RoleEntity createRole(RoleEntity role) {
        return roleRepository.save(role);
    }

    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<RoleEntity> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public boolean deleteRole(Long id) throws Exception {
        try {
            roleRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}
