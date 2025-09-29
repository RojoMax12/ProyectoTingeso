package com.example.proyectotingeso.Controllers;


import com.example.proyectotingeso.Entity.ReportEntity;
import com.example.proyectotingeso.Services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    ReportServices reportServices;

    @PreAuthorize(("hasAnyRole('USER','ADMIN')"))
    @PostMapping("/ReportLoan")
    public ResponseEntity<List<ReportEntity>> createreport(){
        List<ReportEntity> reports = reportServices.ReportLoanTools();
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize(("hasAnyRole('USER','ADMIN')"))
    @GetMapping("/Reports/{initdate}/{findate}")
    public ResponseEntity<List<ReportEntity>> getreportbydate(@PathVariable LocalDate initdate, @PathVariable LocalDate findate){
        List<ReportEntity> reports = reportServices.ReportfilterDate(initdate,findate);
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize(("hasAnyRole('USER','ADMIN')"))
    @GetMapping("/AllReports")
    public ResponseEntity<List<ReportEntity>> getreport(){
        List<ReportEntity> reports = reportServices.ReportLoanTools();
        return ResponseEntity.ok(reports);
    }
}
