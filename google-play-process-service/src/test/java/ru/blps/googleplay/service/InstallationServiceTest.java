package ru.blps.googleplay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.blps.googleplay.dto.InstallationRequest;
import ru.blps.googleplay.dto.InstallationResponse;
import ru.blps.googleplay.dto.PurchaseResponse;
import ru.blps.googleplay.entity.AppItem;
import ru.blps.googleplay.entity.Installation;
import ru.blps.googleplay.entity.PaymentCard;
import ru.blps.googleplay.entity.Purchase;
import ru.blps.googleplay.entity.UserAccount;
import ru.blps.googleplay.enums.InstallationStatus;
import ru.blps.googleplay.enums.PurchaseStatus;
import ru.blps.googleplay.repository.AppItemRepository;
import ru.blps.googleplay.repository.InstallationRepository;
import ru.blps.googleplay.repository.PaymentCardRepository;
import ru.blps.googleplay.repository.PurchaseRepository;
import ru.blps.googleplay.repository.UserAccountRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationServiceTest {

    @Mock
    private AppItemRepository appItemRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private InstallationRepository installationRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    private InstallationService installationService;

    @BeforeEach
    void setUp() {
        CatalogService catalogService = new CatalogService(appItemRepository);
        UserAccountService userAccountService = new UserAccountService(userAccountRepository);
        PaymentCardService paymentCardService = new PaymentCardService(paymentCardRepository, userAccountService);
        installationService = new InstallationService(
            catalogService,
            userAccountService,
            paymentCardService,
            purchaseRepository,
            installationRepository,
            userAccountRepository
        );
    }

    @Test
    void listInstallationsUsesProjectedResponses() {
        UserAccount user = user(1L, "10.00");
        OffsetDateTime installedAt = OffsetDateTime.now();
        InstallationResponse installation = new InstallationResponse(
            9L,
            1L,
            2L,
            7L,
            InstallationStatus.INSTALLED,
            installedAt
        );

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(installationRepository.findResponsesByUserIdOrderByInstalledAtDesc(1L)).thenReturn(List.of(installation));

        List<InstallationResponse> response = installationService.listInstallations(1L);

        assertEquals(1, response.size());
        assertEquals(9L, response.get(0).getId());
        assertEquals(2L, response.get(0).getAppId());
        assertEquals(7L, response.get(0).getPurchaseId());
        assertEquals(installedAt, response.get(0).getInstalledAt());
        verify(installationRepository).findResponsesByUserIdOrderByInstalledAtDesc(1L);
    }

    @Test
    void listPurchasesUsesProjectedResponses() {
        UserAccount user = user(1L, "10.00");
        OffsetDateTime createdAt = OffsetDateTime.now();
        PurchaseResponse purchase = new PurchaseResponse(
            7L,
            1L,
            2L,
            new BigDecimal("4.99"),
            PurchaseStatus.PAID,
            createdAt
        );

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(purchaseRepository.findResponsesByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(purchase));

        List<PurchaseResponse> response = installationService.listPurchases(1L);

        assertEquals(1, response.size());
        assertEquals(7L, response.get(0).getId());
        assertEquals(2L, response.get(0).getAppId());
        assertEquals(new BigDecimal("4.99"), response.get(0).getAmount());
        assertEquals(createdAt, response.get(0).getCreatedAt());
        verify(purchaseRepository).findResponsesByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void installPaidAppDeductsBalanceAndCreatesPurchase() {
        UserAccount user = user(1L, "10.00");
        AppItem app = app(2L, "4.99");
        PaymentCard card = card(3L, user);
        InstallationRequest request = new InstallationRequest();
        request.setUserId(1L);
        request.setAppId(2L);
        request.setCardId(3L);

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(appItemRepository.findByIdAndActiveTrue(2L)).thenReturn(Optional.of(app));
        when(paymentCardRepository.findByIdAndUserIdAndActiveTrue(3L, 1L)).thenReturn(Optional.of(card));
        when(userAccountRepository.save(user)).thenReturn(user);
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> {
            Purchase purchase = invocation.getArgument(0);
            purchase.setId(7L);
            return purchase;
        });
        when(installationRepository.save(any(Installation.class))).thenAnswer(invocation -> {
            Installation installation = invocation.getArgument(0);
            installation.setId(9L);
            return installation;
        });

        InstallationResponse response = installationService.install(request);

        assertEquals(0, new BigDecimal("5.01").compareTo(user.getBalance()));
        assertEquals(9L, response.getId());
        assertEquals(7L, response.getPurchaseId());
        assertEquals(InstallationStatus.INSTALLED, response.getStatus());
        verify(userAccountRepository).save(user);
    }

    @Test
    void uninstallMarksInstallationUninstalled() {
        UserAccount user = user(1L, "10.00");
        AppItem app = app(2L, "0.00");
        Installation installation = new Installation();
        installation.setId(9L);
        installation.setUser(user);
        installation.setApp(app);
        installation.setStatus(InstallationStatus.INSTALLED);
        installation.setInstalledAt(OffsetDateTime.now());

        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(user));
        when(installationRepository.findByIdAndUserId(9L, 1L)).thenReturn(Optional.of(installation));
        when(installationRepository.save(installation)).thenReturn(installation);

        InstallationResponse response = installationService.uninstall(1L, 9L);

        assertEquals(InstallationStatus.UNINSTALLED, response.getStatus());
        verify(installationRepository).save(installation);
    }

    private UserAccount user(Long id, String balance) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setEmail("student@example.com");
        user.setDisplayName("Student");
        user.setBalance(new BigDecimal(balance));
        user.setActive(true);
        return user;
    }

    private AppItem app(Long id, String price) {
        AppItem app = new AppItem();
        app.setId(id);
        app.setPackageName("com.example.app");
        app.setTitle("App");
        app.setDescription("Description");
        app.setPrice(new BigDecimal(price));
        app.setActive(true);
        return app;
    }

    private PaymentCard card(Long id, UserAccount user) {
        PaymentCard card = new PaymentCard();
        card.setId(id);
        card.setUser(user);
        card.setMaskedNumber("**** **** **** 1111");
        card.setHolderName("Student");
        card.setExpiryMonth(12);
        card.setExpiryYear(2030);
        card.setCardToken("token");
        card.setActive(true);
        return card;
    }
}
