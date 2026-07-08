package com.amos_tech_code.zoner.users.enums;

public enum AccountStatus {

    PENDING, // Registration not finished

    ACTIVE, // Normal account

    SUSPENDED, // Temporary admin action

    DEACTIVATED, // User choose to deactivate

    BANNED, // Permanent admin action

    DELETED // User choose to delete

}