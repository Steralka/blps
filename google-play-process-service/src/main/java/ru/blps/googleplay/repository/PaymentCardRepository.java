package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blps.googleplay.entity.PaymentCard;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    List<PaymentCard> findByUserIdAndActiveTrue(Long userId);

    Optional<PaymentCard> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
}
