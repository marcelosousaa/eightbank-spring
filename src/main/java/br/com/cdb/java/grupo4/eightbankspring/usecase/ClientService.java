package br.com.cdb.java.grupo4.eightbankspring.usecase;

import br.com.cdb.java.grupo4.eightbankspring.dao.CardDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.ClientDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.impl.JdbcTemplateDAOImpl;
import br.com.cdb.java.grupo4.eightbankspring.enuns.AccountType;
import br.com.cdb.java.grupo4.eightbankspring.enuns.AnsiColors;
import br.com.cdb.java.grupo4.eightbankspring.enuns.ClientCategory;
import br.com.cdb.java.grupo4.eightbankspring.enuns.SystemMessages;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.AccountNotFoundException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.ClientNotFoundException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InsufficientFundsException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InvalidValueException;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.account.CurrentAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.account.SavingsAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.card.Card;
import br.com.cdb.java.grupo4.eightbankspring.model.card.CreditCard;
import br.com.cdb.java.grupo4.eightbankspring.model.card.DebitCard;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Address;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Service
public class ClientService {
    List<Account> clientAccountsList;
    Client client;

    @Autowired
    private ClientDAO clientDAO;
    @Autowired
    private CardDAO cardDAO;
    @Autowired
    private CardService cardService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JdbcTemplateDAOImpl jdbcTemplateDAOImpl;

    private ClientCategory clientCategory;

    public void addClient(Client client) {
        //Validations

        String email = client.getEmail();

        String passwordString;
        try {
            passwordString = PasswordService.generateStrongPassword(client.getPassword());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            passwordString = client.getPassword();
        }

        String name = client.getName();
        String cpf = client.getCpf();
        LocalDate dateOfBirth = client.getDateOfBirth();
        Address address = client.getAddress();
        String phoneNumber = client.getPhoneNumber();
        double grossMonthlyIncome = client.getGrossMonthlyIncome();
        clientCategory = checkClientCategory(client.getGrossMonthlyIncome());

        client = new Client(
                email,
                passwordString,
                name,
                cpf,
                dateOfBirth,
                address,
                clientCategory,
                phoneNumber,
                grossMonthlyIncome
        );

        //SALVA NO ARRAY
        //clientDAO.save(client);

        //SALVA NO BANCO
        jdbcTemplateDAOImpl.saveClient(client);

        //REGISTRA AS CONTAS
        registerClientAccounts(client, 3);

    }


    public boolean clientRegistration() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidValueException {

        String cpf = inputCPF();
        if (cpf == null) {
            return false;
        }

        double grossMonthlyIncome = inputGrossMonthlyIncome();
        String email = inputEmail();
        String name = inputName();
        LocalDate dateOfBirth = inputDateOfBirth();
        Address address = inputAddress();
        String phoneNumber = inputPhoneNumber();
        String passwordString = inputPassword();

        client = new Client(
                email,
                passwordString,
                name,
                cpf,
                dateOfBirth,
                address,
                clientCategory,
                phoneNumber,
                grossMonthlyIncome
        );

        //Adiciona usuario ao "banco de dados" de clientes
        clientDAO.addClient(client);

        //Cria e registra a conta selecionada
        registerClientAccounts(client);

        return true;
    }

    private void registerClientAccounts(Client client) {
        // Cria a(s) conta(s) do cliente e devolve para um ArrayList
        List<Account> clientAccountsList = clientAccountsRegistration(client);

        //Itera no array retornado e seta o Cliente como owner das contas
        for (Account account : clientAccountsList) {
            accountService.setAccountOwner(account, client.getCpf());
        }
    }

    private void registerClientAccounts(Client client, int accountsTypeOtion) {
        // Cria a(s) conta(s) do cliente e devolve para um ArrayList
        List<Account> clientAccountsList = clientAccountsRegistration(client, accountsTypeOtion);

        //Itera no array retornado e seta o Cliente como owner das contas
        for (Account account : clientAccountsList) {
            accountService.setAccountOwner(account, client.getCpf());
        }
    }

    private String inputPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String passwordString;

        while (true) {
            System.out.println("Certo, para finalizar vamos definir uma senha de acesso: ");
            passwordString = new Scanner(System.in).nextLine();
            if (passwordString.isEmpty()) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());
            } else {
                passwordString = PasswordService.generateStrongPassword(passwordString);
                break;
            }
        }

        return passwordString;
    }

    private String inputPhoneNumber() {
        String phoneNumber;

        while (true) {
            System.out.println("Informa um telefone pra contato, no formato (XX) XXXX(X)-XXXX, por favor:  ");
            phoneNumber = new Scanner(System.in).nextLine();
            if (!PhoneNumberValidator.validatePhoneNumber(phoneNumber)) {
                System.out.println(SystemMessages.INVALID_FORMAT.getFieldName());
            } else {
                break;
            }
        }

        return phoneNumber;
    }

    private Address inputAddress() {
        System.out.println("Certo! Agora vamos cadastrar seu endereço.");

        Address address;
        String streetName;
        long number;
        String district;
        String city;
        String state;
        String zipCode;

        while (true) {
            System.out.println("Por favor digite o nome da rua: ");
            streetName = new Scanner(System.in).nextLine();
            if (streetName.isEmpty()) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("Digite o número do endereço: ");
            try {
                number = new Scanner(System.in).nextLong();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Formato inválido!");
            }
        }

        while (true) {
            System.out.println("Certo, e qual o seu bairro: ");
            district = new Scanner(System.in).nextLine();
            if (district.isEmpty()) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("Beleza! E a cidade: ");
            city = new Scanner(System.in).nextLine();
            if (city.isEmpty()) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("Digita pra gente o seu estado, por favor: ");
            state = new Scanner(System.in).nextLine();
            if (state.isEmpty()) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("OK! Quase finalizando... Agora informa seu CEP, com hífen: ");
            String zipCodeString = new Scanner(System.in).nextLine();
            if (!ZipCodeValidator.validateZipCode(zipCodeString)) {
                System.out.println(SystemMessages.INVALID_ZIP_CODE.getFieldName());
            } else {
                zipCodeString = zipCodeString.replace("-", "");
                zipCode = zipCodeString;
                break;
            }
        }

        System.out.println("Se o endereço possui complemento, digite abaixo, por favor. Enter para continuar.");
        String addressComplement;
        addressComplement = new Scanner(System.in).nextLine();

        if (addressComplement.isEmpty()) {
            return new Address(streetName, number, district, city, state, zipCode);
        } else {
            return new Address(streetName, number, district, city, state, zipCode, addressComplement);
        }
    }

    private LocalDate inputDateOfBirth() {
        LocalDate dateOfBirth;
        String dob;

        while (true) {
            System.out.println("Digite sua data de nascimento, no formato(dd/mm/aaaa):");
            dob = new Scanner(System.in).nextLine();
            if (!DateOfBirthValidator.validateDateOfBirth(dob)) {
                System.out.println("Formato inválido!");
            } else {
                String[] fields = dob.split("/");
                int day = Integer.parseInt(fields[0]);
                int month = Integer.parseInt(fields[1]);
                int year = Integer.parseInt(fields[2]);

                if (year > LocalDate.now().getYear()) {
                    System.out.println("Ano inválido!");
                } else if (year == LocalDate.now().getYear() - 18) {
                    if (month <= LocalDate.now().getMonthValue()) {
                        if (day <= LocalDate.now().getDayOfMonth()) {
                            try {
                                dateOfBirth = LocalDate.of(year, month, day);
                                break;
                            } catch (Exception e) {
                                System.out.println("Data inválida!");
                                //System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Cadastro permitido somente para maiores de 18 anos.");
                        }
                    } else {
                        System.out.println("Cadastro permitido somente para maiores de 18 anos.");
                    }
                } else {
                    try {
                        dateOfBirth = LocalDate.of(year, month, day);
                        break;
                    } catch (Exception e) {
                        System.out.println("Data inválida!");
                        //System.out.println(e.getMessage());
                    }
                }
            }
        }
        return dateOfBirth;
    }

    private String inputName() {
        String name;
        while (true) {
            System.out.println("Digite seu nome: ");
            Scanner scanner = new Scanner(System.in);
            name = new Scanner(System.in).nextLine();

            if (!NameValidator.validateName(name)) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());

            } else {
                return name;
            }
        }
    }

    private String inputEmail() {
        String email;

        while (true) {
            System.out.println("Informe seu email: ");
            email = new Scanner(System.in).nextLine();
            if (email.isEmpty()) {
                System.out.println(SystemMessages.MANDATORY_FIELD_PT_BR.getFieldName());
            } else {
                if (!EmailValidator.validateEmail(email)) {
                    System.err.println("Parece que seu e-mail está em um formato inválido...");
                } else {

                    if (!clientDAO.findClientByEmail(email)) {
                        break;
                    } else {
                        System.err.println("E-mail já existe na base de dados!");
                    }
                }
            }
        }

        return email;
    }

    private double inputGrossMonthlyIncome() throws InvalidValueException {
        double grossMonthlyIncome;

        while (true) {
            System.out.println("Informe sua renda mensal bruta: ");
            try {
                grossMonthlyIncome = new Scanner(System.in).nextDouble();
                if (grossMonthlyIncome <= 0) {
                    throw new InvalidValueException("Valor inválido!");
                } else {
                    clientCategory = checkClientCategory(grossMonthlyIncome);
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Formato inválido!");
            }
        }

        return grossMonthlyIncome;
    }

    private String inputCPF() {
        String cpf;

        while (true) {
            System.out.println("Digite o numero do seu CPF, sem pontos ou traços: ");
            try {
                cpf = new Scanner(System.in).nextLine();
                if (!CPFValidator.validateCPF(cpf)) {
                    System.out.println("CPF inválido!");
                } else if (!clientDAO.searchClientByCPF(cpf)) {
                    break;
                } else {
                    System.err.println("Usuário já existente na base de dados!");
                    cpf = null;
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Caracter(es) inválido(s)!");
            }
        }
        return cpf;
    }

    private List<Account> clientAccountsRegistration(Client client) {
        Account account;
        List<Account> accountList = new ArrayList<>();
        double annualPercentageYield = checkAnnualPercentageYield(client);
        double currentAccountMonthlyFee = checkCurrentAccountMonthlyFee(client);

        while (true) {
            System.out.println("Que tipo de conta você deseja abrir: ");
            int iterator = 1;
            for (AccountType type : AccountType.values()) {
                System.out.println(iterator + " - " + type.getAccountTypeName());
                iterator++;
            }
            System.out.println((iterator++) + " - Ambas");
            try {
                int accountTypeOption = new Scanner(System.in).nextInt();
                if (accountTypeOption < 1 || accountTypeOption > 3) {
                    System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                } else if (accountTypeOption == 1) {
                    account = accountService.createSavingsAccount(client.getCpf(), annualPercentageYield);
                    accountList.add(account);
                } else if (accountTypeOption == 2) {
                    account = accountService.createCurrentAccount(client.getCpf(), currentAccountMonthlyFee);
                    accountList.add(account);
                } else {
                    account = accountService.createSavingsAccount(client.getCpf(), annualPercentageYield);
                    accountList.add(account);
                    account = accountService.createCurrentAccount(client.getCpf(), currentAccountMonthlyFee);
                    accountList.add(account);
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
        }
        return accountList;
    }

    private List<Account> clientAccountsRegistration(Client client, int accountsType) {
        Account account;
        List<Account> accountList = new ArrayList<>();
        double annualPercentageYield = checkAnnualPercentageYield(client);
        double currentAccountMonthlyFee = checkCurrentAccountMonthlyFee(client);

        while (true) {
            try {
                if (accountsType < 1 || accountsType > 3) {
                    System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                } else if (accountsType == 1) {
                    account = accountService.createSavingsAccount(client.getCpf(), annualPercentageYield);
                    accountList.add(account);
                } else if (accountsType == 2) {
                    account = accountService.createCurrentAccount(client.getCpf(), currentAccountMonthlyFee);
                    accountList.add(account);
                } else {
                    account = accountService.createSavingsAccount(client.getCpf(), annualPercentageYield);
                    accountList.add(account);
                    account = accountService.createCurrentAccount(client.getCpf(), currentAccountMonthlyFee);
                    accountList.add(account);
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
        }
        return accountList;
    }

    private double checkCurrentAccountMonthlyFee(Client client) {
        double currentAccountMonthlyFee;

        if (client.getClientCategory().equals(ClientCategory.COMMOM)) {
            currentAccountMonthlyFee = 12;
        } else if (client.getClientCategory().equals(ClientCategory.SUPER)) {
            currentAccountMonthlyFee = 8;
        } else {
            currentAccountMonthlyFee = 0;
        }

        return currentAccountMonthlyFee;
    }

    private double checkAnnualPercentageYield(Client client) {
        double annualPercentageYield;

        if (client.getClientCategory().equals(ClientCategory.COMMOM)) {
            annualPercentageYield = 0.05;
        } else if (client.getClientCategory().equals(ClientCategory.SUPER)) {
            annualPercentageYield = 0.07;
        } else {
            annualPercentageYield = 0.09;
        }

        return annualPercentageYield;
    }

    private ClientCategory checkClientCategory(double grossMonthlyIncome) {
        ClientCategory clientCategory;

        if (grossMonthlyIncome <= 5000) {
            clientCategory = ClientCategory.COMMOM;
        } else if (grossMonthlyIncome > 5000 && grossMonthlyIncome <= 15000) {
            clientCategory = ClientCategory.SUPER;
        } else {
            clientCategory = ClientCategory.PREMIUM;
        }
        return clientCategory;
    }

    public Client login() {
        Client client = null;
        String email;
        String passwordString;

        while (client == null) {
            while (true) {
                System.out.println("Digite seu email: ");
                email = new Scanner(System.in).nextLine();
                if (email.isEmpty()) {
                    System.err.println("Campo obrigatório!");
                } else if (!EmailValidator.validateEmail(email)) {
                    System.err.println("Formato inválido!");
                } else {
                    break;
                }
            }

            while (true) {
                System.out.println("Digite sua senha: ");
                passwordString = new Scanner(System.in).nextLine();
                if (passwordString.isEmpty()) {
                    System.err.println("Campo obrigatório!");
                } else {
                    break;
                }
            }

            try {
                client = clientDAO.searchClientByEmail(email);
                try {
                    if (!PasswordService.validatePassword(passwordString, client.getPassword())) {
                        client = null;
                    }
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    client = null;
                }
            } catch (Exception e) {
                client = null;
            }

            if (client != null) {
                System.out.println("\nLogin realizado com sucesso!\n\n" + "Bem-vindo, " + client.getName() + "!");
            } else {
                System.out.println(AnsiColors.ANSI_RED.getAnsiColorCode() +
                        "\nNao foi possivel realizar o login, verifique seus dados e tente novamente.\n"
                        + AnsiColors.ANSI_RESET.getAnsiColorCode());
                break;
            }
        }
        return client;
    }

    private int validateClientOptionNumber() {
        int clientOption;

        while (true) {
            try {
                clientOption = new Scanner(System.in).nextInt();
                return clientOption;
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
        }
    }

    public void clientMenu(Client client) throws AccountNotFoundException {
        boolean runningClientMenu = false;

        while (!runningClientMenu) {
            System.out.println(
                    "Selecione uma opção abaixo: "
                            + "\n 1 - Saldo"
                            + "\n 2 - Depósito"
                            + "\n 3 - Saque"
                            + "\n 4 - Transferencias"
                            + "\n 5 - Cartões" // SUB-MENU SEGUROS
                            + "\n 6 - Pagamentos"
                            + "\n 7 - Meu cadastro"
                            + "\n 0 - Sair"
            );

            int clientMenuOption = 0;

            try {
                clientMenuOption = new Scanner(System.in).nextInt();

                if (clientMenuOption < 0 || clientMenuOption > 7) {
                    System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                }

                switch (clientMenuOption) {
                    case 1:
                        try {
                            getClientBalance(client);
                        } catch (AccountNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            depositOnClientAccount(client);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            withdrawFromAccount(client);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 4:
                        try {
                            showTransferMenu(client);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 5:
                        //cardService.showCardMenu(client);
                        this.cardService.requestCard(client);
                        break;
                    case 6:
                        showPaymentsMenu(client);
                        break;
                    case 7:
                        try {
                            showProfileEditor(client);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        break;
                    case 0:
                        System.out.println("Saindo...");
                        runningClientMenu = true;
                        break;
                    default:
                        System.err.println(SystemMessages.INVALID_OPTION.getFieldName());
                }
            } catch (InputMismatchException e) {
                System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
        }
    }

    private void showPaymentsMenu(Client client) throws AccountNotFoundException {
        while (true) {

            System.out.println("Qual o valor do pagamento?");
            double paymentValue = 0d;

            try {
                paymentValue = new Scanner(System.in).nextDouble();

                if (paymentValue <= 0) {
                    System.out.println(SystemMessages.INVALID_VALUE.getFieldName());
                    break;
                } else {

                    System.out.println("E qual a forma de pagamento?"
                            + "\n1 - Débito em conta"
                            + "\n2 - Pagar com cartão"
                            + "\n0 - Voltar"
                    );

                    try {
                        int option = new Scanner(System.in).nextInt();

                        switch (option) {
                            case 1:
                                payUsingAccounts(client, paymentValue);
                                break;
                            case 2:
                                showCardPaymentOptions(client, paymentValue);
                                break;
                            case 0:
                                System.out.println("Voltando...");
                                break;
                            default:
                                System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                        }
                    } catch (InputMismatchException e) {
                        System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                    }

                }
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
            break;
        }
    }

    private void payUsingAccounts(Client client, double paymentValue) throws AccountNotFoundException {
        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        boolean runningWithdrawFromAccount = false;
        char clientOption = ' ';

        while (!runningWithdrawFromAccount) {
            if (clientAccountsList.size() > 1) {
                System.out.println("\nVimos aqui que você possui mais de uma conta conosco.\n");

                System.out.println("Número da Conta - Tipo da Conta");
                for (Account account : clientAccountsList) {
                    System.out.println(" - " + account.getAccountNumber()
                            + " - " + account.getAccountType().getAccountTypeName());
                }

                System.out.println("\nPor favor digite o numero da conta que deseja utilizar para efetuar o pagamento: ");

                try {
                    long accountNumber = new Scanner(System.in).nextLong();

                    Account accountToCheck = null;

                    for (Account account : clientAccountsList) {
                        if (account.getAccountNumber() != accountNumber) {
                            System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                        } else {
                            accountToCheck = accountService.findAccountByNumber(accountNumber);

                            try {
                                accountService.withdraw(accountToCheck.getAccountNumber(), paymentValue);
                                System.out.println("Pagamento realizado com sucesso!");
                                break;
                            } catch (InsufficientFundsException e) {
                                System.err.println("Saldo insuficiente!");
                            } catch (InvalidValueException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }

                    if (accountToCheck == null) {
                        throw new AccountNotFoundException(
                                "\n"
                                        + AnsiColors.ANSI_RED.getAnsiColorCode()
                                        + "Conta não está na lista!"
                                        + AnsiColors.ANSI_RESET.getAnsiColorCode()
                                        + "\n"
                        );
                    }
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            } else {
                Account clientAccount = clientAccountsList.get(0);

                System.out.println(
                        "\nNúmero da Conta: " + clientAccount.getAccountNumber()
                                + "\nTipo da Conta: " + clientAccount.getAccountType().getAccountTypeName()
                                + "\nSaldo atual: " + clientAccount.getBalance()
                );

                System.out.println("Digite o valor do pagamento: ");
                try {
                    accountService.withdraw(clientAccount.getAccountNumber(), paymentValue);
                    System.out.println("Pagamento realizado com sucesso!");
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                } catch (InsufficientFundsException e) {
                    System.err.println("Saldo insuficiente!");
                } catch (InvalidValueException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("\nDeseja efetuar outro pagamento?(S/N)");
            clientOption = validateClientOptionYesOrNo();
            if (clientOption != 'S') {
                runningWithdrawFromAccount = true;
            }
        }
    }

    private void showCardPaymentOptions(Client client, double paymentValue) {


        System.out.println("Listando seus cartões...");
        var cards = cardDAO.findCardsByClientCPF(client.getCpf());

        if (cards.isEmpty()) {
            System.out.println("Nenhum cartão encontrado para este CPF.");
            return;
        }

        int index = 1;
        for (Card card : cards) {
            System.out.println(index++ + " - Cartão Número: " + card.getNumber() + " - Tipo: " + card.getCardType() + " - Válido até: " + card.getExpirationDate());
        }

        System.out.println("Digite o número referente ao cartão escolhido:");
        int cardChoiceIndex = new Scanner(System.in).nextInt();
        if (cardChoiceIndex < 1 || cardChoiceIndex > cards.size()) {
            System.out.println("Seleção inválida.");
            return;
        }

        Card selectedCard = cards.get(cardChoiceIndex - 1);

        processCardPayment(selectedCard, paymentValue);
    }

    private void processCardPayment(Card selectedCard, double paymentValue) {
        if (selectedCard instanceof CreditCard) {
            // Processamento específico para cartão de crédito
            System.out.println("Processando pagamento com cartão de crédito...");
        } else if (selectedCard instanceof DebitCard) {
            // Processamento específico para cartão de débito
            System.out.println("Processando pagamento com cartão de débito...");
        } else {
            System.out.println("Tipo de cartão não reconhecido.");
            return;
        }

        if (selectedCard.makePayment(paymentValue)) {
            System.out.println("Pagamento de R$" + paymentValue + " efetuado com sucesso no cartão " + selectedCard.getNumber() + ".");
        } else {
            System.out.println("Pagamento não realizado. Verifique o limite ou saldo do seu cartão.");
        }
    }

    private void showProfileEditor(Client client)
            throws ClientNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, AccountNotFoundException {
        showClientProfile(client);

        boolean runningProfileEditMenu = false;
        while (!runningProfileEditMenu) {
            System.out.println("Deseja atualizar alguma informação do perfil(S/N)?");
            char clientOption = validateClientOptionYesOrNo();

            if (clientOption == 'S') {
                System.out.println(
                        "Selecione uma opção no menu abaixo para atualizar:"
                                + "\n 1 - Nome"
                                + "\n 2 - E-mail"
                                + "\n 3 - Senha"
                                + "\n 4 - Telefone"
                                + "\n 5 - Renda"
                                + "\n 6 - Endereço"
                                + "\n 0 - Sair"
                );
                try {
                    int profileEditMenuOption = new Scanner(System.in).nextInt();
                    switch (profileEditMenuOption) {
                        case 1:
                            updateProfileField(client, "nome");
                            break;
                        case 2:
                            updateProfileField(client, "email");
                            break;
                        case 3:
                            updateProfileField(client, "senha");
                            break;
                        case 4:
                            updateProfileField(client, "telefone");
                            break;
                        case 5:
                            updateProfileField(client, "renda");
                            break;
                        case 6:
                            updateProfileField(client, "endereço");
                            break;
                        case 0:
                            System.out.println("Saindo...");
                            runningProfileEditMenu = true;
                            break;
                        default:
                            System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                    }
                } catch (InputMismatchException e) {
                    System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                }
            } else {
                runningProfileEditMenu = true;
            }
        }
        showClientProfile(client);
    }

    private void updateProfileField(Client client, String field) throws ClientNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        switch (field) {
            case "nome":
                while (true) {
                    System.out.println("Certo, como gostaria de ser chamado à partir de agora?");
                    String newProfileName = new Scanner(System.in).nextLine();

                    if (newProfileName.isEmpty()) {
                        System.out.println("Novo campo nome está vazio! Deseja prosseguir mesmo assim?(S/N)");
                        char option = validateClientOptionYesOrNo();
                        if (option == 'S') {
                            clientDAO.updateClientProfileName(client, newProfileName);
                            break;
                        }
                    } else {
                        clientDAO.updateClientProfileName(client, newProfileName);
                        break;
                    }

                    break;
                }
                break;

            case "email":
                while (true) {
                    System.out.println("Certo, qual seu novo email?");
                    String newProfileEmail = new Scanner(System.in).nextLine();

                    if (newProfileEmail.isEmpty()) {
                        System.err.println("Novo e-mail não pode estar vazio!");
                    } else {
                        clientDAO.updateClientProfileEmail(client, newProfileEmail);
                        break;
                    }
                }
                break;

            case "senha":
                while (true) {
                    System.out.println("Certo, qual sua nova senha?");
                    String newPassword = new Scanner(System.in).nextLine();

                    if (newPassword.isEmpty()) {
                        System.err.println("Senha não pode ser vazia!");
                    } else {
                        newPassword = PasswordService.generateStrongPassword(newPassword);
                        clientDAO.updateClientProfilePassword(client, newPassword);
                        break;
                    }
                }
                break;

            case "telefone":
                while (true) {
                    System.out.println("Certo, qual seu novo telefone?");
                    String newPhoneNumber = new Scanner(System.in).nextLine();

                    if (newPhoneNumber.isEmpty()) {
                        System.err.println("Telefone não pode ser vazio!");
                    } else {
                        if (!PhoneNumberValidator.validatePhoneNumber(newPhoneNumber)) {
                            System.out.println(SystemMessages.INVALID_FORMAT.getFieldName());
                        } else {
                            clientDAO.updateClientProfilePhoneNumber(client, newPhoneNumber);
                        }
                        break;
                    }
                }
                break;

            case "renda":
                while (true) {
                    System.out.println("Certo, qual sua nova renda?");
                    String newGrossMonthlyIncome = new Scanner(System.in).nextLine();

                    if (newGrossMonthlyIncome.isEmpty()) {
                        System.err.println("Telefone não pode ser vazio!");
                    } else {
                        try {
                            double value = Double.parseDouble(newGrossMonthlyIncome);
                            clientDAO.updateClientProfileGrossMonthlyIncome(client, value);
                            break;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case "endereço":
                updateProfileAddress(client);
                break;

            default:
                SystemMessages.INVALID_OPTION.getFieldName();
        }


    }

    private void updateProfileAddress(Client client) throws ClientNotFoundException {
        boolean runningUpdateProfileAddressMenu = false;
        while (!runningUpdateProfileAddressMenu) {
            System.out.println("Selecione a informação que deseja atualizar: "
                    + "\n1 - Nome do Endereço"
                    + "\n2 - Número do Endereço"
                    + "\n3 - Bairro"
                    + "\n4 - Cidade"
                    + "\n5 - Estado"
                    + "\n6 - CEP"
                    + "\n7 - Complemento"
                    + "\n0 - Voltar");

            try {
                int option = new Scanner(System.in).nextInt();
                switch (option) {
                    case 1:
                        while (true) {
                            System.out.println("Certo, qual o nome da rua?");
                            String newStreetName = new Scanner(System.in).nextLine();
                            if (newStreetName.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                clientDAO.updateClientProfileAddressStreetName(client, newStreetName);
                                break;
                            }
                        }
                        break;
                    case 2:
                        while (true) {
                            System.out.println("Certo, qual o número");
                            String newNumber = new Scanner(System.in).nextLine();
                            if (newNumber.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                try {
                                    long newNumberConverted = Long.parseLong(newNumber);
                                    clientDAO.updateClientProfileAddressNumber(client, newNumberConverted);
                                    break;
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case 3:
                        while (true) {
                            System.out.println("Certo, qual o Bairro?");
                            String newDistrictName = new Scanner(System.in).nextLine();
                            if (newDistrictName.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                clientDAO.updateClientProfileAddressDistrictName(client, newDistrictName);
                                break;
                            }
                        }
                        break;
                    case 4:
                        while (true) {
                            System.out.println("Certo, qual a cidade?");
                            String newCityName = new Scanner(System.in).nextLine();
                            if (newCityName.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                clientDAO.updateClientProfileAddressCityName(client, newCityName);
                                break;
                            }
                        }
                        break;
                    case 5:
                        while (true) {
                            System.out.println("Certo, qual o estado?");
                            String newStateName = new Scanner(System.in).nextLine();
                            if (newStateName.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                clientDAO.updateClientProfileAddressStateName(client, newStateName);
                                break;
                            }
                        }
                        break;
                    case 6:
                        while (true) {
                            System.out.println("Certo, qual o CEP?");
                            String newZipCode = new Scanner(System.in).nextLine();
                            if (newZipCode.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                if (!ZipCodeValidator.validateZipCode(newZipCode)) {
                                    System.out.println(SystemMessages.INVALID_ZIP_CODE.getFieldName());
                                } else {
                                    clientDAO.updateClientProfileAddressCep(client, newZipCode);
                                    break;
                                }
                            }
                        }
                        break;
                    case 7:
                        while (true) {
                            System.out.println("Certo, qual o complemento?");
                            String newAddressComplement = new Scanner(System.in).nextLine();
                            if (newAddressComplement.isEmpty()) {
                                System.out.println(SystemMessages.WARNING_EMPTY_OR_NULL_FIELD.getFieldName());
                            } else {
                                clientDAO.updateClientProfileAddressComplement(client, newAddressComplement);
                                break;
                            }
                        }
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        runningUpdateProfileAddressMenu = true;
                        break;
                    default:
                        System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                }
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
        }
    }

    private void showClientProfile(Client client) throws AccountNotFoundException {
        System.out.println("Bem vindo ao seu perfil. "
                + "Aqui você pode conferir os seus dados\n");

        // DADOS PESSOAIS
        System.out.println(
                "CPF: " + client.getCpf()
                        + "\nNome: " + client.getName()
                        + "\nData de Nascimento: " + client.getDateOfBirth()
                        + "\nTelefone: " + client.getPhoneNumber()
                        + "\nE-mail: " + client.getEmail()
                        + "\nSenha: **********"
                        + "\nCategoria: " + client.getClientCategory()
                        + "\nRenda Informada: R$" + client.getGrossMonthlyIncome() + "\n"
        );

        // ENDEREÇO
        System.out.println("Endereço\n"
                + "=============================="
                + "\nRua: " + client.getAddress().getStreetName() + ", " + client.getAddress().getNumber()
                + "\nBairro: " + client.getAddress().getDistrict()
                + "\nCidade: " + client.getAddress().getCity()
                + "\nEstado: " + client.getAddress().getState()
                + "\nCEP: " + client.getAddress().getZipCode()
        );
        if (client.getAddress().getAddressComplement() == null) {
            System.out.println("Complemento: Não informado.\n");
        } else {
            System.out.println("Complemento: " + client.getAddress().getAddressComplement() + "\n");
        }

        //CONTAS
        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        System.out.println("Contas encontradas: \n");

        for (Account account : clientAccountsList) {
            System.out.println(
                    "Número da conta: " + account.getAccountNumber()
                            + "\nTipo da Conta: " + account.getAccountType().getAccountTypeName()
                            + "\nSaldo: " + account.getBalance()
            );
            if (account instanceof CurrentAccount) {
                System.out.println("Taxa mensal: " + ((CurrentAccount) account).getAccountFee());
                System.out.println("Proxima cobranca em: " + LocalDate.now());
                System.out.printf("Valor aproximado cobrado diariamente: R$ %.2f", ((CurrentAccount) account).calculateTaxes());
                System.out.println("\n");
            } else {
                System.out.println("Rendimento anual: " + ((SavingsAccount) account).getAnnualPercentageYield());
                for (int i = 1; i <= 5; i++) {
                    System.out.printf(
                            "Previsão de rendimento em + %d ano(s): R$ %.2f", i, ((SavingsAccount) account).calculateYields(i)
                    );
                    System.out.println("\n");
                }
            }
        }


        //CARTÕES


        //SEGUROS


    }

    private void showTransferMenu(Client client) throws AccountNotFoundException, InvalidValueException, InsufficientFundsException {
        boolean runningTransferMenu = false;
        int transferMenuOption = 0;

        while (!runningTransferMenu) {
            System.out.println("\nBem vindo ao menu de transferencias!"
                    + "\nSelecione uma opção no menu abaixo:"
                    + "\n1 - Transferencia para outro cliente Eightbank"
                    + "\n2 - Pix"
                    + "\n0 - Voltar");

            try {
                transferMenuOption = new Scanner(System.in).nextInt();

                if (transferMenuOption == 0) {
                    System.out.println("Voltando...");
                    break;
                }

                if (transferMenuOption < 0 || transferMenuOption > 2) {
                    System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
                } else {
                    switch (transferMenuOption) {
                        case 1:
                            transferToSameBank(client);
                            break;
                        case 2:
                            transferPix(client);
                            break;
                        case 0:
                            System.out.println("Voltando...");
                            runningTransferMenu = true;
                            break;
                        default:
                            System.err.println(SystemMessages.INVALID_OPTION.getFieldName());
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_OPTION.getFieldName());
            }
        }
    }

    private void transferPix(Client client)
            throws AccountNotFoundException, InvalidValueException, InsufficientFundsException {
        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        boolean runningTransferMenu = false;
        char clientOption = ' ';
        String pixKey;

        while (!runningTransferMenu) {
            if (clientAccountsList.size() > 1) {
                System.out.println("\nVimos aqui que você possui mais de uma conta conosco.\n");

                System.out.println("Número da Conta - Tipo da Conta");
                for (Account account : clientAccountsList) {
                    System.out.println(" - " + account.getAccountNumber()
                            + " - " + account.getAccountType().getAccountTypeName());
                }

                System.out.println("\nPor favor digite o numero da conta que deseja utlizar: ");

                try {
                    long senderAccountNumber = new Scanner(System.in).nextLong();
                    Account senderAccount = null;
                    for (Account account : clientAccountsList) {
                        if (account.getAccountNumber() != senderAccountNumber) {
                            System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                        } else {
                            senderAccount = account;

                            pixKey = inputPixKey();

                            if (pixKey == null) {
                                break;
                            } else {
                                System.out.println("Digite o valor que deseja transferir: ");
                                try {
                                    double value = new Scanner(System.in).nextDouble();
                                    accountService.withdraw(senderAccount.getAccountNumber(), value);
                                    System.out.println(
                                            "Transferencia realizada com sucesso!\n"
                                                    + "O valor de R$ " + value
                                                    + " foi transferido para o PIX: " + pixKey
                                    );
                                    break;
                                } catch (InputMismatchException e) {
                                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                                }
                            }
                        }
                    }
                    if (senderAccount == null) {
                        throw new AccountNotFoundException(
                                "\n"
                                        + AnsiColors.ANSI_RED.getAnsiColorCode()
                                        + "Conta não está na lista!"
                                        + AnsiColors.ANSI_RESET.getAnsiColorCode()
                                        + "\n"
                        );
                    }
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            } else {
                Account clientAccount = clientAccountsList.get(0);

                System.out.println(
                        "\nNúmero da Conta: " + clientAccount.getAccountNumber()
                                + "\nTipo da Conta: " + clientAccount.getAccountType().getAccountTypeName()
                                + "\nSaldo atual: " + clientAccount.getBalance()
                );

                while (true) {
                    pixKey = inputPixKey();

                    if (pixKey == null) {
                        System.err.println("Valor vazio!");
                    } else {
                        break;
                    }
                }

                System.out.println("Digite o valor que deseja transferir: ");
                try {
                    double value = new Scanner(System.in).nextDouble();
                    accountService.withdraw(clientAccount.getAccountNumber(), value);
                    System.out.println(
                            "Transferencia realizada com sucesso!\n"
                                    + "O valor de R$ " + value
                                    + "foi transferido para o PIX: " + pixKey
                    );
                    break;
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                } catch (InsufficientFundsException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("\nDeseja efetuar outra transferencia PIX?(S/N)");
            clientOption = validateClientOptionYesOrNo();
            if (clientOption != 'S') {
                runningTransferMenu = true;
            }
        }
    }

    private String inputPixKey() {
        String pixKey = null;
        boolean inputPixKeyMenu = false;
        int inputPixKeyMenuOption = 0;

        while (true) {
            System.out.println("""
                    Selecione uma opção de chave PIX abaixo:
                    1 - CPF
                    2 - CNPJ
                    3 - E-mail
                    4 - Celular
                    5 - Chave Aleatória
                    0 - Voltar"""
            );

            inputPixKeyMenuOption = new Scanner(System.in).nextInt();
            if (inputPixKeyMenuOption < 0 || inputPixKeyMenuOption > 5) {
                System.err.println(SystemMessages.INVALID_OPTION.getFieldName());
            } else {
                System.out.println("Digite a chave PIX: ");
                pixKey = new Scanner(System.in).nextLine();
                switch (inputPixKeyMenuOption) {
                    case 1:
                        if (!CPFValidator.validateCPF(pixKey)) {
                            System.err.println("Falha ao validar o CPF");
                        }
                        break;
                    case 2:
                        //if(!CNPJValidator.validateCNPJ(pixKey)){
                        //             System.out.println("Falha ao validar o CNPJ");
                        //}
                        pixKey = "CNPJ";
                        break;
                    case 3:
                        if (!EmailValidator.validateEmail(pixKey)) {
                            System.err.println("Falha ao validar o E-mail");
                        }
                        break;
                    case 4:
                        if (!PhoneNumberValidator.validatePhoneNumber(pixKey)) {
                            System.err.println("Falha ao validar o telefone");
                        }
                        break;
                    case 5:
                        pixKey = "Chave aleatória";
                        break;
                    case 0:
                        pixKey = null;
                        break;
                    default:
                        System.err.println(SystemMessages.INVALID_OPTION.getFieldName());
                }
                break;
            }
        }
        return pixKey;
    }

    private void transferToSameBank(Client client) throws AccountNotFoundException {

        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        boolean runningTransferMenu = false;

        while (!runningTransferMenu) {
            if (clientAccountsList.size() > 1) {
                System.out.println("\nVimos aqui que você possui mais de uma conta conosco.\n");

                System.out.println("Número da Conta - Tipo da Conta");
                for (Account account : clientAccountsList) {
                    System.out.println(account.getAccountNumber()
                            + " - " + account.getAccountType().getAccountTypeName());
                }
                System.out.println("\nPressione 0 para voltar");

                System.out.println("\nPor favor digite o numero da conta que deseja utlizar: ");

                try {
                    long senderAccountNumber = new Scanner(System.in).nextLong();

                    if (senderAccountNumber == 0) {
                        break;
                    }

                    Account senderAccount = null;
                    for (Account account : clientAccountsList) {
                        if (account.getAccountNumber() != senderAccountNumber) {
                            System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                        } else {
                            senderAccount = account;

                            System.out.println("Digite o número da conta 'Eightbank' que deseja transferir: ");
                            try {
                                long receiverAccountNumber = new Scanner(System.in).nextLong();

                                if (receiverAccountNumber == senderAccountNumber) {
                                    System.out.println(
                                            AnsiColors.ANSI_RED.getAnsiColorCode()
                                                    + "Digite uma conta diferente da conta de origem!"
                                                    + AnsiColors.ANSI_RESET.getAnsiColorCode()
                                    );
                                    break;
                                } else {
                                    try {
                                        Account receiverAccount = accountService.findAccountByNumber(receiverAccountNumber);
                                        System.out.println("Digite o valor que deseja transferir: ");
                                        try {
                                            double value = new Scanner(System.in).nextDouble();
                                            accountService.withdraw(senderAccount.getAccountNumber(), value);
                                            accountService.deposit(receiverAccount.getAccountNumber(), value);
                                            System.out.println(
                                                    "Transferencia realizada com sucesso!\n"
                                                            + "O valor de R$ " + value
                                                            + "foi transferido para a conta: "
                                                            + receiverAccount.getAccountNumber()
                                            );
                                            break;
                                        } catch (InputMismatchException e) {
                                            System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                                        }
                                    } catch (AccountNotFoundException e) {
                                        System.out.println(
                                                AnsiColors.ANSI_RED
                                                        + "Conta destino não localizada!"
                                                        + AnsiColors.ANSI_RESET
                                        );
                                    } catch (InsufficientFundsException | InvalidValueException e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                            } catch (InputMismatchException e) {
                                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                            }
                        }
                    }

                    if (senderAccount == null) {
                        throw new AccountNotFoundException(
                                "\n"
                                        + AnsiColors.ANSI_RED.getAnsiColorCode()
                                        + "Conta não está na lista!"
                                        + AnsiColors.ANSI_RESET.getAnsiColorCode()
                                        + "\n"
                        );
                    }


                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            } else {
                Account senderAccount = clientAccountsList.get(0);

                System.out.println(
                        "\nNúmero da Conta: " + senderAccount.getAccountNumber()
                                + "\nTipo da Conta: " + senderAccount.getAccountType().getAccountTypeName()
                                + "\nSaldo atual: " + senderAccount.getBalance()
                );

                System.out.println("Digite o número da conta 'Eightbank' que deseja transferir: ");
                try {
                    long receiverAccountNumber = new Scanner(System.in).nextLong();
                    try {
                        Account receiverAccount = accountService.findAccountByNumber(receiverAccountNumber);

                        if (receiverAccountNumber == senderAccount.getAccountNumber()) {
                            System.out.println(
                                    AnsiColors.ANSI_RED.getAnsiColorCode()
                                            + "Digite uma conta diferente da conta de origem!"
                                            + AnsiColors.ANSI_RESET.getAnsiColorCode()
                            );
                            break;
                        } else {
                            System.out.println("Digite o valor que deseja transferir: ");
                            try {
                                double value = new Scanner(System.in).nextDouble();
                                accountService.withdraw(senderAccount.getAccountNumber(), value);
                                accountService.deposit(receiverAccount.getAccountNumber(), value);
                                System.out.println(
                                        "Transferencia realizada com sucesso!\n"
                                                + "O valor de R$ " + value
                                                + " foi transferido para a conta: "
                                                + receiverAccount.getAccountNumber()
                                );
                                break;
                            } catch (InputMismatchException e) {
                                System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                            } catch (InsufficientFundsException | InvalidValueException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } catch (AccountNotFoundException e) {
                        System.out.println(AnsiColors.ANSI_RED + "Conta destino não localizada!" + AnsiColors.ANSI_RESET);
                    }
                } catch (InputMismatchException e) {
                    System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            }

            System.out.println("\nRetornando ao menu de transferencias...\n");
            break;
        }
    }

    private void withdrawFromAccount(Client client) throws AccountNotFoundException {
        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        boolean runningWithdrawFromAccount = false;
        char clientOption = ' ';

        while (!runningWithdrawFromAccount) {
            if (clientAccountsList.size() > 1) {
                System.out.println("\nVimos aqui que você possui mais de uma conta conosco.\n");

                System.out.println("Número da Conta - Tipo da Conta");
                for (Account account : clientAccountsList) {
                    System.out.println(" - " + account.getAccountNumber()
                            + " - " + account.getAccountType().getAccountTypeName());
                }

                System.out.println("\nPor favor digite o numero da conta que deseja efetuar o saque: ");

                try {
                    long accountNumber = new Scanner(System.in).nextLong();

                    Account accountToCheck = null;

                    for (Account account : clientAccountsList) {
                        if (account.getAccountNumber() != accountNumber) {
                            System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                        } else {
                            accountToCheck = accountService.findAccountByNumber(accountNumber);

                            System.out.println("Digite o valor que deseja sacar: ");
                            try {
                                double value = new Scanner(System.in).nextDouble();
                                accountService.withdraw(accountToCheck.getAccountNumber(), value);
                                break;
                            } catch (InputMismatchException e) {
                                System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                            } catch (InsufficientFundsException e) {
                                System.err.println("Saldo insuficiente!");
                            } catch (InvalidValueException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }

                    if (accountToCheck == null) {
                        throw new AccountNotFoundException(
                                "\n"
                                        + AnsiColors.ANSI_RED.getAnsiColorCode()
                                        + "Conta não está na lista!"
                                        + AnsiColors.ANSI_RESET.getAnsiColorCode()
                                        + "\n"
                        );
                    }
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            } else {
                Account clientAccount = clientAccountsList.get(0);

                System.out.println(
                        "\nNúmero da Conta: " + clientAccount.getAccountNumber()
                                + "\nTipo da Conta: " + clientAccount.getAccountType().getAccountTypeName()
                                + "\nSaldo atual: " + clientAccount.getBalance()
                );

                System.out.println("Digite o valor que deseja sacar: ");
                try {
                    double value = new Scanner(System.in).nextDouble();
                    accountService.withdraw(clientAccount.getAccountNumber(), value);
                    System.out.println("Saque realizado com sucesso!");
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                } catch (InsufficientFundsException e) {
                    System.err.println("Saldo insuficiente!");
                } catch (InvalidValueException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("\nDeseja efetuar outro saque?(S/N)");
            clientOption = validateClientOptionYesOrNo();
            if (clientOption != 'S') {
                runningWithdrawFromAccount = true;
            }
        }
    }

    private void depositOnClientAccount(Client client) throws AccountNotFoundException {
        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        boolean runningDepositMenu = false;
        char clientOption = ' ';

        while (!runningDepositMenu) {
            if (clientAccountsList.size() > 1) {
                System.out.println("\nVimos aqui que você possui mais de uma conta conosco.\n");

                System.out.println("Número da Conta - Tipo da Conta");
                for (Account account : clientAccountsList) {
                    System.out.println(
                            account.getAccountNumber()
                                    + " - "
                                    + account.getAccountType().getAccountTypeName());
                }
                System.out.println("0 - Voltar");

                System.out.println("\nPor favor digite o numero da conta que deseja efetuar o depósito: ");

                try {
                    long accountNumber = new Scanner(System.in).nextLong();

                    if (accountNumber == 0) {
                        System.out.println("Voltando...\n");
                        break;
                    }

                    Account accountToCheck = null;

                    for (Account account : clientAccountsList) {
                        if (account.getAccountNumber() != accountNumber) {
                            System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                        } else {
                            accountToCheck = accountService.findAccountByNumber(accountNumber);

                            System.out.println("Digite o valor que deseja depositar: ");
                            try {
                                double value = new Scanner(System.in).nextDouble();
                                accountService.deposit(accountToCheck.getAccountNumber(), value);
                                break;
                            } catch (InputMismatchException e) {
                                System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                            }
                        }
                    }

                    if (accountToCheck == null) {
                        throw new AccountNotFoundException(
                                "\n"
                                        + AnsiColors.ANSI_RED.getAnsiColorCode()
                                        + "Conta não está na lista!"
                                        + AnsiColors.ANSI_RESET.getAnsiColorCode()
                                        + "\n"
                        );
                    }
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            } else {
                Account clientAccount = clientAccountsList.get(0);

                System.out.println(
                        "\nNúmero da Conta: " + clientAccount.getAccountNumber()
                                + "\nTipo da Conta: " + clientAccount.getAccountType().getAccountTypeName()
                                + "\nSaldo atual: " + clientAccount.getBalance()
                );

                System.out.println("\nDigite o valor que deseja depositar: ");
                try {
                    double value = new Scanner(System.in).nextDouble();
                    accountService.deposit(clientAccountsList.get(0).getAccountNumber(), value);
                    System.out.println("Depósito efetuado com sucesso!\n");
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            }
            System.out.println("Deseja efetuar outro depósito?(S/N)");
            clientOption = validateClientOptionYesOrNo();
            if (clientOption != 'S') {
                break;
            }
        }
    }

    private void getClientBalance(Client client) throws AccountNotFoundException {
        clientAccountsList = accountService.findAccountsByCPF(client.getCpf());

        boolean runningGetClientBalanceMenu = false;
        char clientOption = ' ';

        while (!runningGetClientBalanceMenu) {
            if (clientAccountsList.size() > 1) {

                System.out.println("\nVimos aqui que você possui mais de uma conta conosco.\n");

                System.out.println("Número da Conta - Tipo da Conta");
                for (Account account : clientAccountsList) {
                    System.out.println(" - " + account.getAccountNumber()
                            + " - " + account.getAccountType().getAccountTypeName());
                }

                System.out.println("\nPor favor digite o numero da conta que deseja visualizar o saldo: ");

                try {
                    long accountNumber = new Scanner(System.in).nextLong();

                    Account accountToCheck = null;

                    for (Account account : clientAccountsList) {
                        if (account.getAccountNumber() != accountNumber) {
                            System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                        } else {
                            accountToCheck = accountService.findAccountByNumber(accountNumber);

                            System.out.println(
                                    "\nConta selecionada: "
                                            + accountToCheck.getAccountNumber()
                                            + " - " + accountToCheck.getAccountType().getAccountTypeName()
                                            + "\n"
                                            + "Saldo atual: R$ " + accountToCheck.getBalance()
                            );
                            break;
                        }
                    }

                    if (accountToCheck == null) {
                        throw new AccountNotFoundException("Conta não está na lista!");
                    }
                } catch (InputMismatchException e) {
                    System.err.println(SystemMessages.INVALID_CHARACTER.getFieldName());
                }
            } else {
                Account clientAccount = clientAccountsList.get(0);

                System.out.println(
                        "\nNúmero da Conta: " + clientAccount.getAccountNumber()
                                + "\nTipo da Conta: " + clientAccount.getAccountType().getAccountTypeName()
                                + "\nSaldo atual: " + clientAccount.getBalance()
                );
            }
            System.out.println("\nDeseja continuar visualizando saldos?(S/N)");
            clientOption = validateClientOptionYesOrNo();
            if (clientOption != 'S') {
                runningGetClientBalanceMenu = true;
            }
        }
    }

    public char validateClientOptionYesOrNo() {
        char clientOption = ' ';

        while (true) {
            System.out.println("Digite uma opção válida, por favor: ");
            try {
                clientOption = new Scanner(System.in).next().charAt(0);
                if (clientOption == 's' || clientOption == 'S') {
                    clientOption = 'S';
                    break;
                } else if (clientOption == 'n' || clientOption == 'N') {
                    clientOption = 'N';
                    break;
                } else {
                    System.out.println("Opção inválida!");
                }
            } catch (InputMismatchException e) {
                System.out.println(SystemMessages.INVALID_CHARACTER.getFieldName());
            }
        }

        return clientOption;
    }

    public void importClientsFromFile(String fileName) throws NoSuchAlgorithmException, InvalidKeySpecException {
        while (true) {
            //System.out.println("Digite o nome do arquivo:");
            //String fileName = new Scanner(System.in).nextLine();
            if (!fileName.isEmpty()) {
                if (!FileNameValidator.validateFileName(fileName)) {
                    System.err.println("Formato invalido!");
                } else {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new FileReader(fileName));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] fields = line.split(",");
                            String cpf = fields[0];
                            double grossMonthlyIncome = Double.parseDouble(fields[1]);
                            String email = fields[2];
                            String name = fields[3];

                            String dateOfBirth = fields[4];
                            String[] dateOfBirthFields = dateOfBirth.split("/");
                            int dayOfMonth = Integer.parseInt(dateOfBirthFields[0]);
                            int month = Integer.parseInt(dateOfBirthFields[1]);
                            int year = Integer.parseInt(dateOfBirthFields[2]);
                            LocalDate dateOfBirthConverted = LocalDate.of(year, month, dayOfMonth);

                            Address address = new Address(
                                    fields[5],
                                    Long.parseLong(fields[6]),
                                    fields[7],
                                    fields[8],
                                    fields[9],
                                    fields[10]
                            );

                            String phoneNumber = fields[11];
                            String passwordString = PasswordService.generateStrongPassword(fields[12]);

                            clientCategory = checkClientCategory(grossMonthlyIncome);

                            client = new Client(
                                    email,
                                    passwordString,
                                    name,
                                    cpf,
                                    dateOfBirthConverted,
                                    address,
                                    clientCategory,
                                    phoneNumber,
                                    grossMonthlyIncome
                            );

                            clientDAO.addClient(client);

                            registerClientAccounts(client, 3);

                            //Lista os usuários cadastrados, à partir do método toString
                            //System.out.println("Clientes\n");
                            //clientDAO.listClients();

                            //Listar contas e titulares
                            //System.out.println("\n Clientes e contas");
                            //accountService.listAccounts();
                        }
                        reader.close();
                    } catch (IOException e) {
                        System.err.println("Erro ao carregar o arquivo: " + fileName);
                        break;
                    }
                    System.out.println("Usuários importados com sucesso!");
                    break;
                }
            } else {
                System.err.println("Digite o nome do arquivo!");
            }
            break;
        }
    }

    public List<Client> getClients() {
        return clientDAO.listAll();
    }

    public List<Account> listClientAccounts(String cpf) {
        List<Account> foundAccountsList = null;

        try {
            foundAccountsList = accountService.findAccountsByCPF(cpf);
        } catch (AccountNotFoundException e) {
            System.out.println("Não foram localizadas contas para este CPF!");
        }
        return foundAccountsList;
    }
}