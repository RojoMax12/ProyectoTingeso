package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ReportEntity;
import com.example.proyectotingeso.Repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ReportServicesTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServices reportServices;

    @Test
    public void testCreateTopToolsReport() {
        // Datos de entrada simulados
        LocalDate today = LocalDate.now();
        ReportEntity report = new ReportEntity(1L, "ReportTopTools", today);

        // Simular la respuesta del repositorio
        when(reportRepository.save(report)).thenReturn(report);

        // Llamar al servicio
        List<ReportEntity> result = reportServices.createTopToolsReport();

        // Verificar que el servicio haya devuelto el reporte correcto
        assertEquals(1, result.size());
        assertEquals("ReportTopTools", result.get(0).getName());
    }

    @Test
    public void testGetAllReportClientLoanLate() {
        // Crear algunos reportes de ejemplo
        LocalDate today = LocalDate.now();
        ReportEntity report1 = new ReportEntity(1L, "ReportClientLoanLate", today);
        ReportEntity report2 = new ReportEntity(2L, "ReportClientLoanLate", today);

        // Simular la respuesta del repositorio
        when(reportRepository.findByName("ReportClientLoanLate")).thenReturn(Arrays.asList(report1, report2));

        // Llamar al servicio
        List<ReportEntity> result = reportServices.GetAllReportClientLoanLate();

        // Verificar que la lista de reportes es la esperada
        assertEquals(2, result.size());
        assertEquals("ReportClientLoanLate", result.get(0).getName());
        assertEquals("ReportClientLoanLate", result.get(1).getName());
    }

    @Test
    public void testReportLoanTools() {
        // Crear algunos reportes de ejemplo
        LocalDate today = LocalDate.now();
        ReportEntity report1 = new ReportEntity(1L, "ReportLoanTools", today);
        ReportEntity report2 = new ReportEntity(2L, "ReportLoanTools", today);

        // Simular la respuesta del repositorio
        when(reportRepository.findByName("ReportLoanTools")).thenReturn(Arrays.asList(report1, report2));

        // Llamar al servicio
        List<ReportEntity> result = reportServices.ReportLoanTools();

        // Verificar que la lista de reportes es la esperada
        assertEquals(2, result.size());
        assertEquals("ReportLoanTools", result.get(0).getName());
    }

    @Test
    public void testReportfilterDate() {
        // Crear algunos reportes de ejemplo
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);
        ReportEntity report1 = new ReportEntity(1L, "ReportLoanTools", start);
        ReportEntity report2 = new ReportEntity(2L, "ReportLoanTools", end);

        // Simular la respuesta del repositorio
        when(reportRepository.findByDateBetweenOrderByDateDesc(start, end)).thenReturn(Arrays.asList(report1, report2));

        // Llamar al servicio
        List<ReportEntity> result = reportServices.ReportfilterDate(start, end);

        // Verificar que la lista de reportes es la esperada
        assertEquals(2, result.size());
        assertEquals("ReportLoanTools", result.get(0).getName());
    }
}
