package com.budget.transactions.repository.Mapper;

import com.budget.transactions.model.TxnType;
import com.budget.transactions.model.TransactionDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDtoMapper implements RowMapper<TransactionDTO> {
    @Override
    public TransactionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        String typeStr = rs.getString("category_type");
        TxnType type = TxnType.valueOf(typeStr);
        return new TransactionDTO(
                rs.getLong("id"),
                rs.getString("category"),
                type,
                rs.getString("name"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getBigDecimal("amount"),
                rs.getString("comment"),
                rs.getInt("system_flag")
        );
    }
}
