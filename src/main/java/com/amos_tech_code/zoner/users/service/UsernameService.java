package com.amos_tech_code.zoner.users.service;

import java.util.List;

public interface UsernameService {

    boolean isAvailable(String username);

    List<String> generateSuggestions(String username);

}
