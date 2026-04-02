package com.budget.users.service;

import com.budget.common.dto.FeignLoginDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.users.client.AuthClient;
import com.budget.users.model.UpdateBudgetConfigDTO;
import com.budget.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository repo;
    private AuthClient authClient;
    private final Map<String, BigDecimal> expenseDistBaseline = Map.of(
            "housing", new BigDecimal("0.3"),
            "vehicle", new BigDecimal("0.15"),
            "groceries", new BigDecimal("0.12"),
            "education", new BigDecimal("0.1"),
            "leisure", new BigDecimal("0.08"),
            "vacations", new BigDecimal("0.05")
    );

    public UserService (UserRepository repo,
                        AuthClient authClient){
        this.repo = repo;
        this.authClient = authClient;
    }

    public List<String> getAllUsers() {
        return repo.getAllUsernames();
    }

    public void register(RegisterDto user) {
        repo.register(user);
    }

    public FeignLoginDTO login(String username) {
        return repo.login(username);
    }

    public HashMap<String, BigDecimal> activateAccount (UpdateBudgetConfigDTO income){
        repo.updateBudgetConfig(List.of(income));
        HashMap<String, BigDecimal> result = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : expenseDistBaseline.entrySet()){
            result.put(entry.getKey(), income.getAmount().multiply(entry.getValue()));
        }
        repo.activateUser(income.getUsername());
        return result;
    }

    public String updateBudgetConfig (List<UpdateBudgetConfigDTO> expenses){
        return "Updated" + Arrays.stream(repo.updateBudgetConfig(expenses)).sum() + "entries";
    }
}
