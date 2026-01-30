package com.budget.gateway.service;

import com.budget.gateway.client.CacheUsernamesClient;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsernameCacheService {
    private final Set<String> takenUsernames;
    private final Map<String, Instant> lockedUserNames;
    private final CacheUsernamesClient cacheClient;

    public UsernameCacheService (CacheUsernamesClient cacheClient){
        this.takenUsernames = ConcurrentHashMap.newKeySet();
        this.lockedUserNames = new ConcurrentHashMap<>();
        this.cacheClient = cacheClient;
    }
    @PostConstruct
    private void init (){
        try {
            loadFromDb();
        }catch (Exception e){
            System.out.println("Exception caught: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 120000)
    protected void loadFromDb() {
        ResponseEntity<List<String>> res = cacheClient.getUsernames();
        if (res.getBody() != null) {
            takenUsernames.clear();
            takenUsernames.addAll(res.getBody());
        }
    }

    protected boolean tryReserve (String username){
        lockedUserNames.entrySet().removeIf(e -> e.getValue().isBefore(Instant.now()));
        if (isTaken(username)){
            return false;
        }
        return lockedUserNames.putIfAbsent(username, Instant.now().plusSeconds(120)) == null;
    }

    protected void updateCache (String username){
        takenUsernames.add(username);
    }

    private boolean isTaken (String username) {
        return takenUsernames.contains(username);
    }

    public void confirmRegistration (String username){
        takenUsernames.add(username);
        lockedUserNames.remove(username);
    }
}
