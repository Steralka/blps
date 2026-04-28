package ru.blps.googleplay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.blps.googleplay.dto.PaymentCardResponse;
import ru.blps.googleplay.dto.PaymentCardUpdateRequest;
import ru.blps.googleplay.entity.PaymentCard;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.PaymentCardRepository;
import ru.blps.googleplay.repository.UserAccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    private PaymentCardService paymentCardService;

    @BeforeEach
    void setUp() {
        UserAccountService userAccountService = new UserAccountService(userAccountRepository);
        paymentCardService = new PaymentCardService(paymentCardRepository, userAccountService);
    }

    @Test
    void listForUserUsesProjectedResponses() {
        UserAccount user = user(1L);
        PaymentCardResponse card = new PaymentCardResponse(2L, 1L, "**** **** **** 1111", "Student", 12, 2030);

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findResponsesByUserIdAndActiveTrue(1L)).thenReturn(List.of(card));

        List<PaymentCardResponse> response = paymentCardService.listForUser(1L);

        assertEquals(1, response.size());
        assertEquals(2L, response.get(0).getId());
        assertEquals(1L, response.get(0).getUserId());
        assertEquals("**** **** **** 1111", response.get(0).getMaskedNumber());
        verify(paymentCardRepository).findResponsesByUserIdAndActiveTrue(1L);
    }

    @Test
    void updateChangesCardHolderAndExpiry() {
        UserAccount user = user(1L);
        PaymentCard card = card(2L, user);
        PaymentCardUpdateRequest request = new PaymentCardUpdateRequest();
        request.setHolderName("Updated Student");
        request.setExpiryMonth(11);
        request.setExpiryYear(2031);

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByIdAndUserIdAndActiveTrue(2L, 1L)).thenReturn(Optional.of(card));
        when(paymentCardRepository.save(card)).thenReturn(card);

        PaymentCardResponse response = paymentCardService.updateForUser(1L, 2L, request);

        assertEquals("Updated Student", response.getHolderName());
        assertEquals(11, response.getExpiryMonth());
        assertEquals(2031, response.getExpiryYear());
        assertEquals("**** **** **** 1111", response.getMaskedNumber());
        verify(paymentCardRepository).save(card);
    }

    @Test
    void deleteMarksCardInactive() {
        UserAccount user = user(1L);
        PaymentCard card = card(2L, user);

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findByIdAndUserIdAndActiveTrue(2L, 1L)).thenReturn(Optional.of(card));

        paymentCardService.deleteForUser(1L, 2L);

        assertFalse(card.isActive());
        verify(paymentCardRepository).save(card);
    }

    @Test
    void deletedUserCannotUpdateCard() {
        PaymentCardUpdateRequest request = new PaymentCardUpdateRequest();
        request.setHolderName("Updated Student");
        request.setExpiryMonth(11);
        request.setExpiryYear(2031);

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentCardService.updateForUser(1L, 2L, request));
        verify(paymentCardRepository, never()).findByIdAndUserIdAndActiveTrue(2L, 1L);
    }

    private UserAccount user(Long id) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setEmail("student@example.com");
        user.setDisplayName("Student");
        user.setBalance(new BigDecimal("100.00"));
        user.setActive(true);
        return user;
    }

    private PaymentCard card(Long id, UserAccount user) {
        PaymentCard card = new PaymentCard();
        card.setId(id);
        card.setUser(user);
        card.setMaskedNumber("**** **** **** 1111");
        card.setHolderName("Student");
        card.setExpiryMonth(12);
        card.setExpiryYear(2030);
        card.setCardToken("token");
        card.setActive(true);
        return card;
    }
}
