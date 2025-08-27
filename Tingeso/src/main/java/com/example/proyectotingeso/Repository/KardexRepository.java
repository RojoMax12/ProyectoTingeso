package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity, Long> {

    public Optional<KardexEntity> findById(Long id);
}
