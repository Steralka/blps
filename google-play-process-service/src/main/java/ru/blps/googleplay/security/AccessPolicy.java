package ru.blps.googleplay.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.repository.UserAccountRepository;

import java.util.Objects;

@Component("accessPolicy")
public class AccessPolicy {

    private final UserAccountRepository userAccountRepository;

    public AccessPolicy(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public boolean canCreateAccount(String email) {
        Authentication auth = authentication();
        if (has(auth, Privilege.ACCOUNT_ADMIN)) {
            return true;
        }
        return has(auth, Privilege.ACCOUNT_WRITE_SELF) && Objects.equals(email, auth.getName());
    }

    public boolean canReadAccount(Long userId) {
        Authentication auth = authentication();
        if (has(auth, Privilege.ACCOUNT_ADMIN)) {
            return true;
        }
        if (!has(auth, Privilege.ACCOUNT_READ_SELF)) {
            return false;
        }
        return isOwnerByEmail(auth, userId);
    }

    public boolean canWriteAccount(Long userId) {
        Authentication auth = authentication();
        if (has(auth, Privilege.ACCOUNT_ADMIN)) {
            return true;
        }
        if (!has(auth, Privilege.ACCOUNT_WRITE_SELF)) {
            return false;
        }
        return isOwnerByEmail(auth, userId);
    }

    public boolean canManageCards(Long userId) {
        Authentication auth = authentication();
        if (has(auth, Privilege.CARD_ADMIN)) {
            return true;
        }
        if (!has(auth, Privilege.CARD_MANAGE_SELF)) {
            return false;
        }
        return isOwnerByEmail(auth, userId);
    }

    public boolean canInstallFor(Long userId) {
        Authentication auth = authentication();
        if (has(auth, Privilege.INSTALL_ADMIN)) {
            return true;
        }
        if (!has(auth, Privilege.INSTALL_SELF)) {
            return false;
        }
        return isOwnerByEmail(auth, userId);
    }

    private boolean isOwnerByEmail(Authentication auth, Long userId) {
        if (userId == null) {
            return false;
        }
        return userAccountRepository.findById(userId)
            .filter(UserAccount::isActive)
            .map(UserAccount::getEmail)
            .map(email -> Objects.equals(email, auth.getName()))
            .orElse(false);
    }

    private static Authentication authentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("Authentication is required");
        }
        return auth;
    }

    private static boolean has(Authentication auth, String authority) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(authority));
    }
}

