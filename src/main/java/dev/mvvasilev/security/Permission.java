package dev.mvvasilev.security;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    READ_SELF,
    UPDATE_SELF,
    DELETE_SELF,
    READ_OTHER_USER,
    UPDATE_OTHER_USER,
    DELETE_OTHER_USER;

    @Override
    public String getAuthority() {
        return name();
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
