package br.com.cdb.java.grupo4.eightbankspring.dao;

import br.com.cdb.java.grupo4.eightbankspring.dtos.CurrentAccountDTO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.SavingsAccountDTO;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.account.CurrentAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.account.SavingsAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IJdbcTemplateDAO {
    void saveClient(Client client);

    void saveSavingsAccount(String ownerCpf, SavingsAccount savingsAccount);

    void saveCurrentAccount(String ownerCpf, CurrentAccount currentAccount);

    List<SavingsAccountDTO> findSavingsAccountByCpf(String cpf);
    List<CurrentAccountDTO> findCurrentAccountByCpf(String cpf);

    void depositValue(long accountNumber, double value, String accountType);
}
