package kr.hhplus.be.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV3 {

    @Pointcut("execution(* kr.hhplus.be.aop..*(..))")
    private void allOrder() {
    }

    @Pointcut("execution(* *..*Service.*(..))")
    private void allService() {
    }

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log], {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    @Around("allOrder() && allService()")
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
