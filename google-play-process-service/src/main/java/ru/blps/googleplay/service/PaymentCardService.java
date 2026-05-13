package ru.blps.googleplay.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.blps.googleplay.dto.PaymentCardCreateRequest;
import ru.blps.googleplay.dto.PaymentCardResponse;
import ru.blps.googleplay.dto.PaymentCardUpdateRequest;
import ru.blps.googleplay.entity.PaymentCard;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.exception.BadRequestException;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.PaymentCardRepository;
import ru.blps.googleplay.tx.TxExecutor;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserAccountService userAccountService;
    private final TxExecutor tx;

    public PaymentCardService(PaymentCardRepository paymentCardRepository, UserAccountService userAccountService, TxExecutor tx) {
        this.paymentCardRepository = paymentCardRepository;
        this.userAccountService = userAccountService;
        this.tx = tx;
    }

    @PreAuthorize("@accessPolicy.canManageCards(#request.userId)")
    public PaymentCardResponse create(PaymentCardCreateRequest request) {
        return tx.required(() -> {
            UserAccount user = userAccountService.findEntityById(request.getUserId());

            String digits = request.getCardNumber().replaceAll("\\s", "");
            if (digits.length() != 16 || isLuhnValid(digits) == false) {
                throw new BadRequestException("Некорректный номер карты");
            }

            if (!request.getCvv().matches("\\d{3}")) {
                throw new BadRequestException("Некорректный CVV");
            }

            PaymentCard card = new PaymentCard();
            card.setUser(user);
            card.setHolderName(request.getHolderName());
            card.setExpiryMonth(request.getExpiryMonth());
            card.setExpiryYear(request.getExpiryYear());
            card.setMaskedNumber(maskCardNumber(digits));
            card.setCardToken(UUID.randomUUID().toString());
            card.setActive(true);

            return toResponse(paymentCardRepository.save(card));
        });
    }

    @PreAuthorize("@accessPolicy.canManageCards(#userId)")
    public List<PaymentCardResponse> listForUser(Long userId) {
        userAccountService.findEntityById(userId);
        return paymentCardRepository.findResponsesByUserIdAndActiveTrue(userId);
    }

    @PreAuthorize("@accessPolicy.canManageCards(#userId)")
    public PaymentCardResponse updateForUser(Long userId, Long cardId, PaymentCardUpdateRequest request) {
        return tx.required(() -> {
            userAccountService.findEntityById(userId);
            PaymentCard card = paymentCardRepository.findByIdAndUserIdAndActiveTrue(cardId, userId)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
            card.setHolderName(request.getHolderName());
            card.setExpiryMonth(request.getExpiryMonth());
            card.setExpiryYear(request.getExpiryYear());
            return toResponse(paymentCardRepository.save(card));
        });
    }

    @PreAuthorize("@accessPolicy.canManageCards(#userId)")
    public void deleteForUser(Long userId, Long cardId) {
        tx.required(() -> {
            userAccountService.findEntityById(userId);
            PaymentCard card = paymentCardRepository.findByIdAndUserIdAndActiveTrue(cardId, userId)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
            card.setActive(false);
            paymentCardRepository.save(card);
        });
    }

    public PaymentCard findActiveCard(Long userId, Long cardId) {
        return paymentCardRepository.findByIdAndUserIdAndActiveTrue(cardId, userId)
            .orElseThrow(() -> new NotFoundException("Активная карта не найдена"));
    }

    private PaymentCardResponse toResponse(PaymentCard card) {
        PaymentCardResponse response = new PaymentCardResponse();
        response.setId(card.getId());
        response.setUserId(card.getUser().getId());
        response.setMaskedNumber(card.getMaskedNumber());
        response.setCvv("***");
        response.setHolderName(card.getHolderName());
        response.setExpiryMonth(card.getExpiryMonth());
        response.setExpiryYear(card.getExpiryYear());
        return response;
    }

    private String maskCardNumber(String cardNumber) {
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    private boolean isLuhnValid(String digits) {
        int sum = 0;
        boolean doubleDigit = false;

        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (doubleDigit) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            doubleDigit = !doubleDigit;
        }

        return sum % 10 == 0;
    }
}
