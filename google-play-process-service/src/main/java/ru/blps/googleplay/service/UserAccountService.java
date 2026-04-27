package ru.blps.googleplay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.googleplay.dto.TopUpRequest;
import ru.blps.googleplay.dto.UserAccountCreateRequest;
import ru.blps.googleplay.dto.UserAccountResponse;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.UserAccountRepository;

import java.util.Objects;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public UserAccountResponse create(UserAccountCreateRequest request) {
        UserAccount account = new UserAccount();
        account.setEmail(request.getEmail());
        account.setDisplayName(request.getDisplayName());
        account.setBalance(request.getInitialBalance());

        return toResponse(userAccountRepository.save(account));
    }

    public UserAccountResponse getById(Long userId) {
        return toResponse(findEntityById(userId));
    }

    @Transactional
    public UserAccountResponse topUp(Long userId, TopUpRequest request) {
        UserAccount account = findEntityById(userId);
        account.setBalance(account.getBalance().add(request.getAmount()));
        return toResponse(userAccountRepository.save(account));
    }

    public UserAccount findEntityById(Long userId) {
        return userAccountRepository.findById(Objects.requireNonNull(userId))
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
