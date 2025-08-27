package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.KardexEntity;
import com.example.proyectotingeso.Repository.KardexRepository;
import com.example.proyectotingeso.Repository.LoanToolsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KardexServices {

    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private LoanToolsRepository loanToolsRepository;

    public KardexEntity save(KardexEntity kardexEntity) {
        loanToolsRepository.findById(kardexEntity.getLoanToolsId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el préstamo con id " + kardexEntity.getLoanToolsId() + " en el kardex"));

        return kardexRepository.save(kardexEntity);
    }

    public List<KardexEntity> findAll() {
        return kardexRepository.findAll();
    }

    public KardexEntity Update(KardexEntity kardexEntity) {
        return kardexRepository.save(kardexEntity);
    }

    public boolean delete(Long id) throws Exception{
        try {
            kardexRepository.deleteById(id);
            return true;
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
