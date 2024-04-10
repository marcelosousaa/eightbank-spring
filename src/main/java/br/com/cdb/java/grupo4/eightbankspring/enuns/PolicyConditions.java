package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum PolicyConditions {
    FRAUD_INSURANCE_CONDITIONS(
            "Poderá ser acionada caso o cliente não identifique uma compra realizada em seu cartão."
    );

    private final String insuranceConditions;

    PolicyConditions(String insuranceConditions) {
        this.insuranceConditions = insuranceConditions;
    }

    public String getInsuranceConditions() {
        return insuranceConditions;
    }
}
