package br.com.cdb.java.grupo4.eightbankspring.usecase;

import br.com.cdb.java.grupo4.eightbankspring.dao.AccountDAO;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.AccountNotFoundException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InsufficientFundsException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InvalidValueException;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.account.CurrentAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.account.SavingsAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountDAO accountDAO;

    public Account createCurrentAccount(String ownerCpf, double accountFee) {
        double balance = 0;
        Account currentAccount = new CurrentAccount(balance, ownerCpf, accountFee);
        accountDAO.addAccount(currentAccount);
        return currentAccount;
    }

    public Account createSavingsAccount(String ownerCpf, double annualPercentageYield) {
        double balance = 0;
        Account savingsAccount = new SavingsAccount(balance, ownerCpf, annualPercentageYield);
        accountDAO.addAccount(savingsAccount);
        return savingsAccount;
    }

    public void setAccountOwner(Account account, String cpf) {
        accountDAO.setAccountOwner(account, cpf);
    }

    public void withdraw(long accountNumber, double value) throws InsufficientFundsException, InvalidValueException, AccountNotFoundException {
        accountDAO.withdrawValue(accountNumber, value);
    }

    public void deposit(long accountNumber, double value) {
        try {
            accountDAO.depositValue(accountNumber, value);
        } catch (AccountNotFoundException | InvalidValueException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transferSameBank(long originAccountNumber, long targetAccountNumber, double value) {
        try {
            accountDAO.withdrawValue(originAccountNumber, value);
            try {
                accountDAO.depositValue(targetAccountNumber, value);
            } catch (InvalidValueException e) {
                System.out.println(e.getMessage());
            }
        } catch (InsufficientFundsException | InvalidValueException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transferPix(long accountNumber, String pixKey, double value) {
        try {
            accountDAO.withdrawValue(accountNumber, value);
        } catch (InsufficientFundsException | InvalidValueException | AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listAccounts() {
        accountDAO.listAccounts();
    }

    public List<Account> findAccountsByCPF(String cpf) throws AccountNotFoundException {
        return accountDAO.searchAccountByCpf(cpf);
    }

    public Account findAccountByNumber(long accountNumber) throws AccountNotFoundException {
        return accountDAO.findAccountByNumber(accountNumber);
    }
}
