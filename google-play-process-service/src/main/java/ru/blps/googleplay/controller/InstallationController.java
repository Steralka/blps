package ru.blps.googleplay.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
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
public class InstallationController {

    private final InstallationService installationService;

    public InstallationController(InstallationService installationService) {
        this.installationService = installationService;
    }

    @PostMapping("/installations")
    public InstallationResponse install(@Valid @RequestBody InstallationRequest request) {
        return installationService.install(request);
    }

    @GetMapping("/installations")
    public List<InstallationResponse> listInstallations(@RequestParam Long userId) {
        return installationService.listInstallations(userId);
    }

    @GetMapping("/purchases")
    public List<PurchaseResponse> listPurchases(@RequestParam Long userId) {
        return installationService.listPurchases(userId);
    }
}
