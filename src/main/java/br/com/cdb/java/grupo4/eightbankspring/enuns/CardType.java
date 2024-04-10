package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum CardType {
    DEBIT("Cartão de Débito"),
    CREDIT("Cartão de Crédito");

    private final String description;

    CardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

