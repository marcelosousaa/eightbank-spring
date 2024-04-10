package br.com.cdb.java.grupo4.eightbankspring.model.account;

import br.com.cdb.java.grupo4.eightbankspring.enuns.AccountType;
import br.com.cdb.java.grupo4.eightbankspring.model.interfaces.ITaxable;

public class CurrentAccount extends Account implements ITaxable {
    private double accountFee;

    public CurrentAccount(double balance, String ownerCpf, double accountFee) {
        super(balance, AccountType.CURRENT_ACCOUNT, ownerCpf);
        this.accountFee = accountFee;
    }

    public double getAccountFee() {
        return accountFee;
    }

    public void setAccountFee(double accountFee) {
        this.accountFee = accountFee;
    }

    @Override
    public String toString() {
        return "CurrentAccount{" +
                "accountFee=" + accountFee +
                "} " + super.toString();
    }

    @Override
    public double calculateTaxes() {
        return getAccountFee() / 30;
    }
}
