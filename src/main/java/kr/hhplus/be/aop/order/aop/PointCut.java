package kr.hhplus.be.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;


public class PointCut {

    @Pointcut("execution(* kr.hhplus.be.aop..*(..))")
    public void allOrder() {
    }

    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {
    }

    @Pointcut("allOrder() && allService()")
    public void orderAndService() {
    }

}
