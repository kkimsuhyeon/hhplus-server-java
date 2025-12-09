package kr.hhplus.be.server.domain.token.adapter.in.scheduler;

import kr.hhplus.be.server.domain.token.application.QueueTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final QueueTokenService queueTokenService;

    @Scheduled(fixedDelay = 100000)
    public void activeToken() {
        queueTokenService.activateTokens(1);
    }

    @Scheduled(fixedDelay = 100000)
    public void deleteExpiredToken() {
        queueTokenService.deleteExpiredTokens();
    }

}

