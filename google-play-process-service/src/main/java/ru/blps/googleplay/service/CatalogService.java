package ru.blps.googleplay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.googleplay.dto.AppItemRequest;
import ru.blps.googleplay.dto.AppItemResponse;
import ru.blps.googleplay.entity.AppItem;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.AppItemRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class CatalogService {

    private final AppItemRepository appItemRepository;

    public CatalogService(AppItemRepository appItemRepository) {
        this.appItemRepository = appItemRepository;
    }

    public List<AppItemResponse> search(String query, BigDecimal minPrice, BigDecimal maxPrice) {
        String safeQuery = query == null ? "" : query;
        BigDecimal safeMin = minPrice == null ? BigDecimal.ZERO : minPrice;
        BigDecimal safeMax = maxPrice == null ? new BigDecimal("999999.99") : maxPrice;

        return appItemRepository.findByActiveTrueAndTitleContainingIgnoreCaseAndPriceBetween(safeQuery, safeMin, safeMax)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public AppItemResponse getById(Long appId) {
        return toResponse(findEntityById(appId));
    }

    @Transactional
    public AppItemResponse create(AppItemRequest request) {
        AppItem app = new AppItem();
        app.setPackageName(request.getPackageName());
        app.setTitle(request.getTitle());
        app.setDescription(request.getDescription());
        app.setPrice(request.getPrice());
        app.setActive(true);

        return toResponse(appItemRepository.save(app));
    }

    @Transactional
    public AppItemResponse update(Long appId, AppItemRequest request) {
        AppItem app = findEntityById(appId);
        app.setPackageName(request.getPackageName());
        app.setTitle(request.getTitle());
        app.setDescription(request.getDescription());
        app.setPrice(request.getPrice());
        return toResponse(appItemRepository.save(app));
    }

    @Transactional
    public void delete(Long appId) {
        AppItem app = findEntityById(appId);
        app.setActive(false);
        appItemRepository.save(app);
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
