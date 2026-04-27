package ru.blps.googleplay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blps.googleplay.entity.AppItem;

import java.math.BigDecimal;
import java.util.List;

public interface AppItemRepository extends JpaRepository<AppItem, Long> {

    List<AppItem> findByActiveTrueAndTitleContainingIgnoreCaseAndPriceBetween(String title, BigDecimal minPrice, BigDecimal maxPrice);
}
