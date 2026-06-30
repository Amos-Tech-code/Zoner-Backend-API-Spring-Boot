package com.amos_tech_code.zoner.users.service.impl;

import com.amos_tech_code.zoner.users.repository.UserRepository;
import com.amos_tech_code.zoner.users.service.UsernameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UsernameServiceImpl implements UsernameService {

    private static final int MAX_SUGGESTIONS = 5;
    private static final int MAX_GENERATION_ATTEMPTS = 100;

    private final UserRepository userRepository;

    @Override
    public boolean isAvailable(String username) {

        String normalized = normalize(username);

        return !userRepository.existsByUsernameAndDeletedAtIsNull(normalized);
    }

    @Override
    public List<String> generateSuggestions(String username) {

        String normalized = normalize(username);

        Set<String> suggestions = new LinkedHashSet<>();

        int attempts = 0;

        while (suggestions.size() < MAX_SUGGESTIONS
                && attempts < MAX_GENERATION_ATTEMPTS) {

            attempts++;

            String candidate =
                    normalized +
                            ThreadLocalRandom.current()
                                    .nextInt(1000, 10000);

            if (!userRepository.existsByUsernameAndDeletedAtIsNull(candidate)) {
                suggestions.add(candidate);
            }
        }

        return new ArrayList<>(suggestions);
    }

    private String normalize(String username) {
        return username.trim().toLowerCase();
    }

}