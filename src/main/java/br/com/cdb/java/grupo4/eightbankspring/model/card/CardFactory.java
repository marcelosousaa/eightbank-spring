package br.com.cdb.java.grupo4.eightbankspring.model.card;

import br.com.cdb.java.grupo4.eightbankspring.enuns.CardType;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CardFactory {
    private static final Set<String> issuedCardNumbers = new HashSet<>();

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Random random = new Random();
    private static final int CARD_NUMBER_LENGTH = 16;

    public static Card createCard(
            CardType cardType,
            LocalDate expirationDate,
            int cvv,
            String ownerName,
            String clientCPF,
            double limitOrDaily
    ) {
        String number = generateUniqueCardNumber();
        switch (cardType) {
            case CREDIT:
                return new CreditCard(number, expirationDate, ownerName, clientCPF, limitOrDaily, cvv);
            case DEBIT:
                return new DebitCard(number, expirationDate, cvv, ownerName, clientCPF, limitOrDaily);
            default:
                throw new IllegalArgumentException("Tipo de cartão desconhecido.");
        }
    }

    private static String generateUniqueCardNumber() {
        while (true) {
            String cardNumber = generateRandomCardNumber();

            if (!issuedCardNumbers.contains(cardNumber) && isValidLuhn(cardNumber)) {
                issuedCardNumbers.add(cardNumber);
                return cardNumber;
            }
        }
    }

    private static String generateRandomCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        SecureRandom random = new SecureRandom();

        // Gera os primeiros 15 dígitos aleatoriamente
        for (int i = 0; i < 15; i++) {
            int digit = random.nextInt(10); // Gera um dígito aleatório entre 0-9
            cardNumber.append(digit);
        }

        // Calcula e adiciona o dígito verificador ao final
        String partialCardNumber = cardNumber.toString();
        for (int i = 0; i <= 9; i++) {
            String testCardNumber = partialCardNumber + i;
            if (isValidLuhn(testCardNumber)) {
                cardNumber.append(i);
                break;
            }
        }

        return cardNumber.toString();
    }

    private static boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    private static int generateCVV() {
        return secureRandom.nextInt(900) + 100;
    }
}