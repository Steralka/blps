package ru.blps.googleplay.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RolePrivileges {
    private RolePrivileges() {
    }

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private static final Map<String, Set<String>> ROLE_TO_PRIVILEGES = Map.of(
        ROLE_ADMIN, Set.of(
            Privilege.CATALOG_READ,
            Privilege.CATALOG_WRITE,
            Privilege.ACCOUNT_READ_SELF,
            Privilege.ACCOUNT_WRITE_SELF,
            Privilege.ACCOUNT_ADMIN,
            Privilege.CARD_MANAGE_SELF,
            Privilege.CARD_ADMIN,
            Privilege.INSTALL_SELF,
            Privilege.INSTALL_ADMIN
        ),
        ROLE_USER, Set.of(
            Privilege.CATALOG_READ,
            Privilege.ACCOUNT_READ_SELF,
            Privilege.ACCOUNT_WRITE_SELF,
            Privilege.CARD_MANAGE_SELF,
            Privilege.INSTALL_SELF
        )
    );

    public static Set<String> privilegesForRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
            .map(ROLE_TO_PRIVILEGES::get)
            .filter(p -> p != null)
            .flatMap(Set::stream)
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}

