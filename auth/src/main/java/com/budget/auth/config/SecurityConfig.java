package com.budget.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageDigest sha256 () {
        try {
            return MessageDigest.getInstance("SHA-256");
        }catch (NoSuchAlgorithmException e){
            //Email notify NOC
            throw new RuntimeException("Critical error: SHA-256 alg not found");
        }
    }

    @Bean
    public PrivateKey privateKey(@Value("${security.private-key-path}") String path) throws Exception {
        // Paths.get handles the string from your yaml
        String content = Files.readString(Paths.get(path));

        // Clean the string: Remove all "---" lines and all whitespace
        String cleanKey = content
                .replaceAll("(?m)^---.*---$", "") // Removes header/footer lines
                .replaceAll("\\s", "");           // Removes all newlines/spaces

        byte[] encoded = Base64.getDecoder().decode(cleanKey);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    @Bean
    public PublicKey publicKey(@Value("${security.public-key-path}") String path) throws Exception {
        String content = Files.readString(Paths.get(path));

        String cleanKey = content
                .replaceAll("(?m)^---.*---$", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(cleanKey);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
    }
}
