package com.amos_tech_code.zoner.common.exception;

import java.time.Instant;

public record ApiError(

        Instant timestamp,

        int status,

        String error,

        String message,

        String path

) {}