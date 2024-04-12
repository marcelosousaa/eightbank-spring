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
import br.com.cdb.java.grupo4.eightbankspring.utils.DateOfBirthValidator;
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

    public void addClient(Client client) {
        //Validations

        String email = client.getEmail();

        String passwordString = " ";
        try {
            passwordString = PasswordService.generateStrongPassword(client.getPassword());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        String name = client.getName();
        String cpf = client.getCpf();
        LocalDate dateOfBirth = client.getDateOfBirth();
        Address address = client.getAddress();
        String phoneNumber = client.getPhoneNumber();
        double grossMonthlyIncome = client.getGrossMonthlyIncome();
        ClientCategory clientCategory = checkClientCategory(client.getGrossMonthlyIncome());

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