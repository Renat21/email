package com.mail.demo.enumer;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enum Role- перечисление ролей пользователя
 * **/
public enum Role implements GrantedAuthority {

    /**
     * роли пользователя
     * **/
    ROLE_USER, ROLE_ADMIN;

    /**
     * метод из GrantedAuthority
     * **/
    @Override
    public String getAuthority() {
        return name();
    }

}