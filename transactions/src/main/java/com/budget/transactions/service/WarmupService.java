package com.budget.transactions.service;

import com.budget.common.client.InternalUserClient;
import com.budget.common.dto.BudgetCatDTO;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.transactions.model.*;
import com.budget.transactions.repository.BudgetRepository;
import com.budget.transactions.repository.WarmupRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WarmupService {
    private final WarmupRepository repo;
    private final DateTimeFormatter formatter;
    private final BudgetRepository cfgRepo;
    private final InternalUserClient userClient;

    public WarmupService(WarmupRepository repo,
                         BudgetRepository cfgRepo,
                         InternalUserClient userClient) {
        this.repo = repo;
        this.formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.cfgRepo = cfgRepo;
        this.userClient = userClient;
    }

    private boolean isAccountActivated (String username){
        List<BudgetCatDTO> configs = cfgRepo.getConfigs(username);
        if (configs.isEmpty() || configs.size() == 1 && !"INCOME".equals(configs.getFirst().getCategoryType())){
            userClient.deactivateAccount(username);
            return false;
        }
        return true;
    }

    public List<CategoryDTO> warmup (FetchDashDTO dto) {
        if (!isAccountActivated(dto.getUsername()))
            return null;
        List<CategoryDTO> fetchedCategories = repo.fetchCategories(dto);
        List<UpdateBudgetConfigDTO> detectedBugs = repo.bugDetector(dto);
        if (!detectedBugs.isEmpty()){
            for (UpdateBudgetConfigDTO cat : detectedBugs){
                fetchedCategories.add(new CategoryDTO(
                        cat.getCategoryName(),
                        TxnType.valueOf(cat.getCategoryType()),
                        cat.getAmount(),
                        cat.getAmount(),
                        null
                ));
            }
            cfgRepo.updateBudgetConfig(detectedBugs);
        }
        List<TransactionDTO> rs = repo.fetchTransactions(dto);
        Map<String,CategoryDTO> dictionary = new HashMap<>();
        for (CategoryDTO cat : fetchedCategories){
            dictionary.put(cat.getName()+"-"+cat.getCategoryType(), cat);
        }

        for (TransactionDTO txn : rs){
            String lookupKey = txn.getCategory()+"-"+txn.getTxnType();
            CategoryDTO bucket = dictionary.get(lookupKey);
            if (bucket == null)
                throw new CriticalIncidentException(
                        "CRITICAL DOMAIN ERROR: Transaction " + txn.getId() +
                        " attempted to pour into missing bucket: " + lookupKey, null);
            if (bucket.getContent() == null)
                bucket.setContent(new ArrayList<>());
            bucket.getContent().add(
                    new TransactionResponseDTO(
                            txn.getId(),
                            txn.getName(),
                            txn.getDate().format(formatter),
                            txn.getAmount(),
                            txn.getComment(),
                            txn.getFlag()
                    )
            );
        }
        fetchedCategories.sort(Comparator.comparing(CategoryDTO::getName));
        return fetchedCategories;
    }
}
