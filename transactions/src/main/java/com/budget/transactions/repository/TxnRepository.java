package com.budget.transactions.repository;

import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.transactions.model.TransactionEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class TxnRepository {
    private final NamedParameterJdbcTemplate jdbc;
    @Value("classpath:sql/newTxn.sql")
    private Resource newTxn;
    private String newTxnStr;
    public TxnRepository (NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostConstruct
    private void initializeAndVerifySql () {
        try {
            newTxnStr = StreamUtils.copyToString(newTxn.getInputStream(), StandardCharsets.UTF_8);
        }catch (IOException e){
            throw new CriticalIncidentException("Txn repo failed to boot: missing SQL files", e);
        }
    }
    public int[] addNewTxn(List<TransactionEntity> data) {
        SqlParameterSource[] batch = data.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        return jdbc.batchUpdate(newTxnStr, batch);
    }
}
