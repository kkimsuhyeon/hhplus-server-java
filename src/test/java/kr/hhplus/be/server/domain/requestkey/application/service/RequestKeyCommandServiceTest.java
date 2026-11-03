package kr.hhplus.be.server.domain.requestkey.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.requestkey.exception.RequestKeyErrorCode;
import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import kr.hhplus.be.server.domain.requestkey.port.RequestKeyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestKeyCommandServiceTest {

    private static final String USER_ID = "userId";
    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();

    @InjectMocks
    private RequestKeyCommandService requestKeyCommandService;

    @Mock
    private RequestKeyRepository requestKeyRepository;

    @Test
    void 기록이_없으면_처음_요청이다() {
        when(requestKeyRepository.findRequest(USER_ID, IDEMPOTENCY_KEY)).thenReturn(Optional.empty());

        assertThat(requestKeyCommandService.isRetry(USER_ID, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "hash"))).isFalse();
    }

    @Test
    void 해시가_다르면_KEY_REUSED() {
        when(requestKeyRepository.findRequest(USER_ID, IDEMPOTENCY_KEY)).thenReturn(Optional.of(RequestKey.of("CHARGE", "hash")));

        assertThatThrownBy(() -> requestKeyCommandService.isRetry(USER_ID, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "hash1")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(RequestKeyErrorCode.KEY_REUSED);
    }

    @Test
    void operation이_다르면_KEY_REUSED() {
        when(requestKeyRepository.findRequest(USER_ID, IDEMPOTENCY_KEY)).thenReturn(Optional.of(RequestKey.of("CHARGE", "hash")));

        assertThatThrownBy(() -> requestKeyCommandService.isRetry(USER_ID, IDEMPOTENCY_KEY, RequestKey.of("PAY", "hash")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(RequestKeyErrorCode.KEY_REUSED);
    }

    @Test
    void 기록과_같은_요청이면_재시도다() {
        when(requestKeyRepository.findRequest(USER_ID, IDEMPOTENCY_KEY)).thenReturn(Optional.of(RequestKey.of("CHARGE", "hash")));

        assertThat(requestKeyCommandService.isRetry(USER_ID, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "hash"))).isTrue();
    }

}