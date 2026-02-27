package com.budget.users.controller;

import com.budget.common.dto.FeignResponseDTO;
import com.budget.common.dto.InternalFeignDTO;
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
    public ResponseEntity<FeignResponseDTO> register (@RequestBody RegisterDto user){
        service.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FeignResponseDTO("successfully created", "Users"));
    }

    @GetMapping("/login/{service}")
    public  ResponseEntity<InternalFeignDTO> login (@PathVariable("service") String username){
        InternalFeignDTO dto = service.login(username);
        return ResponseEntity.ok().body(dto);
    }
}
