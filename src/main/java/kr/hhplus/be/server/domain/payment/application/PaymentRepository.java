package kr.hhplus.be.server.domain.payment.application;

import kr.hhplus.be.server.domain.payment.model.Payment;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findById(String id);

    Payment save(Payment payment);

    Payment update(Payment payment);
}
