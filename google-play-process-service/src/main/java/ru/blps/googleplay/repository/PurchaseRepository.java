package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.blps.googleplay.dto.PurchaseResponse;
import ru.blps.googleplay.entity.Purchase;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("""
        select new ru.blps.googleplay.dto.PurchaseResponse(
            p.id,
            p.user.id,
            p.app.id,
            p.amount,
            p.status,
            p.createdAt
        )
        from Purchase p
        where p.user.id = :userId
        order by p.createdAt desc
        """)
    List<PurchaseResponse> findResponsesByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
