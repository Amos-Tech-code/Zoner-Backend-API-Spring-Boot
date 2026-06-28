package com.amos_tech_code.zoner.auth.service;

public interface PasswordService {

    String hash(String rawPassword);

    boolean matches(
            String rawPassword,
            String hashedPassword
    );

}
