package kr.hhplus.be.server.domain.token.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.token.exception.TokenErrorCode;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.TokenStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {

    @InjectMocks
    QueueTokenService queueTokenService;

    @Mock
    QueueTokenRepository queueTokenRepository;

    @Test
    void issueToken_Pass() {
        QueueToken expected = QueueToken.create("userId");
        expected.activate();
        when(queueTokenRepository.findByUserId("userId")).thenReturn(Optional.of(expected));

        QueueToken actual = queueTokenService.issueToken("userId");

        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getStatus()).isEqualTo(TokenStatus.ACTIVE);
    }

    @Test
    void issueToken_Issue() {
        when(queueTokenRepository.findByUserId("userId")).thenReturn(Optional.empty());
        when(queueTokenRepository.save(any(QueueToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QueueToken actual = queueTokenService.issueToken("userId");

        verify(queueTokenRepository, times(1)).save(any(QueueToken.class));
        assertThat(actual.getUserId()).isEqualTo("userId");
        assertThat(actual.getStatus()).isEqualTo(TokenStatus.WAITING);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void getToken_Success() {
        QueueToken token = QueueToken.builder().id("1234").expiredAt(LocalDateTime.now().plusMinutes(1)).build();
        when(queueTokenRepository.findById("1234")).thenReturn(Optional.of(token));

        QueueToken actual = queueTokenService.getToken("1234");

        assertThat(actual).isNotNull();
    }

    @Test
    void getToken_Empty() {
        when(queueTokenRepository.findById("1234")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queueTokenService.getToken("1234"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TokenErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    void getToken_Expired() {
        QueueToken expiredToken = QueueToken.builder().id("1234").status(TokenStatus.EXPIRED).build();
        when(queueTokenRepository.findById("1234")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> queueTokenService.getToken("1234"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TokenErrorCode.ALREADY_EXPIRED.getMessage());
    }

    @Test
    void deleteExpiredTokens_Success() {
        QueueToken expired1 = QueueToken.builder().id("1").status(TokenStatus.EXPIRED).build();
        QueueToken expired2 = QueueToken.builder().id("2").status(TokenStatus.EXPIRED).build();
        when(queueTokenRepository.findByStatus(TokenStatus.EXPIRED, Integer.MAX_VALUE)).thenReturn(List.of(expired1, expired2));

        queueTokenService.deleteExpiredTokens();

        verify(queueTokenRepository, times(1)).delete("1");
        verify(queueTokenRepository, times(1)).delete("2");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(queueTokenRepository, times(2)).delete(captor.capture());

        List<String> deletedId = captor.getAllValues();
        assertThat(deletedId).containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void expireTokens_Success() {
        QueueToken expired1 = QueueToken.builder().id("1").expiredAt(LocalDateTime.now().minusMinutes(1)).build();
        QueueToken expired2 = QueueToken.builder().id("2").expiredAt(LocalDateTime.now().minusMinutes(1)).build();
        QueueToken waiting1 = QueueToken.builder().id("3").expiredAt(LocalDateTime.now().plusMinutes(1)).build();
        when(queueTokenRepository.findAll()).thenReturn(List.of(expired1, expired2, waiting1));

        queueTokenService.expireTokens();

        assertThat(expired1.getStatus()).isEqualTo(TokenStatus.EXPIRED);
        assertThat(expired2.getStatus()).isEqualTo(TokenStatus.EXPIRED);
        assertThat(waiting1.getStatus()).isNotEqualTo(TokenStatus.EXPIRED);
    }

    @Test
    void activateTokens_Success() {
        QueueToken token1 = QueueToken.builder().id("1").status(TokenStatus.WAITING).build();
        QueueToken token2 = QueueToken.builder().id("2").status(TokenStatus.WAITING).build();
        QueueToken token3 = QueueToken.builder().id("3").status(TokenStatus.WAITING).build();

        when(queueTokenRepository.findByStatus(TokenStatus.WAITING, 2)).thenReturn(List.of(token1, token2));

        queueTokenService.activateTokens(2);

        assertThat(token1.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(token2.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(token3.getStatus()).isEqualTo(TokenStatus.WAITING);
    }

}