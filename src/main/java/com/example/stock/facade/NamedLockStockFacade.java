package com.example.stock.facade;

import com.example.stock.repository.LockRepositoryImpl;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepositoryImpl lockRepositoryImpl;

    private final StockService stockService;

    @Transactional
    public void decrease(Long id, Long quantity) {
        try{
            lockRepositoryImpl.getLock(id.toString());
            stockService.decrease(id, quantity);
        } finally {
            lockRepositoryImpl.releaseLock(id.toString());
        }
    }
}
