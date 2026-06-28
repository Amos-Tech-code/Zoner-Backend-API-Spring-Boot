package com.amos_tech_code.zoner.auth.service.impl;

import org.springframework.stereotype.Service;
import com.amos_tech_code.zoner.auth.service.UsernameSuggestionService;

import java.util.List;

@Service
public class UsernameSuggestionServiceImpl implements UsernameSuggestionService {
    @Override
    public List<String> generateSuggestions(String desiredUsername) {
        return List.of();
    }
}
