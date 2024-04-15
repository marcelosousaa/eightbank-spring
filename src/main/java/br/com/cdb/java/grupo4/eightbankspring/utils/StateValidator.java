package br.com.cdb.java.grupo4.eightbankspring.utils;

public class StateValidator {

    private static final int MAX_NAME_LENGTH = 2;

    public static boolean validateStateFormat(String state) {
        if (state.isEmpty()) {
            return false;
        }

        String stateTrimmed = state.trim();
        if (stateTrimmed.length() > MAX_NAME_LENGTH) {
            return false;
        }

        return stateTrimmed.matches("[a-zA-Z\\s]+");
    }
}
