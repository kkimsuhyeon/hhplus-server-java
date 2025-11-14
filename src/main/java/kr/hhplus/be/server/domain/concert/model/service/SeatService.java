package kr.hhplus.be.server.domain.concert.model.service;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;

public interface SeatService {

    SeatEntity getSeat(String seatId);

    SeatEntity getSeatExclusive(String seatId);
}
