package br.com.cdb.java.grupo4.eightbankspring.model.card;

import java.time.LocalDate;

public class CreditCard extends Card {
    private double limit;
    private double currentSpend;

    public CreditCard(String number, LocalDate expirationDate, String ownerName, String clientCPF, double limit, int cvv) {
        super(number, expirationDate, ownerName, clientCPF, cvv);
        this.limit = limit;
        this.currentSpend = 0.0;
        this.cardType = "CREDIT";
    }

    @Override
    public boolean makePayment(double amount) {
        if (!isActive || (currentSpend + amount) > limit) {
            return false; // Pagamento recusado devido a limite excedido ou cartão inativo.
        }
        double newSpend = currentSpend + amount;
        if (newSpend > limit * 0.8) {
            double intrest = (newSpend - (limit * 0.8)) * 0.05;
            currentSpend = newSpend + intrest;
        } else {
            currentSpend = newSpend;
        }
        return true;
    }

    @Override
    public void updateLimit(double newLimit) {

    }

    public void applyMonthlyInterest() {
        if (currentSpend > limit * 0.8) {
            double interest = (currentSpend - (limit * 0.8)) * 0.05;
            currentSpend += interest;
        }
    }

    public void resetMonthlySpend() {
        currentSpend = 0;
    }

    // Getters e setters

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getCurrentSpend() {
        return currentSpend;
    }
}
