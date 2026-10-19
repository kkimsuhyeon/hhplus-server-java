package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import kr.hhplus.be.server.domain.payment.PaymentFixture;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.port.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PaymentRepositoryAdapter.class)
class PaymentRepositoryAdapterTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void save_test1() {
        Payment payment = PaymentFixture.aPayment()
                .amount(BigDecimal.valueOf(1000))
                .build();
        Payment savedPayment = paymentRepository.save(payment);
        em.flush();
        em.clear();

        Optional<Payment> actual = paymentRepository.findById(savedPayment.getId());

        assertThat(actual).isPresent().get().satisfies(p -> {
            assertThat(p.getId()).isEqualTo(savedPayment.getId());
        });
    }


}