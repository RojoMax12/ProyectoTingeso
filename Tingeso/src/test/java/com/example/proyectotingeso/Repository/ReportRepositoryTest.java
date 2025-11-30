package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.ReportEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ReportRepositoryTest {

    @MockBean
    private ReportRepository reportRepository;

    @Test
    public void testFindByDateBetweenOrderByDateDesc() {
        // Crear algunos reportes de ejemplo
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);
        ReportEntity r1 = new ReportEntity(1L, "ReportLoanTools", start);
        ReportEntity r2 = new ReportEntity(2L, "ReportLoanTools", end);

        // Guardar los reportes en la base de datos
        reportRepository.saveAll(Arrays.asList(r1, r2));

        // Ejecutar el método findByDateBetweenOrderByDateDesc
        List<ReportEntity> reports = reportRepository.findByDateBetweenOrderByDateDesc(start, end);

        // Verificar que la lista de reportes es la esperada
        assertEquals(2, reports.size());
        assertEquals("ReportLoanTools", reports.get(0).getName());
    }

    @Test
    public void testFindByName() {
        // Crear un reporte de ejemplo
        ReportEntity report = new ReportEntity(1L, "ReportLoanTools", LocalDate.now());
        reportRepository.save(report);

        // Ejecutar el método findByName
        List<ReportEntity> reports = reportRepository.findByName("ReportLoanTools");

        // Verificar que el nombre de los reportes es correcto
        assertEquals(1, reports.size());
        assertEquals("ReportLoanTools", reports.get(0).getName());
    }
}
