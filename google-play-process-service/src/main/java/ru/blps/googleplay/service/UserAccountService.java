package ru.blps.googleplay.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.blps.googleplay.dto.TopUpRequest;
import ru.blps.googleplay.dto.UserAccountCreateRequest;
import ru.blps.googleplay.dto.UserAccountResponse;
import ru.blps.googleplay.dto.UserAccountUpdateRequest;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.UserAccountRepository;
import ru.blps.googleplay.tx.TxExecutor;

import java.util.Objects;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final TxExecutor tx;

    public UserAccountService(UserAccountRepository userAccountRepository, TxExecutor tx) {
        this.userAccountRepository = userAccountRepository;
        this.tx = tx;
    }

    @PreAuthorize("@accessPolicy.canCreateAccount(#request.email)")
    public UserAccountResponse create(UserAccountCreateRequest request) {
        return tx.required(() -> {
            UserAccount account = new UserAccount();
            account.setEmail(request.getEmail());
            account.setDisplayName(request.getDisplayName());
            account.setBalance(request.getInitialBalance());
            account.setActive(true);

            return toResponse(userAccountRepository.save(account));
        });
    }

    @PreAuthorize("@accessPolicy.canReadAccount(#userId)")
    public UserAccountResponse getById(Long userId) {
        return toResponse(findEntityById(userId));
    }

    @PreAuthorize("@accessPolicy.canWriteAccount(#userId)")
    public UserAccountResponse topUp(Long userId, TopUpRequest request) {
        return tx.required(() -> {
            UserAccount account = findEntityById(userId);
            account.setBalance(account.getBalance().add(request.getAmount()));
            return toResponse(userAccountRepository.save(account));
        });
    }

    @PreAuthorize("@accessPolicy.canWriteAccount(#userId)")
    public UserAccountResponse update(Long userId, UserAccountUpdateRequest request) {
        return tx.required(() -> {
            UserAccount account = findEntityById(userId);
            account.setEmail(request.getEmail());
            account.setDisplayName(request.getDisplayName());
            return toResponse(userAccountRepository.save(account));
        });
    }

    @PreAuthorize("@accessPolicy.canWriteAccount(#userId)")
    public void delete(Long userId) {
        tx.required(() -> {
            UserAccount account = findEntityById(userId);
            account.setActive(false);
            userAccountRepository.save(account);
        });
    }

    public UserAccount findEntityById(Long userId) {
        return userAccountRepository.findByIdAndActiveTrue(Objects.requireNonNull(userId))
            .orElseThrow(() -> new NotFoundException("Платежный аккаунт пользователя не найден"));
    }

    private UserAccountResponse toResponse(UserAccount account) {
        UserAccountResponse response = new UserAccountResponse();
        response.setId(account.getId());
        response.setEmail(account.getEmail());
        response.setDisplayName(account.getDisplayName());
        response.setBalance(account.getBalance());
        return response;
    }
}
