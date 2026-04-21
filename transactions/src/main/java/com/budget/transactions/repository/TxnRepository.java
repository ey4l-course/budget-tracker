package com.budget.transactions.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TxnRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public TxnRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
}