package com.budget.users.service;

import com.budget.common.dto.FeignLoginDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService (UserRepository repo){
        this.repo = repo;
    }

    public List<String> getAllUsers() {
        return repo.getAllUsernames();
    }

    public void register(RegisterDto user) {
        repo.register(user);
    }

    public FeignLoginDTO login(String username) {
        return repo.login(username);
    }

    public void activateAccount (String username){
        repo.activateUser(username);
    }

    public void deactivateAccount (String username) {
        repo.deactivateUser(username);
    }
}
