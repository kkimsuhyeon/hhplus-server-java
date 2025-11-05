package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.model.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    @Override
    public SeatEntity getSeat(String seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
    }
}
