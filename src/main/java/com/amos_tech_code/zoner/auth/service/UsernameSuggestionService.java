package com.amos_tech_code.zoner.auth.service;

import java.util.List;

public interface UsernameSuggestionService {

    List<String> generateSuggestions(
            String desiredUsername
    );

}