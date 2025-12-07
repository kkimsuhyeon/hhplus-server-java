package kr.hhplus.be.server.domain.token.application;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueueTokenServiceTest {

    @InjectMocks
    QueueTokenService queueTokenService;

    @Mock
    QueueTokenRepository queueTokenRepository;



}