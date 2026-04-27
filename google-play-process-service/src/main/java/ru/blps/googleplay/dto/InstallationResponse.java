package ru.blps.googleplay.dto;

import ru.blps.googleplay.enums.InstallationStatus;

import java.time.OffsetDateTime;

public class InstallationResponse {

    private Long id;
    private Long userId;
    private Long appId;
    private Long purchaseId;
    private InstallationStatus status;
    private OffsetDateTime installedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public InstallationStatus getStatus() {
        return status;
    }

    public void setStatus(InstallationStatus status) {
        this.status = status;
    }

    public OffsetDateTime getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(OffsetDateTime installedAt) {
        this.installedAt = installedAt;
    }
}
