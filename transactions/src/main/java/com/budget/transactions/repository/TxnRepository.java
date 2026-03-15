package com.budget.transactions.repository;

import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.transactions.model.TransactionDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class TxnRepository {
    private final NamedParameterJdbcTemplate jdbc;
    @Value("classpath:sql/warmup.sql")
    private Resource warmupQuery;

    public TxnRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<TransactionDTO> warmup(String username) {
        try {
            String sql = StreamUtils.copyToString(warmupQuery.getInputStream(), StandardCharsets.UTF_8);
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("username", username);
            return jdbc.query(sql, params, (rs, rowNum) ->
                    new TransactionDTO(
                            rs.getLong("id"),
                            rs.getString("category"),
                            rs.getString("name"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getBigDecimal("amount"),
                            rs.getString("comment"),
                            rs.getInt("system_flag")
                    )
            );
        } catch (IOException e) {
            throw new CriticalIncidentException("Transaction repository failed to boot", e.getCause());
        }
    }

    @PostConstruct
    private void getStringFromFile (){
        try {
            StreamUtils.copyToString(warmupQuery.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e){
            System.out.println("FAILED TO BOOT: failed to load sql");
        }
    }
}