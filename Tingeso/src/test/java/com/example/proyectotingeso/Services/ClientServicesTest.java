package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ClientEntity;
import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Entity.StateUsersEntity;
import com.example.proyectotingeso.Repository.ClientRepository;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServicesTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StateUsersRepository stateUsersRepository;

    @Mock
    private LoanToolsServices loanToolsServices;

    @InjectMocks
    private ClientServices clientServices;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateClient_whenStateIsNull_thenSetActiveState() {
        // Given
        ClientEntity clientEntity = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);

        // Simular que "Active" está presente en la base de datos
        StateUsersEntity activeState = new StateUsersEntity(1L, "Active");
        when(stateUsersRepository.findByName("Active")).thenReturn(activeState);

        // Simular que no existe un cliente con el mismo RUT ni email
        when(clientRepository.findFirstByRut(clientEntity.getRut())).thenReturn(Optional.empty());
        when(clientRepository.findFirstByEmail(clientEntity.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);

        // When
        ClientEntity result = clientServices.createClient(clientEntity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getState());  // El estado debe ser "Active"
        verify(clientRepository, times(1)).save(clientEntity);
    }

    @Test
    public void testCreateClient_whenStateNotFound_thenThrowException() {
        // Given
        ClientEntity clientEntity = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);

        // Simular que no existe el estado "Active" en la base de datos
        when(stateUsersRepository.findByName("Active")).thenReturn(null);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            clientServices.createClient(clientEntity);
        });
        assertEquals("No se encontró el estado 'Active' en la base de datos.", exception.getMessage());
    }


    @Test
    public void testCreateClient_whenRutExists_thenThrowException() {
        // Given
        ClientEntity clientEntity = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);

        // Simular que ya existe un cliente con el mismo RUT
        when(clientRepository.findFirstByRut(clientEntity.getRut())).thenReturn(Optional.of(clientEntity));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientServices.createClient(clientEntity);
        });
        assertEquals("Ya existe un cliente con ese RUT: 12345678-9", exception.getMessage());
    }


    @Test
    public void testCreateClient_whenEmailExists_thenThrowException() {
        // Given
        ClientEntity clientEntity = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);

        // Simular que ya existe un cliente con el mismo email
        when(clientRepository.findFirstByEmail(clientEntity.getEmail())).thenReturn(Optional.of(clientEntity));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientServices.createClient(clientEntity);
        });
        assertEquals("Ya existe un cliente con ese email: juan@mail.com", exception.getMessage());
    }

    @Test
    public void testCreateClient_whenValidClient_thenCreateSuccessfully() {
        // Given
        ClientEntity newClient = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);

        // Simular que no existen clientes con el mismo RUT y email
        when(clientRepository.findFirstByRut(newClient.getRut())).thenReturn(Optional.empty());
        when(clientRepository.findFirstByEmail(newClient.getEmail())).thenReturn(Optional.empty());

        // Simular la respuesta del estado predeterminado
        when(stateUsersRepository.findByName("Active")).thenReturn(new StateUsersEntity(1L, "Active"));

        // Simular que el repositorio guarda el cliente y lo retorna
        when(clientRepository.save(newClient)).thenReturn(newClient);

        // When
        ClientEntity result = clientServices.createClient(newClient);

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@mail.com", result.getEmail());
        assertEquals("12345678-9", result.getRut());
        assertEquals(1L, result.getState());  // Verificar el estado predeterminado
        verify(clientRepository, times(1)).save(newClient);
    }


    @Test
    public void testGetAllClients() {
        // Given
        ClientEntity client1 = new ClientEntity(1L, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", 1L);
        ClientEntity client2 = new ClientEntity(2L, "Maria Lopez", "maria@mail.com", "98765432-1", "555-5678", 1L);
        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));

        // When
        List<ClientEntity> result = clientServices.getAllClients();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan Perez", result.get(0).getName());
        assertEquals("Maria Lopez", result.get(1).getName());
    }


    @Test
    public void testGetClientById() {
        // Given
        ClientEntity client1 = new ClientEntity(1L, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", 1L);

        // Simular que el cliente con el id 1 existe en el repositorio
        when(clientRepository.findById(client1.getId())).thenReturn(Optional.of(client1));

        // When
        ClientEntity result = clientServices.getClientById(client1.getId());

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals(client1.getId(), result.getId());
    }


    @Test
    public void testGetClientById_whenClientDoesNotExist() {
        // Given
        Long nonExistingClientId = 999L;

        // Simular que no existe un cliente con el id 999
        when(clientRepository.findById(nonExistingClientId)).thenReturn(Optional.empty());

        // When
        ClientEntity result = clientServices.getClientById(nonExistingClientId);

        // Then
        assertNull(result);  // Esperamos que el resultado sea null si no existe el cliente
    }






    @Test
    public void testGetClientByRut() {
        // Given
        String rut = "12345678-9";
        ClientEntity client = new ClientEntity(1L, "Juan Perez", "juan@mail.com", rut, "555-1234", 1L);
        when(clientRepository.findByRut(rut)).thenReturn(client);

        // When
        ClientEntity result = clientServices.getClientByRut(rut);

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals(rut, result.getRut());
    }


    @Test
    public void testUpdateClient() {
        // Given
        ClientEntity updatedClient = new ClientEntity(1L, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", 1L);
        when(clientRepository.save(updatedClient)).thenReturn(updatedClient);

        // When
        ClientEntity result = clientServices.updateClient(updatedClient);

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        verify(clientRepository, times(1)).save(updatedClient);
    }

    @Test
    public void testDeleteClient_whenClientExists_thenDeleteSuccessfully() throws Exception {
        // Given
        Long clientId = 1L;
        doNothing().when(clientRepository).deleteById(clientId);

        // When
        boolean result = clientServices.deleteClient(clientId);

        // Then
        assertTrue(result);
        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    public void testDeleteClient_whenClientNotFound_thenThrowException() {
        // Given
        Long clientId = 123L;
        doThrow(new RuntimeException("Client not found")).when(clientRepository).deleteById(clientId);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientServices.deleteClient(clientId);
        });
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    public void testGetAllClientLoanLate() {
        // Given
        LoanToolsEntity loan1 = new LoanToolsEntity(1L, LocalDate.now().minusDays(10), LocalDate.now().plusDays(5), 1L, 1L, "active", 50.0, 100.0, 0.0, 0.0);
        LoanToolsEntity loan2 = new LoanToolsEntity(2L, LocalDate.now().minusDays(20), LocalDate.now().minusDays(2), 2L, 2L, "late", 75.0, 100.0, 0.0, 50.0);
        when(loanToolsServices.findallloanstoolstatusLate()).thenReturn(List.of(loan1, loan2));

        ClientEntity client1 = new ClientEntity(1L, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", 1L);
        ClientEntity client2 = new ClientEntity(2L, "Maria Lopez", "maria@mail.com", "98765432-1", "555-5678", 1L);
        when(clientRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(client1, client2));

        // When
        List<ClientEntity> result = clientServices.getAllClientLoanLate();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan Perez", result.get(0).getName());
        assertEquals("Maria Lopez", result.get(1).getName());
    }



}
