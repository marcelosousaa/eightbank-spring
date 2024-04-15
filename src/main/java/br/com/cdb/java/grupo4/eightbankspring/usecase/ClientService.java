package br.com.cdb.java.grupo4.eightbankspring.usecase;

import br.com.cdb.java.grupo4.eightbankspring.dao.CardDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.ClientDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.impl.JdbcTemplateDAOImpl;
import br.com.cdb.java.grupo4.eightbankspring.enuns.ClientCategory;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.account.CurrentAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.account.SavingsAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Address;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class ClientService {

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
    @Autowired
    private final EmailValidator emailValidator;
    @Autowired
    private final CPFValidator cpfValidator;
    @Autowired
    private final NameValidator nameValidator;
    @Autowired
    private final DateOfBirthValidator dateOfBirthValidator;
    @Autowired
    private final PhoneNumberValidator phoneNumberValidator;
    @Autowired
    private final ZipCodeValidator zipCodeValidator;

    @Autowired
    public ClientService(EmailValidator emailValidator, CPFValidator cpfValidator, NameValidator nameValidator,
                         DateOfBirthValidator dateOfBirthValidator, PhoneNumberValidator phoneNumberValidator,
                         ZipCodeValidator zipCodeValidator){
        this.emailValidator = emailValidator;
        this.cpfValidator = cpfValidator;
        this.nameValidator = nameValidator;
        this.dateOfBirthValidator = dateOfBirthValidator;
        this.phoneNumberValidator = phoneNumberValidator;
        this.zipCodeValidator = zipCodeValidator;
    }

    public void addClient(Client client) {
        //Validations
        //Email validator
        if (!emailValidator.validateEmail(client.getEmail())) {
            throw new IllegalArgumentException("Email Inválido"); //Validador OK
        }

        //CPF validator
        if (!cpfValidator.validateCPF(client.getCpf())) {
            throw new IllegalArgumentException("CPF Inválido"); //ValIdador OK
        }

        //Name validator
       if(!nameValidator.validateName(client.getName())){
            throw new IllegalArgumentException("Nome Inválido");
       }

       //Date of Birth validator
       if (!dateOfBirthValidator.validateDateOfBirth(client.getDateOfBirth().toString())) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
       }

       if (!dateOfBirthValidator.isOfLegalAge(client.getDateOfBirth().toString())) {
            throw new IllegalArgumentException("Cadastro permitido somente para maiores de 18 anos.");
       }

       //Phone Number validator
       if (!phoneNumberValidator.validatePhoneNumber(client.getPhoneNumber())) {
            throw new IllegalArgumentException("Número de celular Inválido");
       }

       //Zip Code validator
       if (!zipCodeValidator.validateZipCode(client.getAddress().getZipCode())){
            throw new IllegalArgumentException("CEP Inválido");
       }

        String passwordString = " ";
        try {
            passwordString = PasswordService.generateStrongPassword(client.getPassword());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Address address = client.getAddress();
        double grossMonthlyIncome = client.getGrossMonthlyIncome();
        ClientCategory clientCategory = checkClientCategory(client.getGrossMonthlyIncome());

        client = new Client(
                client.getEmail(),
                passwordString,
                client.getName(),
                client.getCpf(),
                client.getDateOfBirth(),
                address,
                clientCategory,
                client.getPhoneNumber(),
                grossMonthlyIncome
        );

        //SALVA NO BANCO
        jdbcTemplateDAOImpl.saveClient(client);

        //REGISTRA AS CONTAS e SALVA NO BANCO
        registerClientAccounts(client);

    }

    private void registerClientAccounts(Client client) {
        // Cria a(s) conta(s) do cliente e devolve para um ArrayList
        List<Account> clientAccountsList = clientAccountsRegistration(client);

        //Salva as contas no banco de dados
        for (Account account : clientAccountsList) {
            if (account instanceof SavingsAccount) {
                jdbcTemplateDAOImpl.saveSavingsAccount(client.getCpf(), (SavingsAccount) account);
            } else {
                jdbcTemplateDAOImpl.saveCurrentAccount(client.getCpf(), (CurrentAccount) account);
            }
        }
    }

    public List<Client> getClients() {
        return jdbcTemplateDAOImpl.listAllClients();
    }


    private List<Account> clientAccountsRegistration(Client client) {
        Account account;
        List<Account> accountList = new ArrayList<>();
        double annualPercentageYield = checkAnnualPercentageYield(client);
        double currentAccountMonthlyFee = checkCurrentAccountMonthlyFee(client);

        account = accountService.createSavingsAccount(client.getCpf(), annualPercentageYield);
        accountList.add(account);
        account = accountService.createCurrentAccount(client.getCpf(), currentAccountMonthlyFee);
        accountList.add(account);

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

    public List<Account> showClientAccounts(String cpf) {
        return jdbcTemplateDAOImpl.findAccountsByCpf(cpf);
    }
}