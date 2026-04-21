package com.budget.transactions.repository.Mapper;

import com.budget.common.dto.BudgetCatDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BudgetCatMapper implements RowMapper<BudgetCatDTO> {
    @Override
    public BudgetCatDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BudgetCatDTO(
                rs.getString("category_name"),
                rs.getString("category_type"),
                rs.getBigDecimal("amount_limit")
        );
    }
}
