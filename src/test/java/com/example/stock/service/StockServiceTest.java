package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 재고감소() throws Exception {
        //given
        Stock pre = stockRepository.save(new Stock(1L, 100L));

        //when
        stockService.decrease(pre.getId(), 1L);

        //then
        Stock after = stockRepository.findById(pre.getId()).orElseThrow(() -> new RuntimeException("no such id value data: " + 1L));
        assertThat(after.getQuantity()).isEqualTo(99L);
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException{
        //given
        Stock pre = stockRepository.save(new Stock(1L, 100L));
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(pre.getId(), 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //then
        Stock after = stockRepository.findById(pre.getId()).orElseThrow(() -> new RuntimeException("no such id value data: " + 1L));
        assertThat(after.getQuantity()).isEqualTo(0L);
    }
}
