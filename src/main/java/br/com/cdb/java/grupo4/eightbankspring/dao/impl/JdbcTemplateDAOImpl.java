package br.com.cdb.java.grupo4.eightbankspring.dao.impl;

import br.com.cdb.java.grupo4.eightbankspring.dao.IJdbcTemplateDAO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.CurrentAccountDTO;
import br.com.cdb.java.grupo4.eightbankspring.dtos.SavingsAccountDTO;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.InvalidValueException;
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
import java.util.Objects;

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
    public List<SavingsAccountDTO> findSavingsAccountByCpf(String cpf) {
        List<SavingsAccountDTO> clientAccountsList = null;

        try{
            String sql = "SELECT * FROM SAVINGS_ACCOUNT WHERE OWNER_CPF = ('"+ cpf +"')";

            clientAccountsList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(SavingsAccountDTO.class));

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return clientAccountsList;
    }

    @Override
    public List<CurrentAccountDTO> findCurrentAccountByCpf(String cpf) {
        List<CurrentAccountDTO> clientAccountsList = null;

        try{
            String sql = "SELECT * FROM CURRENT_ACCOUNT WHERE OWNER_CPF = ('"+ cpf +"')";

            clientAccountsList = jdbcTemplate.query(
                    sql,
                    new BeanPropertyRowMapper<>(CurrentAccountDTO.class));

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return clientAccountsList;
    }

    @Override
    public void depositValue(long accountNumber, double value, String accountType) throws InvalidValueException {

        if(Objects.equals(accountType, "CP")){
            try{
                String sql = "SELECT public.update_savings_account_balance(" + accountNumber + "," + value + ");";

                jdbcTemplate.execute(sql);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }

        } else if(Objects.equals(accountType, "CC")){
            try{
                String sql = "SELECT public.update_current_account_balance(" + accountNumber + "," + value + ");";

                jdbcTemplate.execute(sql);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }

        } else {
            throw new InvalidValueException("Tipo de conta inválido!");
        }


    }

}
