package kr.hhplus.be.server.domain.payment.adapter.in.web.mapper;

import kr.hhplus.be.server.application.dto.PayCommand;
import kr.hhplus.be.server.domain.payment.adapter.in.web.request.PayRequest;

public class PaymentCommandMapper {

    public static PayCommand toPayCommand(PayRequest request, String userId) {
        return PayCommand.builder()
                .reservationId(request.getReservationId())
                .userId(userId)
                .build();
    }
}
