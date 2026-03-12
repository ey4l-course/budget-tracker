package com.budget.transactions.service;

import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.model.TransactionDTO;
import com.budget.transactions.model.TransactionResponseDTO;
import com.budget.transactions.repository.TxnRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TxnService {
    private final TxnRepository repo;
    private final DateTimeFormatter formatter;

    public TxnService (TxnRepository repo) {
        this.repo = repo;
        this.formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public List<CategoryDTO> warmup (int userID) {
        List<TransactionDTO> rs = repo.newWarmup(userID);
        List<CategoryDTO> result = new ArrayList<>();
        if (rs.isEmpty()) return result;

        String currentCategory = "";
        for (TransactionDTO txn : rs){
            if (!currentCategory.equals(txn.getCategory())){
                currentCategory = txn.getCategory();
                result.add(new CategoryDTO(
                        currentCategory,
                        BigDecimal.ZERO,
                        new ArrayList<>()
                ));
            }
            CategoryDTO current = result.getLast();
            current.getContent().add(
                    new TransactionResponseDTO(
                            txn.getId(),
                            txn.getName(),
                            txn.getDate().format(formatter),
                            txn.getAmount(),
                            txn.getComment(),
                            txn.getFlag()
                    )
            );
            current.updateSubtotal(txn.getAmount());
        }
        return result;
    }
}
