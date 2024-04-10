package br.com.cdb.java.grupo4.eightbankspring.enuns;

public enum SystemMessages {

    WARNING_EMPTY_OR_NULL_FIELD("Campo não pode ser nulo ou vazio!"),
    MANDATORY_FIELD_PT_BR("Campo obrigatório!"),
    INVALID_ZIP_CODE("CEP inválido!"),
    INVALID_CHARACTER("Caracter inválido!"),
    INVALID_OPTION("Opção inválida!"),
    INVALID_FORMAT("Formato inválido!"),
    INVALID_VALUE("Valor inválido!"),
    PROCESSING_PT_BR("Processando...");


    private final String fieldName;

    SystemMessages(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}
