package br.com.cdb.java.grupo4.eightbankspring.model.insurance;

import br.com.cdb.java.grupo4.eightbankspring.enuns.InsuranceType;

import java.time.LocalDate;

public class Insurance {
    private long policyNumber;
    private final LocalDate issueDate;
    private final double policyValue;
    private final String policyConditions;
    private final InsuranceType insuranceType;
    private final String clientCPF;


    public Insurance(LocalDate issueDate, double policyValue, String policyConditions, InsuranceType insuranceType, String clientCPF) {
        this.issueDate = issueDate;
        this.policyValue = policyValue;
        this.policyConditions = policyConditions;
        this.insuranceType = insuranceType;
        this.clientCPF = clientCPF;
    }

    public long getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(long policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public double getPolicyValue() {
        return policyValue;
    }

    public String getPolicyConditions() {
        return policyConditions;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }


    public String getClientCPF() {
        return clientCPF;
    }

    @Override
    public String toString() {
        return "Insurance{" +
                "policyNumber=" + policyNumber +
                ", issueDate=" + issueDate +
                ", policyValue=" + policyValue +
                ", policyConditions='" + policyConditions + '\'' +
                ", insuranceType=" + insuranceType +
                '}';
    }
}
