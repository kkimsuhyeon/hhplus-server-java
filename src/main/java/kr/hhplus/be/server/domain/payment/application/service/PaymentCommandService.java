package kr.hhplus.be.server.domain.payment.application.service;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.port.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentRepository repository;

    @Transactional
    public Payment create(Payment payment) {
        return repository.save(payment);
    }

    @Transactional
    public Payment update(Payment payment) {
        return repository.update(payment);
    }
}
