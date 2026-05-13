package ru.blps.googleplay.security.jaas;

import java.security.Principal;
import java.util.Objects;

public final class UserPrincipal implements Principal {
    private final String name;

    public UserPrincipal(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "UserPrincipal[" + name + "]";
    }
}

