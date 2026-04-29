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
import ru.blps.googleplay.dto.AppItemRequest;
import ru.blps.googleplay.dto.AppItemResponse;
import ru.blps.googleplay.service.CatalogService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/apps")
@Tag(name = "Catalog", description = "Поиск, просмотр и управление приложениями каталога")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Operation(summary = "Поиск приложений")
    @GetMapping
    public List<AppItemResponse> search(@RequestParam(required = false) String query,
                                        @RequestParam(required = false) BigDecimal minPrice,
                                        @RequestParam(required = false) BigDecimal maxPrice) {
        return catalogService.search(query, minPrice, maxPrice);
    }

    @Operation(summary = "Получить приложение по идентификатору")
    @GetMapping("/{appId}")
    public AppItemResponse getById(@PathVariable Long appId) {
        return catalogService.getById(appId);
    }

    @Operation(summary = "Создать приложение")
    @PostMapping
    public AppItemResponse create(@Valid @RequestBody AppItemRequest request) {
        return catalogService.create(request);
    }

    @Operation(summary = "Обновить приложение")
    @PutMapping("/{appId}")
    public AppItemResponse update(@PathVariable Long appId, @Valid @RequestBody AppItemRequest request) {
        return catalogService.update(appId, request);
    }

    @Operation(summary = "Удалить приложение из активного каталога")
    @DeleteMapping("/{appId}")
    public ResponseEntity<Void> delete(@PathVariable Long appId) {
        catalogService.delete(appId);
        return ResponseEntity.noContent().build();
    }
}
