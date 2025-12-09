package kr.hhplus.be.server.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.token.application.QueueTokenService;
import kr.hhplus.be.server.domain.token.exception.TokenErrorCode;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidationInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Queue-Token";

    private final QueueTokenService queueTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenId = request.getHeader(TOKEN_HEADER);

        if (tokenId == null || StringUtils.isBlank(tokenId)) {
            throw new BusinessException(TokenErrorCode.INVALID);
        }

        QueueToken token = queueTokenService.getToken(tokenId);

        if (!token.isActive()) throw new BusinessException(TokenErrorCode.NOT_ACTIVE);

        request.setAttribute("userId", token.getUserId());
        request.setAttribute("tokenId", token.getId());

        log.info("token validation success : {}, {}", token.getId(), token.getUserId());

        return true;
    }
}
