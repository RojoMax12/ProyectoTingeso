package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    public ClientEntity findByRut(String rut);
}
