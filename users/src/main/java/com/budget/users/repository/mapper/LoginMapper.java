package com.budget.users.repository.mapper;

import com.budget.common.dto.InternalFeignDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginMapper implements RowMapper<InternalFeignDTO> {
    @Override
    public InternalFeignDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new InternalFeignDTO(
                rs.getString("password"),
                rs.getBoolean("is_admin"),
                rs.getString("given_name"),
                rs.getString("surname")
        );
    }
}
