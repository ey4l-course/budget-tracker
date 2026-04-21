package com.budget.transactions.repository.Mapper;

import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.model.TxnType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDtoMapper implements RowMapper <CategoryDTO> {
    @Override
    public CategoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        String typeStr = rs.getString("category_type");
        TxnType type = TxnType.valueOf(typeStr);
        return new CategoryDTO(
                rs.getString("category_name"),
                type,
                rs.getBigDecimal("current_subtotal"),
                rs.getBigDecimal("amount_limit"),
                null
        );
    }
}
