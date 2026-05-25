package com.budget.transactions.service;

import com.budget.common.dto.BudgetCatDTO;
import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.model.FetchDashDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TxnCacheFacade {
    private final WarmupService service;

    public TxnCacheFacade (WarmupService service) { this.service = service; }

    @CachePut(value = "warmupCache")
    public List<CategoryDTO> warmUpFacade (FetchDashDTO dto) {
        return service.warmup(dto);
    }

    @CachePut(value = "warmupCache")
    public List<BudgetCatDTO> configWarmupFacade (String username) { return service.getConfigs(username); }

    @CacheEvict (value = "warmupCache")
    public void clearCache (FetchDashDTO dto) {}

    @CacheEvict (value = "warmupCache")
    public void clearConfigCache (String username) {}

    @Cacheable (value = "warmupCache", sync = true)
    public List<CategoryDTO> getCache(FetchDashDTO dto) {
        return service.warmup(dto);
    }

    @Cacheable (value = "warmupCache", sync = true)
    public List<BudgetCatDTO> getConfigCache (String username) { return service.getConfigs(username); }
}
