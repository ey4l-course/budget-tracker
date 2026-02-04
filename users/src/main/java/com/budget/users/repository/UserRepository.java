package com.budget.users.repository;

import com.budget.common.dto.InternalFeignDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.users.repository.mapper.LoginMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final String USERS = "users_table";
    private final String ADDRESSES = "addresses_table";
    public UserRepository (JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public List<String> getAllUsernames() {
        String sql = String.format("SELECT username FROM %s", USERS);
        return jdbc.queryForList(sql, String.class);
    }

    public void register(RegisterDto user) {
        Integer addressId = insertAddress(user.getAddress());
        String sql = String.format("INSERT INTO %s (username, password, given_name, surname, mobile, email, address_id)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)", USERS);
        jdbc.update(sql, user.getUsername(), user.getPassword(), user.getGivenName(), user.getSurname(), user.getMobile(), user.getEmail(), addressId);
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
        return key.getKeyAs(Integer.class);
    }

    public Optional<InternalFeignDTO> login(String username) {
            String sql = String.format("SELECT password, is_admin FROM %s WHERE username = ?", USERS);
            return jdbc.queryForObject(sql, new LoginMapper(), username);
    }
}