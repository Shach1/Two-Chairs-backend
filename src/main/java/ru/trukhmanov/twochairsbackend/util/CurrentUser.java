package ru.trukhmanov.twochairsbackend.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser{
    private CurrentUser(){}

    public static long id(){
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if(a == null || a.getPrincipal() == null) throw new IllegalStateException("Unauthenticated");
        return Long.parseLong(a.getPrincipal().toString());
    }
}