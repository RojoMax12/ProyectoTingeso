package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.ClientEntity;
import com.example.proyectotingeso.Entity.LoanToolsEntity;
import com.example.proyectotingeso.Repository.ClientRepository;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ClientServicesTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StateUsersRepository stateUsersRepository;

    @InjectMocks
    private ClientServices clientServices;

    @InjectMocks
    private LoanToolsServices loanToolsServices;

    @Test
    public void testCreateClient_whenRutExists_thenThrowException() {
        // Given
        ClientEntity newClient = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);
        when(clientRepository.findFirstByRut(newClient.getRut())).thenReturn(Optional.of(new ClientEntity()));

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clientServices.createClient(newClient);
        });
        assertEquals("Ya existe un cliente con ese RUT: 12345678-9", thrown.getMessage());
    }

    @Test
    public void testCreateClient_whenEmailExists_thenThrowException() {
        // Given
        ClientEntity newClient = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);
        when(clientRepository.findFirstByRut(newClient.getRut())).thenReturn(Optional.empty());
        when(clientRepository.findFirstByEmail(newClient.getEmail())).thenReturn(Optional.of(new ClientEntity()));

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clientServices.createClient(newClient);
        });
        assertEquals("Ya existe un cliente con ese email: juan@mail.com", thrown.getMessage());
    }

    @Test
    public void testCreateClient_whenValidClient_thenCreateSuccessfully() {
        // Given
        ClientEntity newClient = new ClientEntity(null, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", null);
        when(clientRepository.findFirstByRut(newClient.getRut())).thenReturn(Optional.empty());
        when(clientRepository.findFirstByEmail(newClient.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(newClient)).thenReturn(newClient);

        // When
        ClientEntity result = clientServices.createClient(newClient);

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
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
    public void testDeleteClient_whenClientNotFound_thenThrowException() throws Exception {
        // Given
        Long clientId = 1L;
        doThrow(new Exception("Client not found")).when(clientRepository).deleteById(clientId);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            clientServices.deleteClient(clientId);
        });
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    public void testGetClientById() {
        // Given
        Long clientId = 1L;
        ClientEntity client = new ClientEntity(clientId, "Juan Perez", "juan@mail.com", "12345678-9", "555-1234", 1L);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // When
        ClientEntity result = clientServices.getClientById(clientId);

        // Then
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals("Juan Perez", result.getName());
    }

    @Test
    public void testGetAllClientLoanLate() {
        // Given
        LoanToolsEntity loan1 = new LoanToolsEntity(
                1L,
                LocalDate.now().minusDays(10), // initiallenddate
                LocalDate.now().plusDays(5),    // finalreturndate
                1L,                             // clientid
                1L,                             // toolid
                "active",                       // status
                50.0,                           // lateFee
                100.0,                          // rentalFee
                0.0,                            // damageFee
                0.0                             // repositionFee
        );

        LoanToolsEntity loan2 = new LoanToolsEntity(
                2L,
                LocalDate.now().minusDays(20), // initiallenddate
                LocalDate.now().minusDays(2),  // finalreturndate (for late loan)
                2L,                             // clientid
                2L,                             // toolid
                "late",                         // status
                75.0,                           // lateFee
                100.0,                          // rentalFee
                0.0,                            // damageFee
                50.0                            // repositionFee
        );

// Simular el retorno del servicio
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
