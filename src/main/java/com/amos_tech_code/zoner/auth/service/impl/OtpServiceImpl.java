package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.service.OtpService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpServiceImpl implements OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generateOtp() {

        int otp = 100000 + RANDOM.nextInt(900000);

        return String.valueOf(otp);
    }

}
