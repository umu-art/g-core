package ru.kazenin.gcore.auth;

import java.util.UUID;

public class AuthHolder {
    public static final ThreadLocal<UUID> token = new ThreadLocal<>();
}
