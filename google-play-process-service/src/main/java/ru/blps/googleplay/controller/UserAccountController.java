package ru.blps.googleplay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.googleplay.dto.TopUpRequest;
import ru.blps.googleplay.dto.UserAccountCreateRequest;
import ru.blps.googleplay.dto.UserAccountResponse;
import ru.blps.googleplay.dto.UserAccountUpdateRequest;
import ru.blps.googleplay.service.UserAccountService;

@RestController
@RequestMapping("/api/payment-accounts/users")
@Tag(name = "Payment Accounts", description = "Управление платежными аккаунтами пользователей")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Operation(summary = "Создать платежный аккаунт")
    @PostMapping
    public UserAccountResponse create(@Valid @RequestBody UserAccountCreateRequest request) {
        return userAccountService.create(request);
    }

    @Operation(summary = "Получить платежный аккаунт")
    @GetMapping("/{userId}")
    public UserAccountResponse getById(@PathVariable Long userId) {
        return userAccountService.getById(userId);
    }

    @Operation(summary = "Обновить платежный аккаунт")
    @PutMapping("/{userId}")
    public UserAccountResponse update(@PathVariable Long userId, @Valid @RequestBody UserAccountUpdateRequest request) {
        return userAccountService.update(userId, request);
    }

    @Operation(summary = "Деактивировать платежный аккаунт")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userAccountService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Пополнить баланс платежного аккаунта")
    @PostMapping("/{userId}/top-up")
    public UserAccountResponse topUp(@PathVariable Long userId, @Valid @RequestBody TopUpRequest request) {
        return userAccountService.topUp(userId, request);
    }
}
