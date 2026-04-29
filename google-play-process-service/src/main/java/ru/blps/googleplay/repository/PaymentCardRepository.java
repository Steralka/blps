package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.blps.googleplay.dto.PaymentCardResponse;
import ru.blps.googleplay.entity.PaymentCard;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    @Query("""
        select new ru.blps.googleplay.dto.PaymentCardResponse(
            c.id,
            c.user.id,
            c.maskedNumber,
            '***',
            c.holderName,
            c.expiryMonth,
            c.expiryYear
        )
        from PaymentCard c
        where c.user.id = :userId and c.active = true
        """)
    List<PaymentCardResponse> findResponsesByUserIdAndActiveTrue(@Param("userId") Long userId);

    @EntityGraph(attributePaths = "user")
    Optional<PaymentCard> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
}
