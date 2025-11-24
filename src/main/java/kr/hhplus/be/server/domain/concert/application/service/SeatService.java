package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.model.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService implements SeatService {

    private final SeatRepository seatRepository;

    @Override
    @Transactional(readOnly = true)
    public SeatEntity getSeat(String seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public SeatEntity getSeatExclusive(String seatId) {
        return seatRepository.findByIdForUpdate(seatId)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }
}
