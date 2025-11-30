package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Entity.StateToolsEntity;
import com.example.proyectotingeso.Repository.ToolRepository;
import com.example.proyectotingeso.Repository.StateToolsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class ToolServicesTest {

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private StateToolsRepository stateToolsRepository;

    @InjectMocks
    private ToolServices toolServices;

    private ToolEntity toolEntity;
    private StateToolsEntity availableState;

    @BeforeEach
    void setUp() {
        // Configurar un estado disponible
        availableState = new StateToolsEntity(1L, "Available");

        // Configurar la herramienta
        toolEntity = new ToolEntity();
        toolEntity.setId(1L);
        toolEntity.setName("Hammer");
        toolEntity.setCategory("Hand Tools");
        toolEntity.setReplacement_cost(100);
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave_whenToolIsValid_thenSaveTool() {
        // Simula el comportamiento del repositorio para la creación de la herramienta
        when(stateToolsRepository.findByName("Available")).thenReturn(availableState);
        when(toolRepository.save(any(ToolEntity.class))).thenReturn(toolEntity);

        // Llama al método save del servicio
        ToolEntity result = toolServices.save(toolEntity);

        // Verifica que el resultado sea el esperado
        assertNotNull(result);
        assertEquals("Hammer", result.getName());
        assertEquals("Hand Tools", result.getCategory());
        assertEquals(100.0, result.getReplacement_cost());

        // Verifica que el repositorio haya sido llamado para guardar la herramienta
        verify(toolRepository, times(1)).save(any(ToolEntity.class));
    }

    @Test
    public void testSave_whenToolNameIsNull_thenThrowException() {
        // Configura la herramienta con nombre nulo
        toolEntity.setName(null);

        // Llama al método save y espera una excepción
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            toolServices.save(toolEntity);
        });

        // Verifica que la excepción tenga el mensaje esperado
        assertEquals("El nombre de la herramienta es obligatorio", exception.getMessage());
    }

    @Test
    public void testUnsubscribeTool_whenToolIsPresent_thenUpdateToolStatus() throws Exception {
        // Simula que la herramienta está presente
        ToolEntity toolEntity = new ToolEntity();
        toolEntity.setId(1L);
        toolEntity.setStates(1L);  // Estado "Disponible"

        // Simula que el repositorio de herramientas devuelve la herramienta
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));

        // Simula que el estado "Discharged" existe
        StateToolsEntity dischargedState = new StateToolsEntity(3L, "Discharged");
        when(stateToolsRepository.findByName("Discharged")).thenReturn(dischargedState);

        // Llama al método unsubscribeToolAdmin
        ToolEntity result = toolServices.unsubscribeToolAdmin(1L);

        // Verifica que el resultado no sea null
        assertNotNull(result);

        // Verifica que el estado de la herramienta haya sido actualizado a "Discharged"
        assertEquals(3L, result.getStates());  // El estado debe ser "Discharged"

        // Verifica que el repositorio de herramientas haya sido llamado para guardar la herramienta actualizada
        verify(toolRepository, times(1)).save(result);
    }


    @Test
    public void testUnsubscribeTool_whenToolNotFound_thenThrowException() {
        // Simula que la herramienta no existe
        when(toolRepository.findById(1L)).thenReturn(Optional.empty());

        // Llama al método unsubscribeTool y espera que se lance una excepción
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            toolServices.unsubscribeToolAdmin(1L);
        });

        // Verifica que la excepción tenga el mensaje esperado
        assertEquals("No existe la herramienta", exception.getMessage());
    }

    @Test
    public void testBorrowedTool_whenToolIsAvailable_thenUpdateStatusToBorrowed() throws Exception {
        // Simula que la herramienta está disponible
        toolEntity.setStates(availableState.getId());  // Asume que la herramienta tiene el estado "Available"

        // Simula el comportamiento del repositorio para la herramienta y el estado "Borrowed"
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));
        when(stateToolsRepository.findByName("Borrowed")).thenReturn(new StateToolsEntity(2L, "Borrowed"));

        // Llama al método borrowedTool
        ToolEntity result = toolServices.borrowedTool(1L);

        // Verifica que el resultado no sea null
        assertNotNull(result);

        // Verifica que el estado de la herramienta haya sido actualizado a "Borrowed"
        assertEquals(2L, result.getStates());  // El estado debe ser "Borrowed"

        // Verifica que el repositorio de herramientas haya sido llamado para guardar la herramienta actualizada
        verify(toolRepository, times(1)).save(result);
    }


    @Test
    public void testBorrowedTool_whenToolNotFound_thenThrowException() {
        // Simula que la herramienta no existe
        when(toolRepository.findById(1L)).thenReturn(Optional.empty());

        // Llama al método borrowedTool y espera que se lance una excepción
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            toolServices.borrowedTool(1L);
        });

        // Verifica que la excepción tenga el mensaje esperado
        assertEquals("No existe la herramienta", exception.getMessage());
    }

    @Test
    public void testUpdateTool_whenToolExists_thenUpdateTool() {
        // Simula que la herramienta existe
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));

        // Crea un nuevo objeto ToolEntity con el mismo ID
        ToolEntity updatedTool = new ToolEntity(1L, "Updated Hammer", "Power Tools", 1200, availableState.getId());
        when(toolRepository.save(updatedTool)).thenReturn(updatedTool);

        // Llama al método updateTool
        ToolEntity result = toolServices.updateTool(updatedTool);

        // Verifica que el resultado sea el esperado
        assertNotNull(result);
        assertEquals("Updated Hammer", result.getName());
        assertEquals("Power Tools", result.getCategory());
        assertEquals(120.0, result.getReplacement_cost());
        verify(toolRepository, times(1)).save(updatedTool);
    }

    @Test
    public void testDeleteTool_whenToolExists_thenDeleteTool() throws Exception {
        // Simula que la herramienta existe
        when(toolRepository.findById(1L)).thenReturn(Optional.of(toolEntity));

        // Llama al método deletetoolbyid
        boolean result = toolServices.deletetoolbyid(1L);

        // Verifica que la herramienta haya sido eliminada correctamente
        assertTrue(result);
        verify(toolRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteTool_whenToolDoesNotExist_thenThrowException() throws Exception {
        // Simula que la herramienta no existe
        when(toolRepository.findById(1L)).thenReturn(Optional.empty());

        // Llama al método deletetoolbyid y espera que se lance una excepción
        assertThrows(Exception.class, () -> toolServices.deletetoolbyid(1L));
    }
}

