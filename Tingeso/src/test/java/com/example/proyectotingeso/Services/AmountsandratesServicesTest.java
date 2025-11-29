package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.AmountsandratesEntity;
import com.example.proyectotingeso.Repository.AmountsandratesRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AmountsandratesServicesTest {

    @Mock
    private AmountsandratesRepository amountsandratesRepository;

    @InjectMocks
    private AmountsandratesServices amountsandratesServices;

    @Test
    public void testCreateAmountsAndRates_whenNoExistingConfiguration_thenCreateNew() {
        // Given no existing configuration
        when(amountsandratesRepository.findAll()).thenReturn(List.of());

        // When calling the method
        AmountsandratesEntity result = amountsandratesServices.createAmountsAndRates();

        // Then verify that the entity is created with default values
        assertNotNull(result);
        assertEquals(0.0, result.getDailyrentalrate());
        assertEquals(0.0, result.getDailylatefeefine());
        assertEquals(0.0, result.getReparationcharge());

        // Verify that the save method was called
        verify(amountsandratesRepository, times(1)).save(any(AmountsandratesEntity.class));
    }

    @Test
    public void testCreateAmountsAndRates_whenExistingConfiguration_thenReturnExisting() {
        // Given an existing configuration
        AmountsandratesEntity existingEntity = new AmountsandratesEntity();
        existingEntity.setDailyrentalrate(5.0);
        existingEntity.setDailylatefeefine(1.0);
        existingEntity.setReparationcharge(10.0);
        when(amountsandratesRepository.findAll()).thenReturn(List.of(existingEntity));

        // When calling the method
        AmountsandratesEntity result = amountsandratesServices.createAmountsAndRates();

        // Then verify that the existing entity is returned
        assertNotNull(result);
        assertEquals(5.0, result.getDailyrentalrate());
        assertEquals(1.0, result.getDailylatefeefine());
        assertEquals(10.0, result.getReparationcharge());

        // Verify that save method was not called
        verify(amountsandratesRepository, times(0)).save(any(AmountsandratesEntity.class));
    }

    @Test
    public void testGetAmountsAndRates_whenConfigurationExists() {
        // Given an existing configuration
        AmountsandratesEntity existingEntity = new AmountsandratesEntity();
        existingEntity.setDailyrentalrate(5.0);
        existingEntity.setDailylatefeefine(1.0);
        existingEntity.setReparationcharge(10.0);
        when(amountsandratesRepository.findById(1L)).thenReturn(Optional.of(existingEntity));

        // When calling the method
        Optional<AmountsandratesEntity> result = amountsandratesServices.getAmountsAndRates();

        // Then verify that the existing entity is returned
        assertTrue(result.isPresent());
        assertEquals(5.0, result.get().getDailyrentalrate());
        assertEquals(1.0, result.get().getDailylatefeefine());
        assertEquals(10.0, result.get().getReparationcharge());
    }


    @Test
    public void testUpdateAmountAndRates_whenNoExistingConfiguration_thenCreateNew () {
        // Given no existing configuration
        AmountsandratesEntity updatedEntity = new AmountsandratesEntity();
        updatedEntity.setDailyrentalrate(6.0);
        updatedEntity.setDailylatefeefine(1.5);
        updatedEntity.setReparationcharge(12.0);

        when(amountsandratesRepository.findAll()).thenReturn(List.of());

        // When calling the method
        AmountsandratesEntity result = amountsandratesServices.updateAmountAndRates(updatedEntity);

        // Then verify that the entity is created
        assertEquals(6.0, result.getDailyrentalrate());
        assertEquals(1.5, result.getDailylatefeefine());
        assertEquals(12.0, result.getReparationcharge());

        // Verify that save was called once
        verify(amountsandratesRepository, times(1)).save(any(AmountsandratesEntity.class));
    }

    @Test
    public void testUpdateAmountAndRates_whenExistingConfiguration_thenUpdate () {
        // Given an existing configuration
        AmountsandratesEntity existingEntity = new AmountsandratesEntity();
        existingEntity.setDailyrentalrate(5.0);
        existingEntity.setDailylatefeefine(1.0);
        existingEntity.setReparationcharge(10.0);

        AmountsandratesEntity updatedEntity = new AmountsandratesEntity();
        updatedEntity.setDailyrentalrate(6.0);
        updatedEntity.setDailylatefeefine(1.5);
        updatedEntity.setReparationcharge(12.0);

        when(amountsandratesRepository.findAll()).thenReturn(List.of(existingEntity));
        when(amountsandratesRepository.save(existingEntity)).thenReturn(updatedEntity);

        // When calling the method
        AmountsandratesEntity result = amountsandratesServices.updateAmountAndRates(updatedEntity);

        // Then verify that the entity is updated
        assertEquals(6.0, result.getDailyrentalrate());
        assertEquals(1.5, result.getDailylatefeefine());
        assertEquals(12.0, result.getReparationcharge());

        // Verify that save was called once
        verify(amountsandratesRepository, times(1)).save(existingEntity);
    }
}
