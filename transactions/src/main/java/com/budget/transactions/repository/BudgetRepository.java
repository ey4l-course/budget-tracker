package com.budget.transactions.repository;

import com.budget.common.dto.BudgetCatDTO;
import com.budget.transactions.model.UpdateBudgetConfigDTO;
import com.budget.transactions.repository.Mapper.BudgetCatMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetRepository {
    private final JdbcTemplate jdbc;
    private NamedParameterJdbcTemplate namedJdbc;

    public BudgetRepository (JdbcTemplate jdbc,
                             NamedParameterJdbcTemplate namesJdbc){
        this.jdbc = jdbc;
        this.namedJdbc = namesJdbc;
    }

    public int[] updateBudgetConfig(List<UpdateBudgetConfigDTO> data) {
        String sql = "INSERT INTO budget_configs (username, category_name, category_type, is_manual, amount_limit) " +
                "VALUES (:username, :categoryName, :categoryType, true, :amount) " +
                "ON DUPLICATE KEY UPDATE amount_limit = VALUES(amount_limit)";
        SqlParameterSource[] batch = data.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        return namedJdbc.batchUpdate(sql, batch);
    }

    public List<BudgetCatDTO> getConfigs (String username) {
        String sql = "SELECT category_name, category_type, amount_limit  FROM budget_configs WHERE username = ? ORDER BY category_name";
        return jdbc.query(sql,new BudgetCatMapper(), username);
    }

    public String isIncomeOrExpense(String userDefinedCategory, String username) {
        String sql = "SELECT category_type FROM budget_configs WHERE category_name = ? AND username = ?";
        return jdbc.queryForObject(sql, String.class, userDefinedCategory, username);
    }
}
