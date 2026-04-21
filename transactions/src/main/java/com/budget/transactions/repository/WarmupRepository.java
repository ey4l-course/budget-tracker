package com.budget.transactions.repository;

import com.budget.common.dto.BudgetCatDTO;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.transactions.model.CategoryDTO;
import com.budget.transactions.model.TransactionDTO;
import com.budget.transactions.model.UpdateBudgetConfigDTO;
import com.budget.transactions.repository.Mapper.BudgetCatMapper;
import com.budget.transactions.repository.Mapper.CategoryDtoMapper;
import com.budget.transactions.repository.Mapper.TransactionDtoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class WarmupRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Value("classpath:sql/fetchCategories.sql")
    private Resource fetchCategories;
    @Value("classpath:sql/bugDetector.sql")
    private Resource bugDetector;
    @Value("classpath:sql/warmup.sql")
    private Resource warmupQuery;

    private String fetchCategoriesSql;
    private String bugDetectorSql;
    private String warmupQuerySql;

    public WarmupRepository (NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    private void initializeAndVerifySql() {
        try {
            fetchCategoriesSql = StreamUtils.copyToString(fetchCategories.getInputStream(), StandardCharsets.UTF_8);
            bugDetectorSql = StreamUtils.copyToString(bugDetector.getInputStream(), StandardCharsets.UTF_8);
            warmupQuerySql = StreamUtils.copyToString(warmupQuery.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CriticalIncidentException("Warmup repo failed to boot: missing SQL files", e);
        }
    }

    public List<CategoryDTO> fetchCategories (String username) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("username", username);
        return jdbc.query(fetchCategoriesSql, param, new CategoryDtoMapper());
    }

    public List<UpdateBudgetConfigDTO> bugDetector (String username) {
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("username", username);
        return jdbc.query(bugDetectorSql, param, (rs, rowNum) -> new UpdateBudgetConfigDTO(
                username,
                rs.getString("category_name"),
                rs.getString("category_type"),
                false,
                rs.getBigDecimal("bug_subtotal")
        ));
    }

    public List<TransactionDTO> fetchTransactions (String username){
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", username);
        return jdbc.query(warmupQuerySql, params, new TransactionDtoMapper());
    }
}