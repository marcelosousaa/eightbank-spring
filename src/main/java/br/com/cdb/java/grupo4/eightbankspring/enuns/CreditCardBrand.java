package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum CreditCardBrand {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    ELO("Elo"),
    AMERICAN_EXPRESS("American Express");

    private final String brandName;

    CreditCardBrand(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandName() {
        return brandName;
    }
}
