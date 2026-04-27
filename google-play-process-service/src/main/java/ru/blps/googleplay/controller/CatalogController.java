package ru.blps.googleplay.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<AppItemResponse> search(@RequestParam(required = false) String query,
                                        @RequestParam(required = false) BigDecimal minPrice,
                                        @RequestParam(required = false) BigDecimal maxPrice) {
        return catalogService.search(query, minPrice, maxPrice);
    }

    @GetMapping("/{appId}")
    public AppItemResponse getById(@PathVariable Long appId) {
        return catalogService.getById(appId);
    }

    @PostMapping
    public AppItemResponse create(@Valid @RequestBody AppItemRequest request) {
        return catalogService.create(request);
    }
}
