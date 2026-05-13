package ru.blps.googleplay.service;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.blps.googleplay.dto.AppItemRequest;
import ru.blps.googleplay.dto.AppItemResponse;
import ru.blps.googleplay.entity.AppItem;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.AppItemRepository;
import ru.blps.googleplay.security.Privilege;
import ru.blps.googleplay.tx.TxExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class CatalogService {

    private final AppItemRepository appItemRepository;
    private final TxExecutor tx;

    public CatalogService(AppItemRepository appItemRepository, TxExecutor tx) {
        this.appItemRepository = appItemRepository;
        this.tx = tx;
    }

    @PreAuthorize("hasAuthority(T(ru.blps.googleplay.security.Privilege).CATALOG_READ)")
    public List<AppItemResponse> search(String query, BigDecimal minPrice, BigDecimal maxPrice) {
        String safeQuery = query == null ? "" : query;
        BigDecimal safeMin = minPrice == null ? BigDecimal.ZERO : minPrice;
        BigDecimal safeMax = maxPrice == null ? new BigDecimal("999999.99") : maxPrice;

        return appItemRepository.findByActiveTrueAndTitleContainingIgnoreCaseAndPriceBetween(safeQuery, safeMin, safeMax)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @PreAuthorize("hasAuthority(T(ru.blps.googleplay.security.Privilege).CATALOG_READ)")
    public AppItemResponse getById(Long appId) {
        return toResponse(findEntityById(appId));
    }

    @PreAuthorize("hasAuthority(T(ru.blps.googleplay.security.Privilege).CATALOG_WRITE)")
    public AppItemResponse create(AppItemRequest request) {
        return tx.required(() -> {
            AppItem app = new AppItem();
            app.setPackageName(request.getPackageName());
            app.setTitle(request.getTitle());
            app.setDescription(request.getDescription());
            app.setPrice(request.getPrice());
            app.setActive(true);

            return toResponse(appItemRepository.save(app));
        });
    }

    @PreAuthorize("hasAuthority(T(ru.blps.googleplay.security.Privilege).CATALOG_WRITE)")
    public AppItemResponse update(Long appId, AppItemRequest request) {
        return tx.required(() -> {
            AppItem app = findEntityById(appId);
            app.setPackageName(request.getPackageName());
            app.setTitle(request.getTitle());
            app.setDescription(request.getDescription());
            app.setPrice(request.getPrice());
            return toResponse(appItemRepository.save(app));
        });
    }

    @PreAuthorize("hasAuthority(T(ru.blps.googleplay.security.Privilege).CATALOG_WRITE)")
    public void delete(Long appId) {
        tx.required(() -> {
            AppItem app = findEntityById(appId);
            app.setActive(false);
            appItemRepository.save(app);
        });
    }

    public AppItem findEntityById(Long appId) {
        return appItemRepository.findByIdAndActiveTrue(Objects.requireNonNull(appId))
            .orElseThrow(() -> new NotFoundException("Приложение не найдено"));
    }

    private AppItemResponse toResponse(AppItem app) {
        AppItemResponse response = new AppItemResponse();
        response.setId(app.getId());
        response.setPackageName(app.getPackageName());
        response.setTitle(app.getTitle());
        response.setDescription(app.getDescription());
        response.setPrice(app.getPrice());
        response.setFree(app.isFree());
        return response;
    }
}
