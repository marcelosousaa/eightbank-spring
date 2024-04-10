package br.com.cdb.java.grupo4.eightbankspring.model.account;

import br.com.cdb.java.grupo4.eightbankspring.enuns.AccountType;
import br.com.cdb.java.grupo4.eightbankspring.model.interfaces.IYieldable;

public class SavingsAccount extends Account implements IYieldable {
    private double annualPercentageYield;

    public SavingsAccount(double balance, String ownerCpf, double annualPercentageYield) {
        super(balance, AccountType.SAVINGS_ACCOUNT, ownerCpf);
        this.annualPercentageYield = annualPercentageYield;
    }

    public double getAnnualPercentageYield() {
        return annualPercentageYield;
    }

    public void setAnnualPercentageYield(double annualPercentageYield) {
        this.annualPercentageYield = annualPercentageYield;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "annualPercentageYield=" + annualPercentageYield +
                "} " + super.toString();
    }

    @Override
    public double calculateYields(double balance, double interestRate, int time) {
        return 0;
    }

    @Override
    public double calculateYields(int time) {
        double calculatedValue;
        double rate = 1 + this.annualPercentageYield;

        calculatedValue = super.getBalance() * Math.pow(rate, time);
        calculatedValue = calculatedValue - super.getBalance();

        return calculatedValue;
    }
}
