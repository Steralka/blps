package ru.blps.googleplay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.googleplay.dto.InstallationRequest;
import ru.blps.googleplay.dto.InstallationResponse;
import ru.blps.googleplay.dto.PurchaseResponse;
import ru.blps.googleplay.entity.AppItem;
import ru.blps.googleplay.entity.Installation;
import ru.blps.googleplay.entity.Purchase;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.enums.InstallationStatus;
import ru.blps.googleplay.enums.PurchaseStatus;
import ru.blps.googleplay.exception.BadRequestException;
import ru.blps.googleplay.exception.NotFoundException;
import ru.blps.googleplay.repository.InstallationRepository;
import ru.blps.googleplay.repository.PurchaseRepository;
import ru.blps.googleplay.repository.UserAccountRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class InstallationService {

    private final CatalogService catalogService;
    private final UserAccountService userAccountService;
    private final PaymentCardService paymentCardService;
    private final PurchaseRepository purchaseRepository;
    private final InstallationRepository installationRepository;
    private final UserAccountRepository userAccountRepository;

    public InstallationService(CatalogService catalogService,
                               UserAccountService userAccountService,
                               PaymentCardService paymentCardService,
                               PurchaseRepository purchaseRepository,
                               InstallationRepository installationRepository,
                               UserAccountRepository userAccountRepository) {
        this.catalogService = catalogService;
        this.userAccountService = userAccountService;
        this.paymentCardService = paymentCardService;
        this.purchaseRepository = purchaseRepository;
        this.installationRepository = installationRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public InstallationResponse install(InstallationRequest request) {
        UserAccount user = userAccountService.findEntityById(request.getUserId());
        AppItem app = catalogService.findEntityById(request.getAppId());

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setApp(app);
        purchase.setAmount(app.getPrice());
        purchase.setCreatedAt(OffsetDateTime.now());

        if (app.isFree()) {
            purchase.setStatus(PurchaseStatus.PAID);
        } else {
            if (request.getCardId() == null) {
                throw new BadRequestException("Для платного приложения требуется cardId");
            }
            paymentCardService.findActiveCard(user.getId(), request.getCardId());
            if (user.getBalance().compareTo(app.getPrice()) < 0) {
                purchase.setStatus(PurchaseStatus.FAILED);
                purchaseRepository.save(purchase);
                throw new BadRequestException("Недостаточно средств на платежном аккаунте");
            }
            user.setBalance(user.getBalance().subtract(app.getPrice()));
            userAccountRepository.save(user);
            purchase.setStatus(PurchaseStatus.PAID);
        }

        purchase = purchaseRepository.save(purchase);

        Installation installation = new Installation();
        installation.setUser(user);
        installation.setApp(app);
        installation.setPurchase(purchase);
        installation.setStatus(InstallationStatus.INSTALLED);
        installation.setInstalledAt(OffsetDateTime.now());

        return toInstallationResponse(installationRepository.save(installation));
    }

    public List<InstallationResponse> listInstallations(Long userId) {
        userAccountService.findEntityById(userId);
        return installationRepository.findByUserIdOrderByInstalledAtDesc(userId)
            .stream()
            .map(this::toInstallationResponse)
            .toList();
    }

    public List<PurchaseResponse> listPurchases(Long userId) {
        userAccountService.findEntityById(userId);
        return purchaseRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toPurchaseResponse)
            .toList();
    }

    @Transactional
    public InstallationResponse uninstall(Long userId, Long installationId) {
        userAccountService.findEntityById(userId);
        Installation installation = installationRepository.findByIdAndUserId(installationId, userId)
            .orElseThrow(() -> new NotFoundException("Установка не найдена"));
        installation.setStatus(InstallationStatus.UNINSTALLED);
        return toInstallationResponse(installationRepository.save(installation));
    }

    private InstallationResponse toInstallationResponse(Installation installation) {
        InstallationResponse response = new InstallationResponse();
        response.setId(installation.getId());
        response.setUserId(installation.getUser().getId());
        response.setAppId(installation.getApp().getId());
        response.setPurchaseId(installation.getPurchase() != null ? installation.getPurchase().getId() : null);
        response.setStatus(installation.getStatus());
        response.setInstalledAt(installation.getInstalledAt());
        return response;
    }

    private PurchaseResponse toPurchaseResponse(Purchase purchase) {
        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getId());
        response.setUserId(purchase.getUser().getId());
        response.setAppId(purchase.getApp().getId());
        response.setAmount(purchase.getAmount() == null ? BigDecimal.ZERO : purchase.getAmount());
        response.setStatus(purchase.getStatus());
        response.setCreatedAt(purchase.getCreatedAt());
        return response;
    }
}
