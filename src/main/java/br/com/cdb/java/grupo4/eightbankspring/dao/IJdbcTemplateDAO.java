package br.com.cdb.java.grupo4.eightbankspring.dao;

import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;

public interface IJdbcTemplateDAO {
    void saveClient(Client client);
}
