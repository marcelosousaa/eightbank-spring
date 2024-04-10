package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum ClientCategory {

    COMMOM('C', "Comum"),
    SUPER('S', "Super"),
    PREMIUM('P', "Premium");

    private final char categoryCode;
    private final String description;

    ClientCategory(char categoryCode, String description) {
        this.categoryCode = categoryCode;
        this.description = description;
    }

    public char getCategoryCode() {
        return categoryCode;
    }

    public String getDescription() {
        return description;
    }
}
