package kr.hhplus.be.server.domain.payment.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.port.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentRepository repository;

    public Payment getPayment(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.NOT_FOUND));
    }
}
