package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {

    public ToolEntity findByName(String name);

    public Optional<ToolEntity> findById(Long id);
}
