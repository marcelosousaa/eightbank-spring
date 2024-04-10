package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum InsuranceType {

    INSURANCE_TRAVEL("TRAVEL", "Seguro Viagem"),

    INSURANCE_FRAUD("FRAUD", "Seguro contra fraude");

    private final String insuranceCode;
    private final String insuranceName;

    InsuranceType(String insuranceCode, String insuranceName) {
        this.insuranceCode = insuranceCode;
        this.insuranceName = insuranceName;
    }

    public String getInsuranceCode() {
        return insuranceCode;
    }

    public String getInsuranceName() {
        return insuranceName;
    }
}
