package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.AmountsandratesEntity;
import com.example.proyectotingeso.Entity.RoleEntity;
import com.example.proyectotingeso.Repository.AmountsandratesRepository;
import com.example.proyectotingeso.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AmountsandratesServices {

    @Autowired
    private AmountsandratesRepository amountsandratesRepository;

    public AmountsandratesEntity createAmountsAndRates() {
        // Buscar si ya existe el registro con amount=0.0 y rate=0.0
        AmountsandratesEntity existing = amountsandratesRepository.findByDailyrentalrateAndDailylatefeefine(0.0, 0.0);

        if (existing != null) {
            return existing; // ya existe, lo retornamos
        }

        // Si no existe, creamos uno nuevo
        AmountsandratesEntity newEntity = new AmountsandratesEntity(null, 0.0, 0.0);
        return amountsandratesRepository.save(newEntity);
    }

    public Optional<AmountsandratesEntity> getAmountsAndRates() {
        return amountsandratesRepository.findById(1L);
    }

    public AmountsandratesEntity updateAmountAndRates(double mountdailyrentalrate, double mountdailylatefeefeefine) {
        AmountsandratesEntity existing = amountsandratesRepository.findByDailyrentalrateAndDailylatefeefine(mountdailyrentalrate, mountdailylatefeefeefine);
        existing.setDailyrentalrate(mountdailyrentalrate);
        existing.setDailylatefeefine(mountdailylatefeefeefine);
        return amountsandratesRepository.save(existing);
    }


}
