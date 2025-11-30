package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.StateUsersEntity;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StateUsersServicesTest {

    @Mock
    private StateUsersRepository stateUsersRepository;

    @InjectMocks
    private StateUsersServices stateUsersServices;

    private StateUsersEntity activeState;
    private StateUsersEntity restrictedState;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @BeforeEach
    void setUp() {
        // Inicializa los objetos StateUsersEntity
        activeState = new StateUsersEntity(1L, "Active");
        restrictedState = new StateUsersEntity(2L, "Restricted");
    }

    @Test
    public void testCreateStateUsers_whenStatesNotExist_thenCreateStates() {
        // Simula que no existen los estados "Active" y "Restricted"
        when(stateUsersRepository.findByName("Active")).thenReturn(null);
        when(stateUsersRepository.findByName("Restricted")).thenReturn(null);

        // Llama al método createStateUsers
        String result = stateUsersServices.CreateStateUsers();

        // Verifica que los estados sean creados
        assertEquals("Estados creados con exito", result);

        // Verifica que los métodos de guardado hayan sido llamados para los dos estados
        verify(stateUsersRepository, times(2)).save(any(StateUsersEntity.class));
    }

    @Test
    public void testCreateStateUsers_whenStatesExist_thenDoNotCreate() {
        // Simula que los estados "Active" y "Restricted" ya existen
        when(stateUsersRepository.findByName("Active")).thenReturn(activeState);
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restrictedState);

        // Llama al método createStateUsers
        String result = stateUsersServices.CreateStateUsers();

        // Verifica que el mensaje indique que los estados ya están inicializados
        assertEquals("Estados creados", result);

        // Verifica que no se haya intentado guardar un estado nuevo
        verify(stateUsersRepository, times(0)).save(any(StateUsersEntity.class));
    }

    @Test
    public void testGetStateUsersById_whenStateExists_thenReturnState() {
        // Simula que el estado "Active" existe en el repositorio
        when(stateUsersRepository.findById(1L)).thenReturn(Optional.of(activeState));

        // Llama al método getStateUsersById
        StateUsersEntity result = stateUsersServices.getStateUsersById(1L);

        // Verifica que el estado sea el esperado
        assertNotNull(result);
        assertEquals("Active", result.getName());
    }

    @Test
    public void testGetStateUsersById_whenStateDoesNotExist_thenReturnNull() {
        // Simula que el estado no se encuentra
        when(stateUsersRepository.findById(99L)).thenReturn(Optional.empty());

        // Llama al método getStateUsersById
        StateUsersEntity result = stateUsersServices.getStateUsersById(99L);

        // Verifica que el resultado sea null
        assertNull(result);
    }

    @Test
    public void testGetAllStateUsers_whenStatesExist_thenReturnStateList() {
        // Simula que existen los estados "Active" y "Restricted"
        when(stateUsersRepository.findAll()).thenReturn(Arrays.asList(activeState, restrictedState));

        // Llama al método getAllStateUsers
        List<StateUsersEntity> result = stateUsersServices.getAllStateUsers();

        // Verifica que se haya recuperado la lista de estados
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(activeState));
        assertTrue(result.contains(restrictedState));
    }

    @Test
    public void testUpdateStateUsers_whenStateExists_thenUpdateState() {
        // Simula que el estado "Active" existe
        when(stateUsersRepository.save(activeState)).thenReturn(activeState);

        // Llama al método updateStateUsers
        StateUsersEntity updatedState = new StateUsersEntity(1L, "Updated Active");
        StateUsersEntity result = stateUsersServices.updateStateUsers(updatedState);

        // Verifica que el estado haya sido actualizado correctamente
        assertNotNull(result);
        assertEquals("Updated Active", result.getName());

        // Verifica que el repositorio haya sido llamado para guardar el estado actualizado
        verify(stateUsersRepository, times(1)).save(updatedState);
    }

    @Test
    public void testDeleteStateUsersById_whenStateExists_thenDeleteState() throws Exception {
        // Simula que el estado "Active" existe
        when(stateUsersRepository.findById(1L)).thenReturn(Optional.of(activeState));

        // Llama al método deleteStateUsersById
        boolean result = stateUsersServices.deleteStateUsersById(1L);

        // Verifica que el estado haya sido eliminado
        assertTrue(result);
        verify(stateUsersRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteStateUsersById_whenStateDoesNotExist_thenThrowException() {
        // Simula que el estado no existe
        when(stateUsersRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Verifica que se lance una excepción al intentar eliminar un estado que no existe
        assertThrows(Exception.class, () -> stateUsersServices.deleteStateUsersById(99L));
    }
}
