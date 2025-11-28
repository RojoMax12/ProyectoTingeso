package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ClientEntity;
import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Entity.ReportEntity;
import com.example.proyectotingeso.Repository.ReportRepository;
import com.fasterxml.jackson.core.util.RecyclerPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReportServices {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    LoanToolsServices loanToolsServices;

    @Autowired
    KardexServices kardexServices;

    @Autowired
    private ClientServices clientServices;




    public ReportEntity createReport(ReportEntity reportEntity) {
        return reportRepository.save(reportEntity);
    }

    public List<ReportEntity> ReportLoanTools(){
        List<LoanToolsEntity> loantools = loanToolsServices.findallloanstoolstatusandRentalFee();
        System.out.println("Préstamos encontrados: " + loantools);
        LocalDate date = LocalDate.now();

        // Filtrar solo los préstamos que NO tienen reporte ya creado
        List<ReportEntity> reports = loantools.stream()
                .filter(loan -> {
                    // Verificar si ya existe un reporte para este préstamo
                    boolean exists = reportRepository.existsByIdLoanTool(loan.getId());
                    if (exists) {
                        System.out.println("Reporte ya existe para préstamo ID: " + loan.getId());
                    }
                    return !exists;
                })
                .map(loan -> {
                    ReportEntity reportEntity = new ReportEntity();
                    reportEntity.setIdLoanTool(loan.getId());
                    reportEntity.setName("ReportLoanTools");
                    reportEntity.setDate(date);
                    return reportEntity;
                }).toList();

        System.out.println("Nuevos reportes a crear: " + reports);

        if (reports.isEmpty()) {
            System.out.println("No hay préstamos nuevos para generar reportes");
            return new ArrayList<>();
        }

        return reportRepository.saveAll(reports);
    }

    public List<ReportEntity> ReportClientLoanLate(){
        List<ClientEntity> clients = clientServices.getAllClientLoanLate();
        List<LoanToolsEntity> loantools = loanToolsServices.findallloanstoolstatusLate();

        List<ReportEntity> reports = clients.stream().filter(
                client ->{
                    boolean exist = reportRepository.existsByIdClient(client.getId());
                    if (exist){
                        System.out.println("Reporte ya existe para cliente ID: " + client.getId());
                    }
                    return !exist;
                }).map(client -> {
                    ReportEntity reportEntity = new ReportEntity();
                      reportEntity.setIdClient(client.getId());
                      reportEntity.setName("ReportClientLoanLate");
                      reportEntity.setDate(LocalDate.now());
                      return reportEntity;
        }).toList();

        if (reports.isEmpty()){
            System.out.println("No hay Clientes nuevos para generar reportes");
            return new ArrayList<>();
        }
        return reportRepository.saveAll(reports);

    }

    public List<ReportEntity> createTopToolsReport() {

        List<Object[]> ranking = kardexServices.TopToolKardexTool();
        List<ReportEntity> createdReports = new ArrayList<>();

        for (Object[] row : ranking) {

            Long toolId = ((Number) row[0]).longValue();   // idTool

            // Crear reporte
            ReportEntity newReport = new ReportEntity();
            newReport.setIdTool(toolId);
            newReport.setName("ReportTopTools");
            newReport.setDate(LocalDate.now());

            // Guardar en BD
            ReportEntity saved = reportRepository.save(newReport);

            createdReports.add(saved);
        }

        return createdReports;
    }



    public List<ReportEntity> ReportfilterDate(LocalDate initdate, LocalDate findate){
        List<ReportEntity> reports = reportRepository.findByDateBetweenOrderByDateDesc(initdate, findate);
        return reports;
    }

    public List<ReportEntity> ReportTopToolsAll(){
        List<ReportEntity> reports = reportRepository.findByName("ReportTopTools");
        return reports;
    }

    public List<ReportEntity> GetAllReportClientLoanLate(){
        List<ReportEntity> reports = reportRepository.findByName("ReportClientLoanLate");
        return reports;
    }

    public List<ReportEntity> GetAllReportLoanTools(){
        List<ReportEntity> reports = reportRepository.findByName("ReportLoanTools");
        return reports;
    }



}
