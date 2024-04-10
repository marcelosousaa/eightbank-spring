package br.com.cdb.java.grupo4.eightbankspring.model.card;

import java.time.LocalDate;

public class DebitCard extends Card {
    private double dailyLimit;
    private double dailySpend;


    public DebitCard(String number, LocalDate expirationDate, int cvv, String ownerName, String clientCPF, double dailyLimit) {
        super(number, expirationDate, ownerName, clientCPF, cvv);
        this.dailyLimit = dailyLimit;
        this.dailySpend = 0.0;
        this.cardType = "DEBIT";
    }

    @Override
    public boolean makePayment(double amount) {
        if (!isActive || (dailySpend + amount) > dailyLimit) {
            return false; // Pagamento recusado devido a limite diário excedido ou cartão inativo.
        }
        dailySpend += amount;
        return true;
    }

    @Override
    public void updateLimit(double newLimit) {

    }

    public void resetDailySpend() {
        dailySpend = 0;
    }

    // Getters e setters
    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public double getDailySpend() {
        return dailySpend;
    }
}
