package kr.hhplus.be.server.domain.concert.application;

import kr.hhplus.be.server.domain.concert.model.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository repository;

    @Transactional(readOnly = true)
    public Concert getConcert(String concertId) {
        return repository.findById(concertId);
    }

    @Transactional(readOnly = true)
    public List<Concert> getConcerts() {
        return repository.findAll();
    }

    @Transactional
    public Concert createConcert(Concert concert) {
        return repository.save(concert);
    }
}
