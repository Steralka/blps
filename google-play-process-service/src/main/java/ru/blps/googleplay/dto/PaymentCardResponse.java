package ru.blps.googleplay.dto;

public class PaymentCardResponse {

    private Long id;
    private Long userId;
    private String maskedNumber;
    private String cvv;
    private String holderName;
    private int expiryMonth;
    private int expiryYear;

    public PaymentCardResponse() {
    }

    public PaymentCardResponse(Long id,
                               Long userId,
                               String maskedNumber,
                               String cvv,
                               String holderName,
                               int expiryMonth,
                               int expiryYear) {
        this.id = id;
        this.userId = userId;
        this.maskedNumber = maskedNumber;
        this.cvv = cvv;
        this.holderName = holderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

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

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }
}
