package com.amos_tech_code.zoner.auth.dto.internal;

public record GooglePrincipal(

        String subject,

        String email,

        String name,

        String picture

) {}
