package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.AmountsandratesEntity;
import com.example.proyectotingeso.Repository.AmountsandratesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AmountsandratesServicesTest {

    @Mock
    private AmountsandratesRepository amountsandratesRepository;

    @InjectMocks
    private AmountsandratesServices amountsandratesServices;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAmountsAndRates_whenNoExistingConfig_thenCreateNew() {
        // Arrange: Simula que no hay ninguna configuración existente
        when(amountsandratesRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        // Act: Ejecuta el método del servicio
        AmountsandratesEntity result = amountsandratesServices.createAmountsAndRates();

        // Assert: Verifica que se haya creado una nueva entidad con valores por defecto
        assertNotNull(result);
        assertEquals(0.0, result.getDailyrentalrate());
        assertEquals(0.0, result.getDailylatefeefine());
        assertEquals(0.0, result.getReparationcharge());

        // Verifica que el repositorio haya sido llamado para guardar la entidad
        verify(amountsandratesRepository, times(1)).save(any(AmountsandratesEntity.class));
    }

    @Test
    public void testGetAmountsAndRates_whenExists_thenReturnExisting() {
        // Arrange: Simula que hay una configuración existente
        AmountsandratesEntity existingEntity = new AmountsandratesEntity();
        existingEntity.setDailyrentalrate(10.0);
        existingEntity.setDailylatefeefine(2.0);
        existingEntity.setReparationcharge(5.0);

        when(amountsandratesRepository.findById(1L)).thenReturn(Optional.of(existingEntity));

        // Act: Ejecuta el método del servicio
        Optional<AmountsandratesEntity> result = amountsandratesServices.getAmountsAndRates();

        // Assert: Verifica que se haya retornado la configuración existente
        assertTrue(result.isPresent());
        assertEquals(10.0, result.get().getDailyrentalrate());
        assertEquals(2.0, result.get().getDailylatefeefine());
        assertEquals(5.0, result.get().getReparationcharge());
    }

    @Test
    public void testUpdateAmountsAndRates_whenExists_thenUpdate() {
        // Arrange: Simula que existe una configuración
        AmountsandratesEntity existingEntity = new AmountsandratesEntity();
        existingEntity.setDailyrentalrate(10.0);
        existingEntity.setDailylatefeefine(2.0);
        existingEntity.setReparationcharge(5.0);

        when(amountsandratesRepository.findAll()).thenReturn(java.util.Collections.singletonList(existingEntity));

        AmountsandratesEntity updatedEntity = new AmountsandratesEntity();
        updatedEntity.setDailyrentalrate(20.0);
        updatedEntity.setDailylatefeefine(4.0);
        updatedEntity.setReparationcharge(10.0);

        // Act: Ejecuta el método de actualización
        AmountsandratesEntity result = amountsandratesServices.updateAmountAndRates(updatedEntity);

        // Assert: Verifica que la configuración haya sido actualizada
        assertNotNull(result);
        assertEquals(20.0, result.getDailyrentalrate());
        assertEquals(4.0, result.getDailylatefeefine());
        assertEquals(10.0, result.getReparationcharge());

        // Verifica que el repositorio haya sido llamado para guardar la entidad actualizada
        verify(amountsandratesRepository, times(1)).save(any(AmountsandratesEntity.class));
    }
}
