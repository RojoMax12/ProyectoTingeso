package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Entity.ReportEntity;
import com.example.proyectotingeso.Repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ReportServices {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    LoanToolsServices loanToolsServices;

    @Autowired
    ToolServices toolServices;



    public ReportEntity createReport(ReportEntity reportEntity) {
        return reportRepository.save(reportEntity);
    }

    public List<ReportEntity> ReportLoanTools(){
        List<LoanToolsEntity> loantools = loanToolsServices.findallloanstoolstatusandRentalFee();
        LocalDate date = LocalDate.now();

        List<ReportEntity> reports = loantools.stream().map(loan->{
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setIdLoanTool(loan.getId());
            reportEntity.setName("ReportLoanTools");
            reportEntity.setDate(date);
            return reportEntity;
        }).toList();

        return reportRepository.saveAll(reports); // <-- guarda todo en una sola query
    }

    public List<ReportEntity> ReportfilterDate(LocalDate initdate, LocalDate findate){
        List<ReportEntity> reports = reportRepository.findByDateBetweenOrderByDateDesc(initdate, findate);
        return reports;
    }

    public List<ReportEntity> GetAllReport(){
        return reportRepository.findAll();
    }

}
