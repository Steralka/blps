package ru.blps.googleplay.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.googleplay.dto.TopUpRequest;
import ru.blps.googleplay.dto.UserAccountCreateRequest;
import ru.blps.googleplay.dto.UserAccountResponse;
import ru.blps.googleplay.service.UserAccountService;

@RestController
@RequestMapping("/api/payment-accounts/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    public UserAccountResponse create(@Valid @RequestBody UserAccountCreateRequest request) {
        return userAccountService.create(request);
    }

    @GetMapping("/{userId}")
    public UserAccountResponse getById(@PathVariable Long userId) {
        return userAccountService.getById(userId);
    }

    @PostMapping("/{userId}/top-up")
    public UserAccountResponse topUp(@PathVariable Long userId, @Valid @RequestBody TopUpRequest request) {
        return userAccountService.topUp(userId, request);
    }
}
