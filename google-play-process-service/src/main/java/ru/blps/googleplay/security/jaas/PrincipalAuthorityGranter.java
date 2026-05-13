package ru.blps.googleplay.security.jaas;

import org.springframework.security.authentication.jaas.AuthorityGranter;

import java.security.Principal;
import java.util.Set;

public class PrincipalAuthorityGranter implements AuthorityGranter {
    @Override
    public Set<String> grant(Principal principal) {
        if (principal instanceof AuthorityPrincipal) {
            return Set.of(principal.getName());
        }
        return Set.of();
    }
}

