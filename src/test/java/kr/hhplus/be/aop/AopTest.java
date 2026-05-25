package kr.hhplus.be.aop;

import kr.hhplus.be.aop.order.OrderRepository;
import kr.hhplus.be.aop.order.OrderService;
import kr.hhplus.be.aop.order.aop.AspectV1;
import kr.hhplus.be.aop.order.aop.AspectV2;
import kr.hhplus.be.aop.order.aop.AspectV3;
import kr.hhplus.be.aop.order.aop.AspectV4PointCut;
import kr.hhplus.be.aop.order.aop.AspectV5Order;
import kr.hhplus.be.server.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = ServerApplication.class, properties = "spring.profiles.active=local")
@org.springframework.context.annotation.Import({OrderService.class, OrderRepository.class, AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
public class AopTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void aopInfo() {
        log.info("isAopProxy = {}", AopUtils.isAopProxy(orderService));
        log.info("isAopProxy = {}", AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success(){
        orderService.save();
    }

}
