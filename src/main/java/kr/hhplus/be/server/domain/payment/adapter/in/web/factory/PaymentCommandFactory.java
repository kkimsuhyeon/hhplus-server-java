package kr.hhplus.be.server.domain.payment.adapter.in.web.factory;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.payment.adapter.in.web.request.PayRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentCommandFactory {

    public PayCommand toPayCommand(PayRequest request, String userId) {
        return PayCommand.builder()
                .reservationId(request.getReservationId())
                .userId(userId)
                .build();
    }
}
