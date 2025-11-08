package com.budget.users.controller;

import com.budget.common.dto.RegisterDto;
import com.budget.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicUserController {
    private final UserService service;

    public PublicUserController (UserService service){
        this.service = service;
    }

    @GetMapping ("/update-cache")
    public ResponseEntity<List<String>> getAllUsernames (){
        List<String> res = service.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register (@RequestBody RegisterDto user){
        service.register(user);
        return new  ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
