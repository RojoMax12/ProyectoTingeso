package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.*;
import com.example.proyectotingeso.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoanToolsServicesTest {

    @Mock
    private LoanToolsRepository loanToolsRepository;

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AmountsandratesRepository amountsandratesRepository;

    @Mock
    private StateToolsRepository stateToolsRepository;

    @Mock
    private StateUsersRepository stateUsersRepository;

    @InjectMocks
    private LoanToolsServices loanToolsServices;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateLoanToolsEntity_whenValidLoan_thenCreateSuccessfully() {
        // Arrange: Simula los datos para un préstamo de herramienta válido
        LoanToolsEntity loanToolsEntity = new LoanToolsEntity(1L, LocalDate.now(), LocalDate.now().plusDays(7), 1L, 1L, "Active", 0.0, 0.0, 0.0, 0.0);

        // Simula el comportamiento del repositorio
        when(clientRepository.findById(loanToolsEntity.getClientid())).thenReturn(Optional.of(new ClientEntity()));
        when(toolRepository.findById(loanToolsEntity.getToolid())).thenReturn(Optional.of(new ToolEntity()));

        // Simula que el estado "Restricted" existe y tiene un ID específico
        StateUsersEntity restrictedState = new StateUsersEntity();
        restrictedState.setId(2L);  // Asigna un ID al estado "Restricted"
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restrictedState);

        // Simula un cliente con estado "Active"
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setState(1L);  // Asigna un estado válido, como "Active"
        when(clientRepository.findById(loanToolsEntity.getClientid())).thenReturn(Optional.of(client));

        // Simula que el repositorio de préstamos guarda la entidad
        when(loanToolsRepository.save(loanToolsEntity)).thenReturn(loanToolsEntity);

        // Act: Llama al método CreateLoanToolsEntity del servicio
        LoanToolsEntity result = loanToolsServices.CreateLoanToolsEntity(loanToolsEntity);

        // Assert: Verifica que la entidad se haya creado correctamente
        assertNotNull(result);
        assertEquals(loanToolsEntity.getToolid(), result.getToolid());
        assertEquals("Active", result.getStatus());

        // Verifica que el repositorio haya sido llamado para guardar la entidad
        verify(loanToolsRepository, times(1)).save(any(LoanToolsEntity.class));
    }



    @Test
    public void testCreateLoanToolsEntity_whenClientHasOverdueLoans_thenThrowException() {
        // Given: Simula un cliente con préstamos vencidos
        LoanToolsEntity loanToolsEntity = new LoanToolsEntity(1L, LocalDate.now(), LocalDate.now().plusDays(7), 1L, 1L, "Active", 0.0, 0.0, 0.0, 0.0);

        // Simula el comportamiento del repositorio de cliente
        when(clientRepository.findById(loanToolsEntity.getClientid())).thenReturn(Optional.of(new ClientEntity()));

        // Simula el comportamiento para que el cliente tenga préstamos vencidos
        // Asegúrate de que 'findAllByClientid' devuelve una lista
        when(loanToolsRepository.findAllByClientid(loanToolsEntity.getClientid())).thenReturn(Arrays.asList(loanToolsEntity));

        // Act & Assert: Verifica que se lance una excepción si el cliente tiene préstamos vencidos
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanToolsServices.CreateLoanToolsEntity(loanToolsEntity);
        });
        assertEquals("Cliente bloqueado por préstamos vencidos pendientes", exception.getMessage());
    }


    @Test
    public void testCreateLoanToolsEntity_whenToolNotAvailable_thenThrowException() {
        // Arrange: Simula los datos para un préstamo de herramienta
        LoanToolsEntity loanToolsEntity = new LoanToolsEntity(1L, LocalDate.now(), LocalDate.now().plusDays(7), 1L, 1L, "Active", 0.0, 0.0, 0.0, 0.0);

        // Simula que el cliente existe
        when(clientRepository.findById(loanToolsEntity.getClientid())).thenReturn(Optional.of(new ClientEntity()));

        // Simula que la herramienta con el ID no está disponible
        ToolEntity tool = new ToolEntity();
        tool.setId(loanToolsEntity.getToolid());
        tool.setStates(2L); // Estado no disponible

        // Simula el repositorio para la herramienta
        when(toolRepository.findById(loanToolsEntity.getToolid())).thenReturn(Optional.of(tool));

        // Act & Assert: Verifica que se lance una excepción si la herramienta no está disponible
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanToolsServices.CreateLoanToolsEntity(loanToolsEntity);
        });
        assertEquals("La herramienta no está disponible", exception.getMessage());
    }

    @Test
    public void testCalculateFine_whenLoanIsLate_thenCalculateFine() {
        // Arrange: Simula un préstamo vencido
        LoanToolsEntity loanToolsEntity = new LoanToolsEntity(1L, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5), 1L, 1L, "Late", 0.0, 0.0, 0.0, 0.0);
        AmountsandratesEntity rates = new AmountsandratesEntity();
        rates.setDailylatefeefine(5.0);  // Tarifas para las multas por día

        // Simula el comportamiento del repositorio
        when(loanToolsRepository.findById(loanToolsEntity.getId())).thenReturn(Optional.of(loanToolsEntity));
        when(amountsandratesRepository.findAll()).thenReturn(List.of(rates));

        // Simula la existencia del cliente
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setState(2L); // Estado del cliente, que debe ser "Restricted"

        // Simula la existencia del estado "Restricted"
        StateUsersEntity restrictedState = new StateUsersEntity();
        restrictedState.setId(2L);
        restrictedState.setName("Restricted");

        when(clientRepository.findById(loanToolsEntity.getClientid())).thenReturn(Optional.of(client));
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restrictedState);  // Simula el repositorio para 'Restricted'

        // Act: Llama al método calculateFine del servicio
        double result = loanToolsServices.calculateFine(loanToolsEntity.getId());

        // Assert: Verifica que la multa se haya calculado correctamente
        assertEquals(50.0, result);
        assertEquals("Late", loanToolsEntity.getStatus()); // Verifica que el estado se haya actualizado
    }



    @Test
    public void testCountActiveLoans_whenClientHasActiveLoans_thenReturnCount() {
        // Arrange: Simula préstamos activos para un cliente
        Long clientId = 1L;
        LoanToolsEntity loan1 = new LoanToolsEntity(1L, LocalDate.now(), LocalDate.now().plusDays(7), clientId, 1L, "Active", 0.0, 0.0, 0.0, 0.0);
        LoanToolsEntity loan2 = new LoanToolsEntity(2L, LocalDate.now(), LocalDate.now().plusDays(7), clientId, 2L, "Active", 0.0, 0.0, 0.0, 0.0);

        when(loanToolsRepository.findAllByClientidAndStatus(clientId, "Active")).thenReturn(Arrays.asList(loan1, loan2));

        // Act: Llama al método countActiveLoans del servicio
        int result = loanToolsServices.countActiveLoans(clientId);

        // Assert: Verifica que el número de préstamos activos sea el esperado
        assertEquals(2, result);
    }

    @Test
    public void testReturnLoanTools_whenLoanIsReturned_thenUpdateToolStatus() {
        // Arrange: Simula un cliente con estado restringido
        ClientEntity client = new ClientEntity();
        client.setId(1L);
        client.setState(2L);  // Estado "Restricted"
        System.out.println("[TRACE] Cliente simulado: " + client);

        // Simula el estado "Restricted"
        StateUsersEntity restrictedState = new StateUsersEntity();
        restrictedState.setId(2L);  // ID del estado "Restricted"
        restrictedState.setName("Restricted");  // Nombre "Restricted"
        System.out.println("[TRACE] Estado restringido: " + restrictedState);

        // Simula un estado de herramienta "Disponible"
        StateToolsEntity state = new StateToolsEntity();
        state.setName("Disponible");  // Estado de la herramienta cuando es devuelta
        state.setId(1L);  // ID del estado "Disponible"
        System.out.println("[TRACE] Estado de herramienta disponible: " + state);

        // Simula que la herramienta tiene estado "Disponible"
        ToolEntity tool = new ToolEntity();
        tool.setStates(1L);  // Estado "Disponible"
        tool.setId(1L);
        System.out.println("[TRACE] Herramienta simulada: " + tool);

        LoanToolsEntity loan1 = new LoanToolsEntity(1L, LocalDate.now(), LocalDate.now().plusDays(7), client.getId(), tool.getId(), "Active", 0.0, 0.0, 0.0, 0.0);
        System.out.println("[TRACE] Préstamo simulado: " + loan1);

        // Simula los repositorios
        when(toolRepository.findById(tool.getId())).thenReturn(Optional.of(tool));
        when(loanToolsRepository.findByClientidAndToolid(client.getId(), tool.getId())).thenReturn(Optional.of(loan1));
        when(stateToolsRepository.findAll()).thenReturn(List.of(state));  // Simula que hay un estado configurado para herramientas

        // Simula el repositorio de cliente
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));  // Simula que el cliente está presente en el repositorio

        // Simula el estado del cliente como "Restricted" (que es el estado 2L)
        // Aquí aseguramos que la simulación de `stateUsersRepository.findByName("Restricted")` esté configurada correctamente
        when(stateUsersRepository.findById(client.getState())).thenReturn(Optional.of(restrictedState)); // Devuelve el estado "Restricted"
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restrictedState);  // Simula la búsqueda por nombre

        // Act: Llama al método returnLoanTools del servicio
        LoanToolsEntity result = loanToolsServices.returnLoanTools(client.getId(), tool.getId());

        // Assert: Verifica que el estado del préstamo haya cambiado a "No active" después de devolver la herramienta
        assertNotNull(result);
        assertEquals("No active", result.getStatus());  // Verifica que el estado del préstamo haya cambiado a "No active"
        assertEquals(1L, tool.getStates());  // Verifica que el estado de la herramienta haya sido actualizado a "Disponible"

        // Verifica que el estado de la herramienta haya sido actualizado y guardado correctamente
        verify(toolRepository, times(1)).save(tool);  // Verifica que el repositorio de herramientas haya sido llamado para guardar la herramienta
        verify(loanToolsRepository, times(1)).save(result);  // Verifica que el repositorio de préstamos haya sido llamado para guardar el préstamo
    }

}






