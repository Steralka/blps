package ru.blps.googleplay.security.jaas;

import java.util.List;

public record XmlUserRecord(
    String username,
    String password,
    List<String> roles,
    List<String> privileges
) {
}

