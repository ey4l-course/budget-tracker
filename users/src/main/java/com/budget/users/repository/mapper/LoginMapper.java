package com.budget.users.repository.mapper;

import com.budget.common.dto.FeignLoginDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginMapper implements RowMapper<FeignLoginDTO> {
    @Override
    public FeignLoginDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FeignLoginDTO(
                rs.getString("password"),
                rs.getBoolean("is_admin"),
                rs.getString("given_name"),
                rs.getString("surname")
        );
    }
}
