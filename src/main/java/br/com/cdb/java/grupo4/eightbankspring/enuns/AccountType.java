package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum AccountType {
    SAVINGS_ACCOUNT("CP", "Conta Poupança"),
    CURRENT_ACCOUNT("CC", "Conta Corrente");

    private final String accountTypeName;
    private final String accountTypeAcronym;

    AccountType(String accountTypeName, String accountTypeAcronym) {
        this.accountTypeName = accountTypeName;
        this.accountTypeAcronym = accountTypeAcronym;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public String getAccountTypeAcronym() {
        return this.accountTypeAcronym;
    }
}
