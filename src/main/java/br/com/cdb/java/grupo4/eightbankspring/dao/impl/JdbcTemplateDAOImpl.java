package br.com.cdb.java.grupo4.eightbankspring.dao.impl;

import br.com.cdb.java.grupo4.eightbankspring.dao.IJdbcTemplateDAO;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JdbcTemplateDAOImpl implements IJdbcTemplateDAO {

    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveClient(Client client){

        try{
            String sql = "SELECT  public.save_client(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.execute(sql, (PreparedStatementCallback<Void>) preparedStatement -> {
                preparedStatement.setString(1, client.getCpf());
                preparedStatement.setString(2, client.getEmail());
                preparedStatement.setString(3, client.getPassword());
                preparedStatement.setString(4, client.getDateOfBirth().toString());
                preparedStatement.setString(5, client.getClientCategory().toString());
                preparedStatement.setString(6, client.getPhoneNumber());
                preparedStatement.setDouble(7, client.getGrossMonthlyIncome());
                preparedStatement.setString(8, client.getAddress().getStreetName());
                preparedStatement.setLong(9, client.getAddress().getNumber());
                preparedStatement.setString(10, client.getAddress().getDistrict());
                preparedStatement.setString(11, client.getAddress().getCity());
                preparedStatement.setString(12, client.getAddress().getState());
                preparedStatement.setString(13, client.getAddress().getZipCode());
                preparedStatement.setString(14, client.getAddress().getAddressComplement());

                return null;
            });

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
