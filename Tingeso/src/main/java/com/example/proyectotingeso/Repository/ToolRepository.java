package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
}
