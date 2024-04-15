package br.com.cdb.java.grupo4.eightbankspring.utils;

import org.springframework.stereotype.Component;

@Component
public class CPFValidator {

    public boolean validateCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", ""); // Remove caracteres não numéricos

        if (cpf.length() != 11 || hasAllEqualDigits(cpf)) {
            return false;
        }

        int[] weightsFirstDigit = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weightsSecondDigit = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int firstDigit = calulateDigit(cpf.substring(0, 9), weightsFirstDigit);
        int secondDigit = calulateDigit(cpf.substring(0, 9) + firstDigit, weightsSecondDigit);

        return cpf.equals(cpf.substring(0, 9) + firstDigit + secondDigit);
    }

    private static int calulateDigit(String str, int[] weights) {
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            int num = Integer.parseInt(str.substring(i, i + 1));
            sum += num * weights[i];
        }
        int result = 11 - (sum % 11);
        return result > 9 ? 0 : result;
    }

    private static boolean hasAllEqualDigits(String cpf) {
        return cpf.chars().allMatch(c -> c == cpf.charAt(0));
    }
}
