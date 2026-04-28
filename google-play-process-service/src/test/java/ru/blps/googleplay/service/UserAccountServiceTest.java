package ru.blps.googleplay.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.blps.googleplay.dto.TopUpRequest;
import ru.blps.googleplay.dto.UserAccountResponse;
import ru.blps.googleplay.dto.UserAccountUpdateRequest;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.UserAccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountService userAccountService;

    @Test
    void updateChangesEmailAndDisplayNameOnly() {
        UserAccount account = account(1L);
        UserAccountUpdateRequest request = new UserAccountUpdateRequest();
        request.setEmail("updated@example.com");
        request.setDisplayName("Updated Student");

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(account));
        when(userAccountRepository.save(account)).thenReturn(account);

        UserAccountResponse response = userAccountService.update(1L, request);

        assertEquals("updated@example.com", response.getEmail());
        assertEquals("Updated Student", response.getDisplayName());
        assertEquals(new BigDecimal("100.00"), response.getBalance());
        verify(userAccountRepository).save(account);
    }

    @Test
    void deleteMarksAccountInactive() {
        UserAccount account = account(1L);
        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(account));

        userAccountService.delete(1L);

        assertFalse(account.isActive());
        verify(userAccountRepository).save(account);
    }

    @Test
    void topUpDeletedAccountFails() {
        TopUpRequest request = new TopUpRequest();
        request.setAmount(new BigDecimal("10.00"));
        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAccountService.topUp(1L, request));
    }

    private UserAccount account(Long id) {
        UserAccount account = new UserAccount();
        account.setId(id);
        account.setEmail("student@example.com");
        account.setDisplayName("Student");
        account.setBalance(new BigDecimal("100.00"));
        account.setActive(true);
        return account;
    }
}
