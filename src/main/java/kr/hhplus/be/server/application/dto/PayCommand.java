package kr.hhplus.be.server.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayCommand {

    private String reservationId;

    private String userId;

}
