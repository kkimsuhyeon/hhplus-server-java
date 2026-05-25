package kr.hhplus.be.aop.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderRepository {

    public void save() {
        log.info("repository save");
    }
}
