package br.com.cdb.java.grupo4.eightbankspring.dao;

import br.com.cdb.java.grupo4.eightbankspring.enuns.AnsiColors;
import br.com.cdb.java.grupo4.eightbankspring.enuns.SystemMessages;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.AccountNotFoundException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InsufficientFundsException;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InvalidValueException;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    List<Account> accountList = new ArrayList<>();

    public void listAccounts() {
        for (Account account : accountList) {
            System.out.println(account);
            System.out.println(account.getOwnerCPF());
        }
    }

    public void addAccount(Account account) {
        account.setAccountNumber(accountList.size() + 1);
        accountList.add(account);
    }

    public boolean setAccountOwner(Account account, String ownerCPF) {
        for (Account a : accountList) {
            if (a.equals(account)) {
                account.setOwnerCPF(ownerCPF);
                return true;
            }
        }
        return false;
    }

    public Account findAccountByNumber(long accountNumber) throws AccountNotFoundException {
        Account account = null;

        for (Account a : this.accountList) {
            if (a.getAccountNumber() == accountNumber) {
                account = a;
                break;
            }
        }

        if (account == null) {
            throw new AccountNotFoundException("\n"
                    + AnsiColors.ANSI_RED.getAnsiColorCode()
                    + "Conta não localizada!"
                    + AnsiColors.ANSI_RESET.getAnsiColorCode()
                    + "\n");
        } else {
            return account;
        }
    }

    public void withdrawValue(long accountNumber, double value)
            throws InsufficientFundsException, InvalidValueException, AccountNotFoundException {
        boolean validateAccountSearch = false;
        double balance = 0;
        if (value >= 0) {
            for (Account a : this.accountList) {
                if (a.getAccountNumber() != accountNumber) {
                    System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                } else if (a.getBalance() >= value) {
                    validateAccountSearch = true;
                    a.setBalance(a.getBalance() - value);
                    balance = a.getBalance();
                    break;
                } else {
                    throw new InsufficientFundsException("Saldo Insuficiente!");
                }
            }

            if (!validateAccountSearch) {
                throw new AccountNotFoundException("Conta de origem não localizada!");
            }

        } else {
            throw new InvalidValueException("Valor inválido!");
        }
    }

    public void depositValue(long accountNumber, double value) throws InvalidValueException, AccountNotFoundException {
        boolean validateAccountSearch = false;
        if (value > 0) {
            for (Account a : this.accountList) {
                if (a.getAccountNumber() != accountNumber) {
                    System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
                } else {
                    validateAccountSearch = true;
                    a.setBalance(a.getBalance() + value);
                    break;
                }
            }
        } else {
            throw new InvalidValueException(
                    AnsiColors.ANSI_RED.getAnsiColorCode()
                            + SystemMessages.INVALID_VALUE.getFieldName()
                            + AnsiColors.ANSI_RESET.getAnsiColorCode()
            );
        }

        if (!validateAccountSearch) {
            throw new AccountNotFoundException(
                    AnsiColors.ANSI_RED.getAnsiColorCode()
                            + "Conta não localizada!"
                            + AnsiColors.ANSI_RESET.getAnsiColorCode()
            );
        }
    }

    public double checkBalance(long accountNumber) throws AccountNotFoundException {
        boolean validateAccountSearch = false;
        double balance = 0;
        for (Account a : this.accountList) {
            if (a.getAccountNumber() != accountNumber) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                validateAccountSearch = true;
                balance = a.getBalance();
                break;
            }
        }
        if (!validateAccountSearch) {
            throw new AccountNotFoundException("Conta não localizada!");
        } else {
            return balance;
        }
    }

    public List<Account> searchAccountByCpf(String cpf) throws AccountNotFoundException {
        List<Account> clientAccountsList = new ArrayList<>();

        for (Account a : this.accountList) {
            if (a.getOwnerCPF().equals(cpf)) {
                clientAccountsList.add(a);
            }
        }

        if (clientAccountsList.isEmpty()) {
            throw new AccountNotFoundException("Não foram localizadas contas para o seu CPF!");
        } else {
            return clientAccountsList;
        }
    }
}
