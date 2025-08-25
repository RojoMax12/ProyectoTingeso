package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Repository.LoanToolsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanToolsServices {

    @Autowired
    LoanToolsRepository loanToolsRepository;

    public LoanToolsEntity getLoanToolsEntityById(Long id) {
        return loanToolsRepository.findById(id).get();
    }

    public LoanToolsEntity CreateLoanToolsEntity(LoanToolsEntity loanToolsEntity) {
        return loanToolsRepository.save(loanToolsEntity);
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
