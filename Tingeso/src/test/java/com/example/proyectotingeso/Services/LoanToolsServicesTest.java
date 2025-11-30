package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.*;
import com.example.proyectotingeso.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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

    // Se usa spy en lugar de @InjectMocks para poder mockear métodos internos como calculateRentalFee
    @InjectMocks
    private LoanToolsServices loanToolsServices = spy(new LoanToolsServices());

    @BeforeEach
    public void setup() {
        // Asegurarse de que el spy de loanToolsServices use los mocks
        MockitoAnnotations.openMocks(this);
    }

    // --- Pruebas de CREACIÓN (CREATE) ---

    @Test
    public void testCreateLoanToolsEntity_Success() {
        // Arrange
        Long clientId = 1L;
        Long toolId = 10L;

        // Asumo que 100 es replacement_cost y 1L es state.
        ToolEntity availableTool = new ToolEntity(toolId, "Martillo", "Herramienta", 100, 1L);
        LoanToolsEntity newLoan = new LoanToolsEntity(null, LocalDate.now(), LocalDate.now().plusDays(7), clientId, toolId, "Active", 0.0, 0.0, 0.0, 0.0);

        // 1. Cliente y Herramienta
        ClientEntity client = new ClientEntity();
        client.setId(clientId);
        // CORRECCIÓN: Establecer el estado del cliente para evitar NullPointerException
        client.setState(1L); // Asumimos 1L es el estado "Active" o no restringido

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(toolRepository.findById(toolId)).thenReturn(Optional.of(availableTool));

        // 2. Estados de Herramienta (1L = Disponible, 2L = Prestado)
        StateToolsEntity availableState = new StateToolsEntity(1L, "Disponible");
        StateToolsEntity borrowedState = new StateToolsEntity(2L, "Prestado");
        when(stateToolsRepository.findAll()).thenReturn(Arrays.asList(availableState, borrowedState));

        // 2.5 Mockear los estados de Usuario (Añadido en el paso anterior para evitar NPE)
        StateUsersEntity restrictedUserState = new StateUsersEntity(2L, "Restricted");
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restrictedUserState);

        // 3. Préstamos activos (ninguno para pasar el límite y la validación de duplicados)
        when(loanToolsRepository.findAllByClientid(clientId)).thenReturn(Collections.emptyList());
        when(loanToolsRepository.findAllByClientidAndStatus(clientId, "Active")).thenReturn(Collections.emptyList());

        // 4. Simular guardado del préstamo (asignar ID)
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenAnswer(i -> {
            LoanToolsEntity saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // 5. Mockear el método calculateRentalFee para que devuelva un double
        doReturn(100.0).when(loanToolsServices).calculateRentalFee(anyLong());

        // Act
        LoanToolsEntity result = loanToolsServices.CreateLoanToolsEntity(newLoan);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Active", result.getStatus());

        // Verificar interacciones y estados
        verify(toolRepository, times(1)).save(availableTool);
        verify(loanToolsRepository, times(1)).save(newLoan);
        verify(loanToolsServices, times(1)).calculateRentalFee(1L);
        assertEquals(2L, availableTool.getStates()); // Herramienta debe estar en estado 'Prestado' (2L)
    }

    @Test
    public void testCreateLoanToolsEntity_ClientLimitExceeded_thenThrowException() {
        // Arrange
        Long clientId = 1L;
        LoanToolsEntity newLoan = new LoanToolsEntity(null, LocalDate.now(), LocalDate.now().plusDays(7), clientId, 10L, "Active", 0.0, 0.0, 0.0, 0.0);

        // Simular que el cliente ya tiene 6 préstamos activos (Límite 5)
        List<LoanToolsEntity> sixLoans = Arrays.asList(
                mock(LoanToolsEntity.class), mock(LoanToolsEntity.class), mock(LoanToolsEntity.class),
                mock(LoanToolsEntity.class), mock(LoanToolsEntity.class), mock(LoanToolsEntity.class)
        );
        when(loanToolsRepository.findAllByClientidAndStatus(clientId, "Active")).thenReturn(sixLoans);

        // Cliente existe y no tiene préstamos vencidos
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(new ClientEntity()));
        when(loanToolsRepository.findAllByClientid(clientId)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanToolsServices.CreateLoanToolsEntity(newLoan);
        });

        assertTrue(exception.getMessage().contains("ya tiene el máximo de 5 préstamos vigentes"));
        verify(loanToolsRepository, never()).save(any(LoanToolsEntity.class));
    }

    @Test
    public void testCreateLoanToolsEntity_ClientHasSameToolType_thenThrowException() {
        // Arrange
        Long clientId = 1L;
        Long newToolId = 10L;
        Long existingToolId = 11L;

        LoanToolsEntity newLoan = new LoanToolsEntity(null, LocalDate.now(), LocalDate.now().plusDays(7), clientId, newToolId, "Active", 0.0, 0.0, 0.0, 0.0);
        LoanToolsEntity existingLoan = new LoanToolsEntity(100L, LocalDate.now(), LocalDate.now().plusDays(5), clientId, existingToolId, "Active", 0.0, 0.0, 0.0, 0.0);

        // Cliente y Herramientas (mismo nombre y categoría)
        ClientEntity client = new ClientEntity();
        client.setId(clientId);
        ToolEntity newTool = new ToolEntity(newToolId, "Martillo", "Herramientas de mano", 100, 1L);
        ToolEntity existingTool = new ToolEntity(existingToolId, "Martillo", "Herramientas de mano", 100, 2L);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(loanToolsRepository.findAllByClientid(clientId)).thenReturn(Collections.emptyList());
        when(loanToolsRepository.findAllByClientidAndStatus(clientId, "Active")).thenReturn(Arrays.asList(existingLoan));

        when(toolRepository.findById(newToolId)).thenReturn(Optional.of(newTool));
        when(toolRepository.findById(existingToolId)).thenReturn(Optional.of(existingTool));

        // Estados de Herramienta
        StateToolsEntity availableState = new StateToolsEntity(1L, "Disponible");
        when(stateToolsRepository.findAll()).thenReturn(Arrays.asList(availableState));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanToolsServices.CreateLoanToolsEntity(newLoan);
        });

        assertTrue(exception.getMessage().contains("El cliente ya tiene una herramienta prestada con el nombre 'Martillo' en la categoría 'Herramientas de mano'"));
        verify(loanToolsRepository, never()).save(any(LoanToolsEntity.class));
    }

    // El test testCreateLoanToolsEntity_whenClientHasOverdueLoans_thenThrowException ya está cubierto.
    // El test testCreateLoanToolsEntity_whenToolNotAvailable_thenThrowException ya está cubierto.

    // --- Pruebas de MULTA (Fine) ---

    @Test
    public void testCalculateFine_LoanIsOnTime_thenZeroFineAndStatusActive() {
        // Arrange
        Long loanId = 2L;
        LocalDate finalReturnDate = LocalDate.now().plusDays(5);
        LoanToolsEntity loanToolsEntity = new LoanToolsEntity(loanId, LocalDate.now().minusDays(2), finalReturnDate, 1L, 1L, "Active", 10.0, 0.0, 0.0, 0.0);

        when(loanToolsRepository.findById(loanId)).thenReturn(Optional.of(loanToolsEntity));
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenReturn(loanToolsEntity);

        // Act
        double result = loanToolsServices.calculateFine(loanId);

        // Assert
        assertEquals(0.0, result);
        assertEquals(0.0, loanToolsEntity.getLateFee());
        assertEquals("Active", loanToolsEntity.getStatus());

        // Verifica que se haya guardado el préstamo con los valores actualizados
        verify(loanToolsRepository, times(1)).save(loanToolsEntity);
        // Verifica que NO se haya llamado a obtener tarifas ni a bloquear cliente
        verify(amountsandratesRepository, never()).findAll();
        verify(clientRepository, never()).findById(anyLong());
    }

    // El test testCalculateFine_whenLoanIsLate_thenCalculateFine ya está cubierto.

    // --- Pruebas de TARIFA DE ALQUILER (Rental Fee) ---

    @Test
    public void testCalculateRentalFee_Success() {
        // Arrange
        Long loanId = 3L;
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(5); // 7 días de préstamo (endDate - startDate)
        LoanToolsEntity loan = new LoanToolsEntity(loanId, startDate, endDate, 1L, 1L, "Active", 0.0, 0.0, 0.0, 0.0);

        AmountsandratesEntity rates = new AmountsandratesEntity();
        rates.setDailyrentalrate(10.0); // $10.0 por día

        // Simular repositorio
        when(loanToolsRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(amountsandratesRepository.findAll()).thenReturn(List.of(rates));
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenReturn(loan);

        // Act
        double result = loanToolsServices.calculateRentalFee(loanId);

        // Assert
        // Días de préstamo: 7. Tarifa: 7 * 10.0 = 70.0
        assertEquals(70.0, result);
        assertEquals(70.0, loan.getRentalFee());

        verify(loanToolsRepository, times(1)).save(loan);
    }

    // --- Pruebas de DEVOLUCIÓN (Return) ---

    @Test
    public void testReturnLoanTools_Success() {
        // Arrange
        Long userId = 1L;
        Long toolId = 10L;

        ToolEntity tool = new ToolEntity(toolId, "Martillo", "Herramienta", 300, 2L); // Estado 2L: Prestado
        LoanToolsEntity loan = new LoanToolsEntity(1L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(2), userId, toolId, "Active", 0.0, 0.0, 0.0, 0.0);

        // Estados de herramienta (1L = Disponible, 2L = Prestado)
        StateToolsEntity availableState = new StateToolsEntity(1L, "Disponible");
        StateToolsEntity borrowedState = new StateToolsEntity(2L, "Prestado");
        when(stateToolsRepository.findAll()).thenReturn(Arrays.asList(availableState, borrowedState));

        // Comportamiento del repositorio
        when(toolRepository.findById(toolId)).thenReturn(Optional.of(tool));
        when(loanToolsRepository.findByClientidAndToolid(userId, toolId)).thenReturn(Optional.of(loan));
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenReturn(loan);

        // Mockear checkAndUpdateClientStatus (simular que no lanza excepción)
        doReturn(true).when(loanToolsServices).checkAndUpdateClientStatus(userId);

        // Act
        LoanToolsEntity result = loanToolsServices.returnLoanTools(userId, toolId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, tool.getStates()); // Estado de la herramienta debe ser "Disponible" (1L)
        assertEquals("No active", result.getStatus()); // Estado del préstamo debe ser "No active"

        // Verificar interacciones
        verify(toolRepository, times(1)).save(tool);
        verify(loanToolsServices, times(1)).checkAndUpdateClientStatus(userId);
        verify(loanToolsRepository, times(1)).save(loan);
    }

    // --- Pruebas de ESTADO DE CLIENTE (checkAndUpdateClientStatus) ---

    @Test
    public void testCheckAndUpdateClientStatus_RestrictedAndClear_BecomesActive() {
        // Arrange
        Long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setId(clientId);
        client.setState(2L); // 2L = Restricted

        // Estados de usuario
        StateUsersEntity restricted = new StateUsersEntity(2L, "Restricted");
        StateUsersEntity active = new StateUsersEntity(1L, "Active");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restricted);
        when(stateUsersRepository.findByName("Active")).thenReturn(active);

        // Simular NO préstamos vencidos
        doReturn(false).when(loanToolsServices).hasOverdueLoans(clientId);

        // Simular NO multas impagas
        LoanToolsEntity cleanLoan = new LoanToolsEntity();
        cleanLoan.setLateFee(0.0);
        cleanLoan.setDamageFee(0.0);
        cleanLoan.setRepositionFee(0.0);
        when(loanToolsRepository.findAllByClientid(clientId)).thenReturn(Arrays.asList(cleanLoan));

        // Act
        boolean result = loanToolsServices.checkAndUpdateClientStatus(clientId);

        // Assert
        assertTrue(result); // Se desbloqueó
        assertEquals(1L, client.getState()); // El estado debe ser Active
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    public void testCheckAndUpdateClientStatus_RestrictedButUnpaidFees_RemainsRestricted() {
        // Arrange
        Long clientId = 1L;
        ClientEntity client = new ClientEntity();
        client.setId(clientId);
        client.setState(2L); // 2L = Restricted

        // Estados de usuario
        StateUsersEntity restricted = new StateUsersEntity(2L, "Restricted");
        StateUsersEntity active = new StateUsersEntity(1L, "Active");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(stateUsersRepository.findByName("Restricted")).thenReturn(restricted);
        when(stateUsersRepository.findByName("Active")).thenReturn(active);

        // Simular NO préstamos vencidos
        doReturn(false).when(loanToolsServices).hasOverdueLoans(clientId);

        // Simular multas impagas
        LoanToolsEntity fineLoan = new LoanToolsEntity();
        fineLoan.setLateFee(15.0); // Multa impaga
        fineLoan.setDamageFee(0.0);
        fineLoan.setRepositionFee(0.0);
        when(loanToolsRepository.findAllByClientid(clientId)).thenReturn(Arrays.asList(fineLoan));

        // Act
        boolean result = loanToolsServices.checkAndUpdateClientStatus(clientId);

        // Assert
        assertFalse(result); // No se desbloqueó
        assertEquals(2L, client.getState()); // El estado debe permanecer Restricted
        verify(clientRepository, never()).save(client); // No se guarda el cliente
    }

    // --- Pruebas de REGISTRO DE CARGOS POR DAÑO/REPOSICIÓN ---

    @Test
    public void testRegisterDamageFeeandReposition_State3_RepositionFee_Calculated() {
        // Arrange
        Long loanId = 1L;
        Long toolId = 10L;
        double reparationCharge = 50.0;

        LoanToolsEntity loan = new LoanToolsEntity(loanId, LocalDate.now(), LocalDate.now().plusDays(7), 1L, toolId, "Active", 0.0, 0.0, 0.0, 0.0);
        ToolEntity tool = new ToolEntity(toolId, "Martillo", "Herramienta", 3000, 3L); // Estado 3: Reparación

        AmountsandratesEntity rates = new AmountsandratesEntity();
        rates.setReparationcharge(reparationCharge);

        // Comportamiento del repositorio
        when(loanToolsRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(toolRepository.findById(toolId)).thenReturn(Optional.of(tool));
        when(amountsandratesRepository.findById(3L)).thenReturn(Optional.of(rates)); // ID 3L en el servicio
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenReturn(loan);

        // Act
        loanToolsServices.registerDamageFeeandReposition(loanId);

        // Assert
        assertEquals(reparationCharge, loan.getRepositionFee());
        assertEquals(0.0, loan.getDamageFee()); // El otro fee debe ser 0.0
        verify(loanToolsRepository, times(1)).save(loan);
    }

    @Test
    public void testRegisterDamageFeeandReposition_State4_DamageFee_Calculated() {
        // Arrange
        Long loanId = 2L;
        Long toolId = 11L;
        int replacementCost = 350;

        LoanToolsEntity loan = new LoanToolsEntity(loanId, LocalDate.now(), LocalDate.now().plusDays(7), 1L, toolId, "Active", 0.0, 0.0, 0.0, 0.0);
        ToolEntity tool = new ToolEntity(toolId, "Sierra", "Herramienta", replacementCost, 4L); // Estado 4: Dañada

        AmountsandratesEntity rates = new AmountsandratesEntity(); // Necesario para evitar NullPointerException si se llama a rates.getReparationcharge()
        when(amountsandratesRepository.findById(3L)).thenReturn(Optional.of(rates));

        // Comportamiento del repositorio
        when(loanToolsRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(toolRepository.findById(toolId)).thenReturn(Optional.of(tool));
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenReturn(loan);

        // Act
        loanToolsServices.registerDamageFeeandReposition(loanId);

        // Assert
        assertEquals(0.0, loan.getRepositionFee()); // El otro fee debe ser 0.0
        assertEquals((double) replacementCost, loan.getDamageFee());
        verify(loanToolsRepository, times(1)).save(loan);
    }


    // --- Pruebas de PAGO DE CARGOS ---

    @Test
    public void testRegisterAllFeesPayment_Success() {
        // Arrange
        Long loanId = 1L;
        LoanToolsEntity loan = new LoanToolsEntity(loanId, LocalDate.now(), LocalDate.now().plusDays(7), 1L, 10L, "Late", 20.0, 10.0, 5.0, 50.0);

        when(loanToolsRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanToolsRepository.save(any(LoanToolsEntity.class))).thenReturn(loan);

        // Mockear checkAndUpdateClientStatus (simular el desbloqueo si es necesario)
        doReturn(true).when(loanToolsServices).checkAndUpdateClientStatus(loan.getClientid());

        // Act
        Boolean result = loanToolsServices.registerAllFeesPayment(loanId);

        // Assert
        assertTrue(result);
        assertEquals(0.0, loan.getLateFee());
        assertEquals(0.0, loan.getDamageFee());
        assertEquals(0.0, loan.getRepositionFee());
        assertEquals(0.0, loan.getRentalFee());
        assertEquals("No active", loan.getStatus());

        verify(loanToolsRepository, times(1)).save(loan);
        verify(loanToolsServices, times(1)).checkAndUpdateClientStatus(loan.getClientid());
    }

    // --- Pruebas CRUD Simples y Queries ---

    @Test
    public void testUpdateLoanToolsEntity_Success() {
        // Arrange
        Long loanId = 1L;
        LoanToolsEntity original = new LoanToolsEntity(loanId, LocalDate.now(), LocalDate.now().plusDays(7), 1L, 10L, "Active", 0.0, 0.0, 0.0, 0.0);
        LoanToolsEntity updated = new LoanToolsEntity(loanId, LocalDate.now(), LocalDate.now().plusDays(14), 1L, 10L, "Active", 0.0, 0.0, 0.0, 0.0);

        when(loanToolsRepository.save(ArgumentMatchers.any(LoanToolsEntity.class))).thenReturn(updated);

        // Act
        LoanToolsEntity result = loanToolsServices.UpdateLoanToolsEntity(updated);

        // Assert
        assertNotNull(result);
        assertEquals(updated.getFinalreturndate(), result.getFinalreturndate());
        verify(loanToolsRepository, times(1)).save(updated);
    }

    @Test
    public void testDeleteLoanToolsEntity_Success() throws Exception {
        // Arrange
        Long loanId = 1L;
        doNothing().when(loanToolsRepository).deleteById(loanId);

        // Act
        boolean result = loanToolsServices.DeleteLoanToolsEntity(loanId);

        // Assert
        assertTrue(result);
        verify(loanToolsRepository, times(1)).deleteById(loanId);
    }

    @Test
    public void testGetAlluserLoanTools_Success() {
        // Arrange
        Long userId = 1L;
        List<LoanToolsEntity> mockLoans = Arrays.asList(new LoanToolsEntity(), new LoanToolsEntity());
        when(loanToolsRepository.findAllByClientid(userId)).thenReturn(mockLoans);

        // Act
        List<LoanToolsEntity> result = loanToolsServices.getAlluserLoanTools(userId);

        // Assert
        assertEquals(2, result.size());
        verify(loanToolsRepository, times(1)).findAllByClientid(userId);
    }

    @Test
    public void testFindAllLoansToolStatusAndRentalFee_Success() {
        // Arrange
        List<String> statuses = List.of("Late", "Active");
        List<LoanToolsEntity> mockLoans = Arrays.asList(new LoanToolsEntity(), new LoanToolsEntity());
        when(loanToolsRepository.findAllBystatusInAndRentalFeeGreaterThan(statuses, 0.0)).thenReturn(mockLoans);

        // Act
        List<LoanToolsEntity> result = loanToolsServices.findallloanstoolstatusandRentalFee();

        // Assert
        assertEquals(2, result.size());
        verify(loanToolsRepository, times(1)).findAllBystatusInAndRentalFeeGreaterThan(statuses, 0.0);
    }

    @Test
    public void testFindAllLoansToolStatusLate_Success() {
        // Arrange
        List<LoanToolsEntity> mockLoans = Arrays.asList(new LoanToolsEntity());
        when(loanToolsRepository.findAllBystatus("Late")).thenReturn(mockLoans);

        // Act
        List<LoanToolsEntity> result = loanToolsServices.findallloanstoolstatusLate();

        // Assert
        assertEquals(1, result.size());
        verify(loanToolsRepository, times(1)).findAllBystatus("Late");
    }
}