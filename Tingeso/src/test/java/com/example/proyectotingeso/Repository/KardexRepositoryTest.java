package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.KardexEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class KardexRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KardexRepository kardexRepository;

    @Test
    void whenFindByDateBetweenOrderByDateDesc_thenReturnKardex() {
        // given
        KardexEntity kardex = new KardexEntity(null, 1L, LocalDate.now(), "Pepito", 1L, 2);
        entityManager.persistAndFlush(kardex);

        // when
        List<KardexEntity> foundKardex = kardexRepository.findByDateBetweenOrderByDateDesc(LocalDate.now().minusDays(1), LocalDate.now());

        // then
        assertThat(foundKardex).isNotEmpty();
        assertThat(foundKardex.get(0).getStateToolsId()).isEqualTo("active");
    }
}
