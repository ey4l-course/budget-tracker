package com.budget.transactions.service;

import com.budget.transactions.model.FetchDashDTO;
import com.budget.transactions.model.TransactionDTO;
import com.budget.transactions.model.TransactionEntity;
import com.budget.transactions.model.UpdateBudgetConfigDTO;
import com.budget.transactions.repository.TxnRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        int[] updated = repo.addNewTxn(data);
        for (FetchDashDTO key : keys){ cacheFacade.clearCache(key); }
        return String.format("Received %d transactions, successfully inserted %d transactions", data.size(), Arrays.stream(updated).sum());
    }
}
