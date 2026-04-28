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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.googleplay.dto.PaymentCardCreateRequest;
import ru.blps.googleplay.dto.PaymentCardResponse;
import ru.blps.googleplay.dto.PaymentCardUpdateRequest;
import ru.blps.googleplay.service.PaymentCardService;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Управление платежными картами")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @Operation(summary = "Добавить платежную карту")
    @PostMapping
    public PaymentCardResponse create(@Valid @RequestBody PaymentCardCreateRequest request) {
        return paymentCardService.create(request);
    }

    @Operation(summary = "Получить активные карты пользователя")
    @GetMapping
    public List<PaymentCardResponse> list(@RequestParam Long userId) {
        return paymentCardService.listForUser(userId);
    }

    @Operation(summary = "Обновить данные платежной карты")
    @PutMapping("/{cardId}")
    public PaymentCardResponse update(@PathVariable Long cardId,
                                      @RequestParam Long userId,
                                      @Valid @RequestBody PaymentCardUpdateRequest request) {
        return paymentCardService.updateForUser(userId, cardId, request);
    }

    @Operation(summary = "Деактивировать платежную карту")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long cardId, @RequestParam Long userId) {
        paymentCardService.deleteForUser(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
