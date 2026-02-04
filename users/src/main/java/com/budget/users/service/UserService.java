package com.budget.users.service;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.InternalFeignDTO;
import com.budget.common.dto.RegisterDto;
import com.budget.common.dto.SecurityLogDto;
import com.budget.users.client.AuthClient;
import com.budget.users.repository.UserRepository;
import com.budget.users.util.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository repo;
    private AuthClient authClient;

    public UserService (UserRepository repo,
                        AuthClient authClient){
        this.repo = repo;
        this.authClient = authClient;
    }

    public List<String> getAllUsers() {
        return repo.getAllUsernames();
    }

    public void register(RegisterDto user) {
        repo.register(user);
    }

    public InternalFeignDTO login(String username) {
        return repo.login(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
