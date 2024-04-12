package br.com.cdb.java.grupo4.eightbankspring.usecase;


import br.com.cdb.java.grupo4.eightbankspring.dao.CardDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.ClientDAO;
import br.com.cdb.java.grupo4.eightbankspring.enuns.CardType;
import br.com.cdb.java.grupo4.eightbankspring.model.card.Card;
import br.com.cdb.java.grupo4.eightbankspring.model.card.CardFactory;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Scanner;

@Service
public class CardService {
    private Scanner scanner = new Scanner(System.in);

    private ClientDAO clientDAO;
    private CardDAO cardDAO; // Assumindo inicialização correta

    public CardService(CardDAO cardDAO, ClientDAO clientDAO) {
        this.cardDAO = cardDAO;
        this.clientDAO = clientDAO;
    }

    public CardService() {
    }

    private boolean isValidCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }

    private int readMenuOption(int min, int max) {
        int option = -1;
        do {
            try {
                System.out.print("Escolha uma opção: ");
                option = Integer.parseInt(scanner.nextLine());
                if (option < min || option > max) {
                    System.out.println("Opção inválida, por favor tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, por favor insira um número.");
            }
        } while (option < min || option > max);
        return option;
    }

    public void requestCard(Client client) {
        boolean running = true;

        while (running) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("1. Solicitar Novo Cartão");
            System.out.println("2. Visualizar Meus Cartões");
            System.out.println("3. Seguros");
            System.out.println("0. Voltar");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer do scanner

            switch (choice) {
                case 1:
                    solicitNewCard(client);
                    break;
                case 2:
                    viewMyCards(client);
                    break;
                case 3:
                    InsuranceService insuranceService = new InsuranceService();
                    insuranceService.showInsuranceMenu(client);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }

    private void viewMyCards(Client client) {
        System.out.println("Listando seus cartões...");
        var cards = cardDAO.findCardsByClientCPF(client.getCpf());

        if (cards.isEmpty()) {
            System.out.println("Nenhum cartão encontrado para este CPF.");
            return;
        }

        cards.forEach(card -> {
            System.out.println("Cartão Número: " + card.getNumber() + " - Tipo: " + card.getCardType() + " - Válido até: " + card.getExpirationDate() + "- CVV: " + card.getCvv());
        });

        System.out.println("\n1. Desativar Cartão\n0. Voltar");
        int choice = readMenuOption(0, 1);

        if (choice == 1) {
            deactivateCardProcess(client);
        }
    }

    private void solicitNewCard(Client client) {
        System.out.println("\nEscolha o tipo de cartão que deseja solicitar:");
        System.out.println("1. Cartão de Débito");
        System.out.println("2. Cartão de Crédito");
        System.out.println("0. Voltar");

        int cardTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer do scanner

        if (cardTypeChoice == 0) {
            return; // Retorna ao menu anterior
        }
        switch (cardTypeChoice) {
            case 1, 2:
                System.out.println("Defina a senha para o novo cartão:");
                String cardPassword = scanner.nextLine();

                System.out.println("Digite a senha da sua conta para confirmar a solicitação:");
                String accountPassword = scanner.nextLine();

                if (!clientDAO.verifyClientPassword(client.getCpf(), accountPassword)) {
                    System.out.println("Senha da conta incorreta.");
                    return;
                }

                CardType cardType = cardTypeChoice == 1 ? CardType.DEBIT : CardType.CREDIT;
                double limitOrDaily = (cardType == CardType.CREDIT) ? 5000 : 1000;


                Card newCard = generateAndSaveCard(cardType, client.getName(), client.getCpf(), limitOrDaily, "password"); // Substituir "password" pela lógica de senha
                System.out.println("Seu pedido de cartão de " + cardType + " foi confirmado!");
                System.out.println("Número do Cartão: " + newCard.getNumber());
                System.out.println("Data de Validade: " + newCard.getExpirationDate());
                System.out.println("CVV: " + newCard.getCvv());
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }

    }


    private void deactivateCardProcess(Client client) {
        System.out.println("Digite o número do cartão que deseja desativar:");
        String cardNumber = scanner.nextLine();

        boolean cardExists = cardDAO.findCardsByClientCPF(client.getCpf()).stream()
                .anyMatch(card -> card.getNumber().equals(cardNumber));
        if (!cardExists) {
            System.out.println("Número do cartão inválido ou cartão não pertence ao cliente.");
            return;
        }

        System.out.println("Digite a senha da conta para confirmar a desativação:");
        String password = scanner.nextLine();
        if (!clientDAO.verifyClientPassword(client.getCpf(), password)) {
            System.out.println("Senha incorreta.");
            return;
        }

        cardDAO.removeCard(cardNumber);
        System.out.println("Cartão desativado com sucesso.");

    }

    private String requestCardPassword() {
        String password, confirmPassword;
        while (true) {
            System.out.print("Definir senha: ");
            password = scanner.nextLine();

            System.out.print("Confirmar senha: ");
            confirmPassword = scanner.nextLine();

            if (password.equals(confirmPassword)) {
                return password; // Senha confirmada
            } else {
                System.out.println("As senhas não coincidem. Tente novamente.");
            }

            System.out.println("\n0. Voltar");
            System.out.println("Pressione qualquer outra tecla para tentar novamente.");
            String choice = scanner.nextLine();
            if ("0".equals(choice)) {
                return null; // Usuário optou por voltar
            }
        }
    }

    private Card generateAndSaveCard(CardType cardType, String ownerName, String cpf, double limitOrDaily, String password) {
        int cvv = generateCVV();
        Card newCard = CardFactory.createCard(cardType, LocalDate.now().plusYears(5), cvv, ownerName, cpf, limitOrDaily); // Assumindo que CardFactory foi ajustado para aceitar CPF
        cardDAO.addCard(newCard); // Adiciona o cartão ao DAO
        return newCard;
    }

    private int generateCVV() {
        return (int) (Math.random() * 900) + 100; // Gera um número aleatório entre 100 e 999.
    }
}

