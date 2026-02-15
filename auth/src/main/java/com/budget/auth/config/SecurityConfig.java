package com.budget.auth.config;

import jakarta.annotation.PostConstruct;
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
    @Value("${security.private-key-path}")
    private String privateKeyPath;
    @Value("${security.public-key-path}")
    private String publicKeyPath;


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
            startUpError(e);
            return null;
        }
    }

    @Bean
    public PrivateKey privateKey() {
        try {
            String content = Files.readString(Paths.get(privateKeyPath));
            String cleanKey = content
                    .replaceAll("(?m)^---.*---$", "") // Removes header/footer lines
                    .replaceAll("\\s", "");           // Removes all newlines/spaces

            byte[] encoded = Base64.getDecoder().decode(cleanKey);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
        }catch (Exception e){
            startUpError(e);
            return null;
        }
    }

    @Bean
    public PublicKey publicKey(){
        try {
            String content = Files.readString(Paths.get(publicKeyPath));

            String cleanKey = content
                    .replaceAll("(?m)^---.*---$", "")
                    .replaceAll("\\s", "");

            byte[] encoded = Base64.getDecoder().decode(cleanKey);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception e){
            startUpError(e);
            return null;
        }
    }

    @PostConstruct
    public void validateKeysOnBoot(){
        try {
            String testString = "Startup test" + System.currentTimeMillis();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey());
            sig.update(testString.getBytes());
            byte[] signature = sig.sign();

            sig.initVerify(publicKey());
            sig.update(testString.getBytes());
            if (!sig.verify(signature))
                throw new RuntimeException("Keys do not match");
        }catch (Exception e){
            startUpError(e);
        }

    }

    private void startUpError (Exception e){
        //Trigger SMS and mail brokers to alert NOC
        System.out.println("FATAL boot error:\n" + e.getMessage());
        throw new RuntimeException(e);
    }
}
