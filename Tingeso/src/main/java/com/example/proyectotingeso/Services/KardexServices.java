package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.KardexEntity;
import com.example.proyectotingeso.Repository.KardexRepository;
import com.example.proyectotingeso.Repository.LoanToolsRepository;
import com.example.proyectotingeso.Repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KardexServices {

    @Autowired
    private KardexRepository kardexRepository;

    @Autowired
    private LoanToolsRepository loanToolsRepository;

    @Autowired
    private ToolRepository ToolRepository;


    public KardexEntity save(KardexEntity kardexEntity) {
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

    public List<KardexEntity> HistoryKardexTool(String nameTool) {
        if (ToolRepository.findByName(nameTool) != null) {
            Long toolId = ToolRepository.findByName(nameTool).getId();
            return kardexRepository.findAllByIdtool(toolId);
        } else {
            throw new IllegalArgumentException("No existe la herramienta con nombre: " + nameTool);
        }
    }


    public List<KardexEntity> HistoryKardexDateInitandDateFin(LocalDate init, LocalDate fin) {
        return kardexRepository.findByDateBetweenOrderByDateDesc(init, fin);
    }
}
