package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Services.LoanToolsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/LoanTools")
public class LoanToolsController {

    @Autowired
    LoanToolsServices loanToolsServices;

    @PostMapping("/")
    public ResponseEntity<LoanToolsEntity> createLoanTools(@RequestBody LoanToolsEntity loanToolsEntity) {
        LoanToolsEntity newLoanToos = loanToolsServices.CreateLoanToolsEntity(loanToolsEntity);
        return ResponseEntity.ok(newLoanToos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanToolsEntity> getLoanTools(@PathVariable Long id) {
        LoanToolsEntity newLoanTools = loanToolsServices.getLoanToolsEntityById(id);
        return ResponseEntity.ok(newLoanTools);

    }

    @PutMapping("/return/{iduser}/{idloantools}")
    public ResponseEntity<LoanToolsEntity> returnLoanTools(@PathVariable Long iduser, @PathVariable Long idloantools) {
        LoanToolsEntity newLoanTools = loanToolsServices.returnLoanTools(iduser, idloantools);
        return ResponseEntity.ok(newLoanTools);
    }


    @PutMapping("/")
    public ResponseEntity<LoanToolsEntity> updateLoanTools(@RequestBody LoanToolsEntity loanToolsEntity) {
        LoanToolsEntity newLoanTools = loanToolsServices.UpdateLoanToolsEntity(loanToolsEntity);
        return ResponseEntity.ok(newLoanTools);
    }

    @DeleteMapping("/")
    public ResponseEntity<Boolean> deleteLoanTools(Long id) throws Exception {
        var isDeleted = loanToolsServices.DeleteLoanToolsEntity(id);
        return ResponseEntity.noContent().build();
    }
}
