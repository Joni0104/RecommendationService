package com.starbank.recommendationService.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.starbank.recommendationService.repository.UserDataRepository;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final UserDataRepository userDataRepository;

    public CacheService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    public void clearAllCaches() {

    }
}