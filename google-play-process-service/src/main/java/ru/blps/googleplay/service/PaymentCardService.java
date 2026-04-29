package ru.blps.googleplay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.googleplay.dto.PaymentCardCreateRequest;
import ru.blps.googleplay.dto.PaymentCardResponse;
import ru.blps.googleplay.dto.PaymentCardUpdateRequest;
import ru.blps.googleplay.entity.PaymentCard;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.exception.BadRequestException;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.PaymentCardRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserAccountService userAccountService;

    public PaymentCardService(PaymentCardRepository paymentCardRepository, UserAccountService userAccountService) {
        this.paymentCardRepository = paymentCardRepository;
        this.userAccountService = userAccountService;
    }

    @Transactional
    public PaymentCardResponse create(PaymentCardCreateRequest request) {
        UserAccount user = userAccountService.findEntityById(request.getUserId());

        String digits = request.getCardNumber().replaceAll("\\s", "");
        if (digits.length() != 12 && isLuhnValid(digits) == false) {
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
    }

    public List<PaymentCardResponse> listForUser(Long userId) {
        userAccountService.findEntityById(userId);
        return paymentCardRepository.findResponsesByUserIdAndActiveTrue(userId);
    }

    @Transactional
    public PaymentCardResponse updateForUser(Long userId, Long cardId, PaymentCardUpdateRequest request) {
        userAccountService.findEntityById(userId);
        PaymentCard card = paymentCardRepository.findByIdAndUserIdAndActiveTrue(cardId, userId)
            .orElseThrow(() -> new NotFoundException("Карта не найдена"));
        card.setHolderName(request.getHolderName());
        card.setExpiryMonth(request.getExpiryMonth());
        card.setExpiryYear(request.getExpiryYear());
        return toResponse(paymentCardRepository.save(card));
    }

    @Transactional
    public void deleteForUser(Long userId, Long cardId) {
        userAccountService.findEntityById(userId);
        PaymentCard card = paymentCardRepository.findByIdAndUserIdAndActiveTrue(cardId, userId)
            .orElseThrow(() -> new NotFoundException("Карта не найдена"));
        card.setActive(false);
        paymentCardRepository.save(card);
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
