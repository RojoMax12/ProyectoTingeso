package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.StateToolsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateToolsRepository extends JpaRepository<StateToolsEntity, Long> {
}
