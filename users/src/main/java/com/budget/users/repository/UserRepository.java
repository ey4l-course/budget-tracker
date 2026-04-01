package com.budget.users.repository;

import com.budget.common.dto.FeignLoginDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.users.model.UpdateBudgetConfigDTO;
import com.budget.users.repository.mapper.LoginMapper;
import com.budget.users.util.UserNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final String USERS = "users_table";
    private final String ADDRESSES = "addresses_table";
    public UserRepository (JdbcTemplate jdbc,
                           NamedParameterJdbcTemplate namedJdbc){
        this.jdbc = jdbc;
        this.namedJdbc = namedJdbc;
    }

    public List<String> getAllUsernames() {
        String sql = String.format("SELECT username FROM %s", USERS);
        return jdbc.queryForList(sql, String.class);
    }

    public void register(RegisterDto user) {
        try {
            Integer addressId = insertAddress(user.getAddress());
            String sql = String.format("INSERT INTO %s (username, password, given_name, surname, mobile, email, address_id)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)", USERS);
            jdbc.update(sql, user.getUsername(), user.getPassword(), user.getGivenName(), user.getSurname(), user.getMobile(), user.getEmail(), addressId);
        }catch (DuplicateKeyException e){
            throw new DuplicateKeyException(user.getUsername());
        }

    }

    private Integer insertAddress (RegisterDto.Address address){
        KeyHolder key = new GeneratedKeyHolder();
        String sql = String.format("INSERT INTO %s (state , city, house, apartment, zipcode)" +
                " VALUES (?, ?, ?, ?, ?)", ADDRESSES);
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, address.getState());
            ps.setString(2, address.getCity());
            ps.setInt(3, address.getHouse());
            ps.setInt(4, address.getApartment());
            ps.setString(5, address.getZipcode());
            return ps;
        }, key);
        return key.getKeyAs(BigInteger.class).intValue();
    }

    public FeignLoginDTO login(String username) {
        try {
            String sql = String.format("SELECT password, is_admin, is_activated, given_name, surname FROM %s WHERE username = ?", USERS);
            return jdbc.queryForObject(sql, new LoginMapper(), username);
        }catch (EmptyResultDataAccessException e){
            throw new UserNotFoundException(username);
        }
    }

    public String whoAmI(String username) {
        String sql = String.format("SELECT CONCAT (COALESCE(given_name, ''), ' ',COALESCE(surname, '')) FROM %s WHERE username = ?", USERS);
        return jdbc.queryForObject(sql, String.class, username);
    }

    public int[] updateBudgetConfig(List<UpdateBudgetConfigDTO> data) {
            String sql = "INSERT INTO budget_configs (username, category_name, category_type, is_manual, amount_limit) " +
                    "values (:username, :categoryName, :categoryType, true, :amount) " +
                    "ON CONFLICT (username, category_name) " +
                    "DO UPDATE SET amount_limit = EXCLUDED.amount_limit";
            SqlParameterSource[] batch = data.stream()
                    .map(BeanPropertySqlParameterSource::new)
                    .toArray(SqlParameterSource[]::new);
            return namedJdbc.batchUpdate(sql, batch);
    }
}