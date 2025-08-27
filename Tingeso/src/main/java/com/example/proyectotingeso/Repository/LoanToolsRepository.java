package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.LoanToolsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanToolsRepository extends JpaRepository<LoanToolsEntity, Long> {

    public Optional<LoanToolsEntity> findById(Long id);

    public Optional<LoanToolsEntity> findByUseridAndToolid(Long userid, Long toolid);

    public LoanToolsEntity findByInitiallenddate(LocalDate initiallenddate);

    public List<LoanToolsEntity> findByInitiallenddateBefore(LocalDate initiallenddate);

    public List<LoanToolsEntity> findByFinalreturndate(LocalDate finalreturndate);
}
