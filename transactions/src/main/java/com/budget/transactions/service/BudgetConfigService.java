package com.budget.transactions.service;

import com.budget.common.client.InternalUserClient;
import com.budget.common.dto.BudgetCatDTO;
import com.budget.transactions.model.UpdateBudgetConfigDTO;
import com.budget.transactions.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BudgetConfigService {
    private final BudgetRepository repo;
    private final InternalUserClient userClient;
    private final Map<String, BigDecimal> expenseDistBaseline = Map.of(
            "housing", new BigDecimal("0.3"),
            "vehicle", new BigDecimal("0.15"),
            "groceries", new BigDecimal("0.12"),
            "education", new BigDecimal("0.1"),
            "leisure", new BigDecimal("0.08"),
            "vacations", new BigDecimal("0.05")
    );

    public BudgetConfigService (BudgetRepository bgtRepo,
                                InternalUserClient userClient) {
        this.repo = bgtRepo;
        this.userClient = userClient;
    }

    public List<BudgetCatDTO> activateUser (List<UpdateBudgetConfigDTO> income, String username){
        userClient.activateAccount(username);
        for (UpdateBudgetConfigDTO el : income){
            if (el.getUsername() == null || el.getUsername().isEmpty())
                el.setUsername(username);
        }
        repo.updateBudgetConfig(income);
        return applyDefaults(income.getFirst().getAmount());
    }

    public String updateBudgetConfig (List<UpdateBudgetConfigDTO> expenses, String username){
        for (UpdateBudgetConfigDTO exp : expenses){
            if (exp.getUsername() == null || exp.getUsername().isEmpty())
                exp.setUsername(username);
        }
        return "Updated" + Arrays.stream(repo.updateBudgetConfig(expenses)).sum() + "entries";
    }

    private List<BudgetCatDTO> applyDefaults (BigDecimal income) {
        List<BudgetCatDTO> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : expenseDistBaseline.entrySet()){
            result.add(new BudgetCatDTO(
                    entry.getKey(),
                    "EXPENSE",
                    entry.getValue().multiply(income)
            ));
        }
        return result;
    }


}
