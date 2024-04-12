package br.com.cdb.java.grupo4.eightbankspring.model.account;

import br.com.cdb.java.grupo4.eightbankspring.enuns.AccountType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Account {
    private long accountNumber;
    private double balance;
    private AccountType accountType;
    private String ownerCPF;

    public Account(double balance, AccountType accountType, String ownerCPF) {
        this.balance = balance;
        this.accountType = accountType;
        this.ownerCPF = ownerCPF;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getOwnerCPF() {
        return this.ownerCPF;
    }

    public void setOwnerCPF(String ownerCPF) {
        this.ownerCPF = ownerCPF;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
