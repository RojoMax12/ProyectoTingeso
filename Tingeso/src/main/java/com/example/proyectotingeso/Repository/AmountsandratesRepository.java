package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.AmountsandratesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmountsandratesRepository extends JpaRepository<AmountsandratesEntity, Long>{

    public AmountsandratesEntity findByDailyrentalrateAndDailylatefeefine(double dailyrentalrate, double dailylatefeefeefine);

    public Optional<AmountsandratesEntity> findById(Long id);
}
