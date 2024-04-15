package br.com.cdb.java.grupo4.eightbankspring.usecase;

import br.com.cdb.java.grupo4.eightbankspring.dao.CardDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.ClientDAO;
import br.com.cdb.java.grupo4.eightbankspring.dao.impl.JdbcTemplateDAOImpl;
import br.com.cdb.java.grupo4.eightbankspring.dtos.AddressDTO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.enuns.ClientCategory;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.account.CurrentAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.account.SavingsAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Address;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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

    public void addClient(ClientDTO clientDTO) {

        //Validations
        //Email validator
        if (!EmailValidator.validateEmail(clientDTO.getEmail())) {
            throw new IllegalArgumentException("Email Inválido"); //Validador OK
        }

        //CPF validator
        if (!CpfValidator.validateCPF(clientDTO.getCpf())) {
            throw new IllegalArgumentException("CPF Inválido"); //ValIdador OK
        }

        //Name validator
       if(!NameValidator.validateName(clientDTO.getName())){
            throw new IllegalArgumentException("Nome Inválido");
       }

       //Date of Birth validator
       if (!DateOfBirthValidator.validateDateOfBirth(clientDTO.getDateOfBirth())) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
       }

       if (!DateOfBirthValidator.isOfLegalAge(clientDTO.getDateOfBirth())) {
            throw new IllegalArgumentException("Cadastro permitido somente para maiores de 18 anos.");
       }

       LocalDate convertedDateOfBirth = convertDateOfBirth(clientDTO.getDateOfBirth());

       //Phone Number validator
       if (!PhoneNumberValidator.validatePhoneNumber(clientDTO.getPhoneNumber())) {
            throw new IllegalArgumentException("Número de celular Inválido");
       }

       //Zip Code validator
       if (!ZipCodeValidator.validateZipCode(clientDTO.getAddress().getZipCode())){
            throw new IllegalArgumentException("CEP Inválido");
       }

        String passwordString = " ";
        try {
            passwordString = PasswordService.generateStrongPassword(clientDTO.getPassword());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Address address = clientDTO.getAddress();
        double grossMonthlyIncome = clientDTO.getGrossMonthlyIncome();
        ClientCategory clientCategory = checkClientCategory(clientDTO.getGrossMonthlyIncome());

        Client client = new Client(
                clientDTO.getEmail(),
                passwordString,
                clientDTO.getName(),
                clientDTO.getCpf(),
                convertedDateOfBirth,
                address,
                clientCategory,
                clientDTO.getPhoneNumber(),
                grossMonthlyIncome
        );

        //SALVA NO BANCO
        jdbcTemplateDAOImpl.saveClient(client);

        //REGISTRA AS CONTAS e SALVA NO BANCO
        registerClientAccounts(client);

    }

    private LocalDate convertDateOfBirth(String dateOfBirth) {
        LocalDate convertedDob = null;
        BufferedReader reader;

        try{
            reader = new BufferedReader(new StringReader(dateOfBirth));

            while ((dateOfBirth = reader.readLine()) != null) {
                String[] dateOfBirthFields = dateOfBirth.split("-");
                int year = Integer.parseInt(dateOfBirthFields[0]);
                int month = Integer.parseInt(dateOfBirthFields[1]);
                int dayOfMonth = Integer.parseInt(dateOfBirthFields[2]);
                convertedDob = LocalDate.of(year, month, dayOfMonth);
            }

        } catch (IOException e){
            throw new RuntimeException();
        }

        return convertedDob;

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