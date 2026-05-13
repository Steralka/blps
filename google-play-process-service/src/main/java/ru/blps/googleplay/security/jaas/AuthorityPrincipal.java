package ru.blps.googleplay.security.jaas;

import java.security.Principal;
import java.util.Objects;

public final class AuthorityPrincipal implements Principal {
    private final String authority;

    public AuthorityPrincipal(String authority) {
        this.authority = Objects.requireNonNull(authority);
    }

    @Override
    public String getName() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorityPrincipal that)) return false;
        return authority.equals(that.authority);
    }

    @Override
    public int hashCode() {
        return authority.hashCode();
    }

    @Override
    public String toString() {
        return "AuthorityPrincipal[" + authority + "]";
    }
}

