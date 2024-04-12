package br.com.cdb.java.grupo4.eightbankspring.dao.impl;

import br.com.cdb.java.grupo4.eightbankspring.dao.IJdbcTemplateDAO;
import br.com.cdb.java.grupo4.eightbankspring.model.account.Account;
import br.com.cdb.java.grupo4.eightbankspring.model.account.CurrentAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.account.SavingsAccount;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
public class JdbcTemplateDAOImpl implements IJdbcTemplateDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveClient(Client client) {

        try {
            String sql = "SELECT public.save_client (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.execute(sql, (PreparedStatementCallback<Void>) preparedStatement -> {
                preparedStatement.setString(1, client.getCpf());
                preparedStatement.setString(2, client.getName());
                preparedStatement.setString(3, client.getEmail());
                preparedStatement.setString(4, client.getPassword());
                preparedStatement.setDate(5, Date.valueOf(client.getDateOfBirth()));
                preparedStatement.setString(6, String.valueOf(client.getClientCategory()));
                preparedStatement.setString(7, client.getPhoneNumber());
                preparedStatement.setBigDecimal(8, BigDecimal.valueOf(client.getGrossMonthlyIncome()));
                preparedStatement.setString(9, client.getAddress().getStreetName());
                preparedStatement.setInt(10, Integer.parseInt(String.valueOf(client.getAddress().getNumber())));
                preparedStatement.setString(11, client.getAddress().getDistrict());
                preparedStatement.setString(12, client.getAddress().getCity());
                preparedStatement.setString(13, client.getAddress().getState());
                preparedStatement.setString(14, client.getAddress().getZipCode());
                preparedStatement.setString(15, client.getAddress().getAddressComplement());
                preparedStatement.execute();

                return null;
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void saveSavingsAccount(String ownerCpf, SavingsAccount savingsAccount) {
        try {
            String sql = "SELECT public.save_savings_account (?, ?, ?, ?)";

            jdbcTemplate.execute(sql, (PreparedStatementCallback<Void>) preparedStatement -> {
                preparedStatement.setString(1, ownerCpf);
                preparedStatement.setBigDecimal(2, BigDecimal.valueOf(savingsAccount.getBalance()));
                preparedStatement.setString(3, savingsAccount.getAccountType().getAccountTypeName());
                preparedStatement.setBigDecimal(4, BigDecimal.valueOf(savingsAccount.getAnnualPercentageYield()));
                preparedStatement.execute();

                return null;
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void saveCurrentAccount(String ownerCpf, CurrentAccount currentAccount) {
        try {
            String sql = "SELECT public.save_current_account (?, ?, ?, ?)";

            jdbcTemplate.execute(sql, (PreparedStatementCallback<Void>) preparedStatement -> {
                preparedStatement.setString(1, ownerCpf);
                preparedStatement.setBigDecimal(2, BigDecimal.valueOf(currentAccount.getBalance()));
                preparedStatement.setString(3, currentAccount.getAccountType().getAccountTypeName());
                preparedStatement.setBigDecimal(4, BigDecimal.valueOf(currentAccount.getAccountFee()));
                preparedStatement.execute();

                return null;
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Client> listAllClients() {
        List<Client> clientsList = null;
        try {
            String sql = "SELECT * FROM CLIENT";
            clientsList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(Client.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return clientsList;
    }

    @Override
    public List<Account> findAccountsByCpf(String cpf) {
        List<Account> accountsList = null;
        try {
            String sql = "SELECT public.show_client_accounts('" + cpf + "')";

            accountsList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(Account.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return accountsList;
    }

}
