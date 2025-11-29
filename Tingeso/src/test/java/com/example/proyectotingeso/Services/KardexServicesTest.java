package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.KardexEntity;
import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Repository.KardexRepository;
import com.example.proyectotingeso.Repository.ToolRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KardexServicesTest {

    @Mock
    private KardexRepository kardexRepository;

    @Mock
    private ToolRepository toolRepository;

    @InjectMocks
    private KardexServices kardexServices;

    @Test
    public void testSave() {
        // Given: Simulamos un objeto KardexEntity
        KardexEntity kardexEntity = new KardexEntity(1L, 1L, LocalDate.now(), "pepito", 10L, 1);

        // When: Llamamos al método save
        when(kardexRepository.save(kardexEntity)).thenReturn(kardexEntity);

        // Then: Verificamos que el resultado sea el esperado
        KardexEntity result = kardexServices.save(kardexEntity);
        assertNotNull(result);
        assertEquals(kardexEntity.getId(), result.getId());
    }

    @Test
    public void testFindAll() {
        // Given: Simulamos algunos objetos KardexEntity
        KardexEntity kardex1 = new KardexEntity(1L, 1L, LocalDate.now(), "Pepito", 10L, 1);
        KardexEntity kardex2 = new KardexEntity(2L, 1L, LocalDate.now().minusDays(1), "Rodrigo", 20L, 1);

        // When: Llamamos al método findAll
        when(kardexRepository.findAll()).thenReturn(Arrays.asList(kardex1, kardex2));

        // Then: Verificamos que la lista de Kardex no sea nula y tenga el tamaño correcto
        List<KardexEntity> result = kardexServices.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdate() {
        // Given: Simulamos un objeto KardexEntity
        KardexEntity kardexEntity = new KardexEntity(1L, 1L, LocalDate.now(), "Pepito", 10L, 1);
        // When: Llamamos al método update
        when(kardexRepository.save(kardexEntity)).thenReturn(kardexEntity);

        // Then: Verificamos que el resultado sea el esperado
        KardexEntity result = kardexServices.Update(kardexEntity);
        assertNotNull(result);
        assertEquals(kardexEntity.getId(), result.getId());
    }

    @Test
    public void testDelete() throws Exception {
        // Given: El id de un KardexEntity
        Long id = 1L;

        // When: Llamamos al método delete
        doNothing().when(kardexRepository).deleteById(id);

        // Then: Verificamos que no se lance ninguna excepción
        boolean result = kardexServices.delete(id);
        assertTrue(result);
        verify(kardexRepository, times(1)).deleteById(id);
    }

    @Test
    public void testHistoryKardexTool() {
        // Given: Simulamos los datos de entrada
        ToolEntity tool = new ToolEntity(1L, "Hammer", "Tools", 50, 1L);
        KardexEntity kardex1 = new KardexEntity(1L, 1L, LocalDate.now(), "Pepito", 10L, 1);
        KardexEntity kardex2 = new KardexEntity(2L, 1L, LocalDate.now().minusDays(1), "active", 12L, 2);

        // When: Llamamos al método HistoryKardexTool
        when(toolRepository.findAllByName("Hammer")).thenReturn(Arrays.asList(tool));
        when(kardexRepository.findAllByIdtool(1L)).thenReturn(Arrays.asList(kardex1, kardex2));

        // Then: Verificamos el resultado
        List<KardexEntity> history = kardexServices.HistoryKardexTool("Hammer");
        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    public void testTopToolKardexTool() {
        // Given: Simulamos los datos de entrada
        Object[] toolData1 = new Object[]{1L, "Hammer", 5L};
        Object[] toolData2 = new Object[]{2L, "Wrench", 3L};

        // When: Llamamos al método TopToolKardexTool
        when(kardexRepository.getTopTools()).thenReturn(Arrays.asList(toolData1, toolData2));

        // Then: Verificamos el resultado
        List<Object[]> topTools = kardexServices.TopToolKardexTool();
        assertNotNull(topTools);
        assertEquals(2, topTools.size());
        assertEquals("Hammer", topTools.get(0)[1]);
        assertEquals(5L, topTools.get(0)[2]);
    }

    @Test
    public void testHistoryKardexDateInitandDateFin() {
        // Given: Simulamos los datos de entrada
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        KardexEntity kardex1 = new KardexEntity(1L, 1L, LocalDate.now(), "Pepito", 10L, 1);
        // When: Llamamos al método HistoryKardexDateInitandDateFin
        when(kardexRepository.findByDateBetweenOrderByDateDesc(start, end)).thenReturn(Arrays.asList(kardex1));

        // Then: Verificamos el resultado
        List<KardexEntity> history = kardexServices.HistoryKardexDateInitandDateFin(start, end);
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(kardex1, history.get(0));
    }
}
