package com.example.stock.facade;

import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;

    private final StockService stockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        RLock lock = redissonClient.getLock(id.toString());

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);// 락 획득 시도 시간, 락 점유 시간

            if (!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decrease(id, quantity);
        } finally {
            lock.unlock();
        }
    }
}
