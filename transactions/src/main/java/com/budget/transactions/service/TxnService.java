package com.budget.transactions.service;

import com.budget.common.dto.BudgetCatDTO;
import com.budget.transactions.model.FetchDashDTO;
import com.budget.transactions.model.TransactionEntity;
import com.budget.transactions.repository.TxnRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TxnService {
    private final TxnCacheFacade cacheFacade;
    private final TxnRepository repo;
    private final DataHandlerUtil util;

    public TxnService (TxnRepository repo,
                       TxnCacheFacade cacheFacade,
                       DataHandlerUtil util){
        this.repo = repo;
        this .cacheFacade = cacheFacade;
        this.util = util;
    }

    public String newTxn(List<TransactionEntity> data) {
        util.validateTxn(data);
        List<FetchDashDTO> keys = util.generateCacheKeys(data);
        List<BudgetCatDTO> configs = cacheFacade.getConfigCache(data.getFirst().getUsername());
        Map<String, String> configsMap = configs.stream()
                .collect(Collectors.toMap(BudgetCatDTO::getCategoryName, BudgetCatDTO::getCategoryType, (existing, replacement) -> existing));
        for (TransactionEntity txn : data){
            if (txn.getCategoryType() == null || txn.getCategoryType().isBlank()) {
                if (!configsMap.containsKey(txn.getUserDefinedCategory()))
                    throw new  IllegalArgumentException("Category " + txn.getUserDefinedCategory() + " not found for user");
                txn.setCategoryType(configsMap.get(txn.getUserDefinedCategory()));
            }
        }
        int[] updated = repo.addNewTxn(data);
        for (FetchDashDTO key : keys){ cacheFacade.clearCache(key); }
        return String.format("Received %d transactions, successfully inserted %d transactions", data.size(), Arrays.stream(updated).sum());
    }
}
