package com.chefswar.models;

import com.chefswar.enums.UserRole;

public class SystemAdmin extends User {
    public SystemAdmin(String email, String name, String userId) {
        super(UserRole.SYSTEM_ADMIN,email, name, userId);
    }
}