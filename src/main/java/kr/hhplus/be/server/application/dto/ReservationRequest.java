package kr.hhplus.be.server.application.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    // todo remove, 임시로 userId 추가
    private String userId;

    private String concertId;
    private String scheduleId;
    private String seatId;

}
