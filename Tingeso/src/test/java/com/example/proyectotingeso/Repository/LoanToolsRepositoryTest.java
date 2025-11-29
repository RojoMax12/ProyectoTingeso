package com.example.proyectotingeso.Repository;

import com.example.proyectotingeso.Entity.LoanToolsEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Asegúrate de que el perfil "test" esté configurado
class LoanToolsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private LoanToolsRepository loanToolsRepository;

    @Test
    void whenFindAllByClientid_thenReturnLoanTools() {
        // given
        // Crear una nueva entidad de préstamo
        LoanToolsEntity loan = new LoanToolsEntity();
        loan.setInitiallenddate(LocalDate.now());
        loan.setFinalreturndate(LocalDate.now().plusDays(7));
        loan.setClientid(1L);
        loan.setToolid(2L);
        loan.setStatus("active");
        loan.setRentalFee(50.0);
        loan.setLateFee(10.0);
        loan.setDamageFee(5.0);

        // Persistir la entidad en la base de datos de prueba
        entityManager.persistAndFlush(loan);

        // when
        // Obtener los préstamos por clientid
        List<LoanToolsEntity> foundLoans = loanToolsRepository.findAllByClientid(1L);

        // then
        // Verificar que la lista no esté vacía y que el clientid sea 1L
        assertThat(foundLoans).isNotEmpty();
        assertThat(foundLoans.get(0).getClientid()).isEqualTo(1L);
    }
}
