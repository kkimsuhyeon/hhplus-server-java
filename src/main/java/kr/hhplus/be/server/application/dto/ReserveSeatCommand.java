package kr.hhplus.be.server.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveSeatCommand {

    private String userId;

    private String seatId;

}
