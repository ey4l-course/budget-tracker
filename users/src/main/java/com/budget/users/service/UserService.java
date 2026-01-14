package com.budget.users.service;

import com.budget.common.dto.RegisterDto;
import com.budget.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository repo;

    public UserService (UserRepository repo){
        this.repo = repo;
    }

    public List<String> getAllUsers() {
        return repo.getAllUsernames();
    }

    public void register(RegisterDto user) {
        repo.register(user);
    }
}
