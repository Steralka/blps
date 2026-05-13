package ru.blps.googleplay.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
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
import ru.blps.googleplay.tx.TxExecutor;

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
    private final TxExecutor tx;

    public InstallationService(CatalogService catalogService,
                               UserAccountService userAccountService,
                               PaymentCardService paymentCardService,
                               PurchaseRepository purchaseRepository,
                               InstallationRepository installationRepository,
                               UserAccountRepository userAccountRepository,
                               TxExecutor tx) {
        this.catalogService = catalogService;
        this.userAccountService = userAccountService;
        this.paymentCardService = paymentCardService;
        this.purchaseRepository = purchaseRepository;
        this.installationRepository = installationRepository;
        this.userAccountRepository = userAccountRepository;
        this.tx = tx;
    }

    @PreAuthorize("@accessPolicy.canInstallFor(#request.userId)")
    public InstallationResponse install(InstallationRequest request) {
        UserAccount user = userAccountService.findEntityById(request.getUserId());
        AppItem app = catalogService.findEntityById(request.getAppId());

        if (!app.isFree()) {
            if (request.getCardId() == null) {
                throw new BadRequestException("Для платного приложения требуется cardId");
            }
            paymentCardService.findActiveCard(user.getId(), request.getCardId());
            if (user.getBalance().compareTo(app.getPrice()) < 0) {
                tx.required(() -> {
                    Purchase failedPurchase = new Purchase();
                    failedPurchase.setUser(user);
                    failedPurchase.setApp(app);
                    failedPurchase.setAmount(app.getPrice());
                    failedPurchase.setCreatedAt(OffsetDateTime.now());
                    failedPurchase.setStatus(PurchaseStatus.FAILED);
                    purchaseRepository.save(failedPurchase);
                });
                throw new BadRequestException("Недостаточно средств на платежном аккаунте");
            }
        }

        return tx.required(() -> {
            UserAccount lockedUser = userAccountService.findEntityById(request.getUserId());
            AppItem lockedApp = catalogService.findEntityById(request.getAppId());

            Purchase purchase = new Purchase();
            purchase.setUser(lockedUser);
            purchase.setApp(lockedApp);
            purchase.setAmount(lockedApp.getPrice());
            purchase.setCreatedAt(OffsetDateTime.now());

            if (lockedApp.isFree()) {
                purchase.setStatus(PurchaseStatus.PAID);
            } else {
                if (lockedUser.getBalance().compareTo(lockedApp.getPrice()) < 0) {
                    purchase.setStatus(PurchaseStatus.FAILED);
                    purchaseRepository.save(purchase);
                    throw new BadRequestException("Недостаточно средств на платежном аккаунте");
                }
                lockedUser.setBalance(lockedUser.getBalance().subtract(lockedApp.getPrice()));
                userAccountRepository.save(lockedUser);
                purchase.setStatus(PurchaseStatus.PAID);
            }

            purchase = purchaseRepository.save(purchase);

            Installation installation = new Installation();
            installation.setUser(lockedUser);
            installation.setApp(lockedApp);
            installation.setPurchase(purchase);
            installation.setStatus(InstallationStatus.INSTALLED);
            installation.setInstalledAt(OffsetDateTime.now());

            return toInstallationResponse(installationRepository.save(installation));
        });
    }

    @PreAuthorize("@accessPolicy.canInstallFor(#userId)")
    public List<InstallationResponse> listInstallations(Long userId) {
        userAccountService.findEntityById(userId);
        return installationRepository.findResponsesByUserIdOrderByInstalledAtDesc(userId);
    }

    @PreAuthorize("@accessPolicy.canInstallFor(#userId)")
    public List<PurchaseResponse> listPurchases(Long userId) {
        userAccountService.findEntityById(userId);
        return purchaseRepository.findResponsesByUserIdOrderByCreatedAtDesc(userId);
    }

    @PreAuthorize("@accessPolicy.canInstallFor(#userId)")
    public InstallationResponse uninstall(Long userId, Long installationId) {
        return tx.required(() -> {
            userAccountService.findEntityById(userId);
            Installation installation = installationRepository.findByIdAndUserId(installationId, userId)
                .orElseThrow(() -> new NotFoundException("Установка не найдена"));
            installation.setStatus(InstallationStatus.UNINSTALLED);
            return toInstallationResponse(installationRepository.save(installation));
        });
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

}
