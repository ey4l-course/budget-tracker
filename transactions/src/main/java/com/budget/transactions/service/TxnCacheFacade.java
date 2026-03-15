package com.budget.transactions.service;

import com.budget.transactions.model.CategoryDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TxnCacheFacade {
    private final TxnService service;

    public TxnCacheFacade (TxnService service) { this.service = service; }

    @CachePut(value = "warmupCache", key = "#a0")
    public List<CategoryDTO> warmUpFacade (String username) {
        return service.warmup(username);
    }

    @CacheEvict (value = "warmupCache", key = "#a0")
    public void clearCache (String username) {}

    @Cacheable (value = "warmupCache", key = "#a0", sync = true)
    public List<CategoryDTO> getCache(String username) {
        return service.warmup(username);
    }
}
