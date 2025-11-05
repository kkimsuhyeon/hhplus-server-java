package kr.hhplus.be.server.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveSeatCommand {

    // todo remove, 임시로 userId 추가
    private String userId;

    private String seatId;

}
