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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TxnRepository {
    private final NamedParameterJdbcTemplate jdbc;
    @Value("classpath:sql/warmup.sql")
    private Resource warmupQuery;

    public TxnRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, BigDecimal> warmup(int userId) throws IOException {
        String sql = StreamUtils.copyToString(warmupQuery.getInputStream(), StandardCharsets.UTF_8);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);
        return jdbc.query(sql, params, rs -> {
            Map<String, BigDecimal> totals = new HashMap<>();
            while (rs.next()) {
                totals.put(rs.getString("category"), rs.getBigDecimal("sub_total"));
            }
            return totals;
        });
    }

    // Version 2
    public List<TransactionDTO> newWarmup(int userId) {
        try {
            String sql = StreamUtils.copyToString(warmupQuery.getInputStream(), StandardCharsets.UTF_8);
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("userId", userId);
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