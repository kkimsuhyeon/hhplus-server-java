package kr.hhplus.be.server.domain.payment.application;

import kr.hhplus.be.server.domain.payment.adapter.out.persistence.PaymentRepositoryAdapter;
import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepositoryAdapter repository;

    @Transactional
    public Payment create(Payment payment) {
        return repository.save(payment);
    }

    public Payment update(Payment payment) {
        return repository.update(payment);
    }
}
