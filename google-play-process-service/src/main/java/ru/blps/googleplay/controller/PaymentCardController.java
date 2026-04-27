package ru.blps.googleplay.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.googleplay.dto.PaymentCardCreateRequest;
import ru.blps.googleplay.dto.PaymentCardResponse;
import ru.blps.googleplay.service.PaymentCardService;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping
    public PaymentCardResponse create(@Valid @RequestBody PaymentCardCreateRequest request) {
        return paymentCardService.create(request);
    }

    @GetMapping
    public List<PaymentCardResponse> list(@RequestParam Long userId) {
        return paymentCardService.listForUser(userId);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long cardId, @RequestParam Long userId) {
        paymentCardService.deleteForUser(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
