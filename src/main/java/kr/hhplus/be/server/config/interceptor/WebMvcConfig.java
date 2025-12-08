package kr.hhplus.be.server.config.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenValidationInterceptor tokenValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .addPathPatterns(
                        "/api/v1/concerts/**",
                        "/api/v1/reservations/**",
                        "/api/v1/payments/**"
                )
                .excludePathPatterns(
                        "/api/v1/token/**",
                        "/api/v1/users/balance/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                )
                .order(1);
    }
}
