package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Entity.StateToolsEntity;
import com.example.proyectotingeso.Entity.StateUsersEntity;
import com.example.proyectotingeso.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanToolsServices {

    @Autowired
    LoanToolsRepository loanToolsRepository;

    @Autowired
    ToolRepository toolRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StateToolsRepository stateToolsRepository;

    @Autowired

    StateUsersRepository stateUsersRepository;

    public LoanToolsEntity getLoanToolsEntityById(Long id) {
        return loanToolsRepository.findById(id).get();
    }

    public LoanToolsEntity CreateLoanToolsEntity(LoanToolsEntity loanToolsEntity) {
        List<StateToolsEntity> states = stateToolsRepository.findAll();
        if(userRepository.findById(loanToolsEntity.getUserid()).isPresent() && userRepository.findById(loanToolsEntity.getUserid()).get().getState() == stateUsersRepository.findByName("Active").getId() ) {
            if(toolRepository.findById(loanToolsEntity.getToolid()).isPresent() && toolRepository.findById(loanToolsEntity.getToolid()).get().getStates() == states.get(0).getId()) {
                loanToolsEntity.setToolid(toolRepository.findById(loanToolsEntity.getToolid()).get().getId());
                loanToolsEntity.setUserid(userRepository.findById(loanToolsEntity.getUserid()).get().getId());
                toolRepository.findById(loanToolsEntity.getToolid()).get().setStates(states.get(1).getId());
                return loanToolsRepository.save(loanToolsEntity);
            }
            throw new IllegalArgumentException("La herramienta no esta disponible");
        }
        throw new IllegalArgumentException("El usuario no existe");
    }

    public int calculatefine(LoanToolsEntity loanToolsEntity){
        int latefine = 0;
        int Fine_for_irreparable_damage = 0;

        return 0;
    }


    //Se cambia el estado de la herramienta actual a que esta disponble, falta actualizar en el kardex
    public LoanToolsEntity returnLoanTools(Long userid, Long toolid) {
        // Buscar usuario
        var user = userRepository.findById(userid)
                .orElseThrow(() -> new IllegalArgumentException("El usuario con id " + userid + " no existe"));

        // Buscar herramienta
        var tool = toolRepository.findById(toolid)
                .orElseThrow(() -> new IllegalArgumentException("La herramienta con id " + toolid + " no existe"));

        // Buscar préstamo asociado
        var loan = loanToolsRepository.findByUseridAndToolid(userid, toolid)
                .orElseThrow(() -> new IllegalArgumentException("No existe un préstamo para este usuario y herramienta"));

        // Cambiar estado de la herramienta a "Disponible" (por ejemplo el primero en la lista)
        List<StateToolsEntity> states = stateToolsRepository.findAll();
        if (states.isEmpty()) {
            throw new IllegalStateException("No hay estados configurados en la BD");
        }

        tool.setStates(states.get(0).getId()); // disponible
        toolRepository.save(tool);

        return loanToolsRepository.save(loan);
    }

    public LoanToolsEntity UpdateLoanToolsEntity(LoanToolsEntity loanToolsEntity) {
        return loanToolsRepository.save(loanToolsEntity);
    }

    public boolean DeleteLoanToolsEntity(Long id) throws Exception{
        try {
            loanToolsRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
