package kr.hhplus.be.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Slf4j
@Aspect
public class AspectV5Order {

    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("kr.hhplus.be.aop.order.aop.PointCut.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log], {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect {
        @Around("kr.hhplus.be.aop.order.aop.PointCut.allOrder() && kr.hhplus.be.aop.order.aop.PointCut.allService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
            try {
                log.info("[transaction start] {}", joinPoint.getSignature());
                Object proceed = joinPoint.proceed();
                log.info("[trasaction commit] {}", joinPoint.getSignature());

                return proceed;
            } catch (Exception e) {
                log.info("[trasaction fail] {}", joinPoint.getSignature());
                throw e;
            } finally {
                log.info("[trasaction release] {}", joinPoint.getSignature());
            }
        }
    }
}
