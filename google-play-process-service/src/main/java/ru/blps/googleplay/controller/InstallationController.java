package ru.blps.googleplay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.googleplay.dto.InstallationRequest;
import ru.blps.googleplay.dto.InstallationResponse;
import ru.blps.googleplay.dto.PurchaseResponse;
import ru.blps.googleplay.service.InstallationService;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Installations and Purchases", description = "Установка приложений и история покупок")
public class InstallationController {

    private final InstallationService installationService;

    public InstallationController(InstallationService installationService) {
        this.installationService = installationService;
    }

    @Operation(summary = "Установить приложение, при необходимости выполнить покупку")
    @PostMapping("/installations")
    public InstallationResponse install(@Valid @RequestBody InstallationRequest request) {
        return installationService.install(request);
    }

    @Operation(summary = "Получить историю установок пользователя")
    @GetMapping("/installations")
    public List<InstallationResponse> listInstallations(@RequestParam Long userId) {
        return installationService.listInstallations(userId);
    }

    @Operation(summary = "Удалить установленное приложение")
    @DeleteMapping("/installations/{installationId}")
    public InstallationResponse uninstall(@PathVariable Long installationId, @RequestParam Long userId) {
        return installationService.uninstall(userId, installationId);
    }

    @Operation(summary = "Получить историю покупок пользователя")
    @GetMapping("/purchases")
    public List<PurchaseResponse> listPurchases(@RequestParam Long userId) {
        return installationService.listPurchases(userId);
    }
}
