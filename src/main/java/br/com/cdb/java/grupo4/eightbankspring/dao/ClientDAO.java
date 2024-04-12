package br.com.cdb.java.grupo4.eightbankspring.dao;

import br.com.cdb.java.grupo4.eightbankspring.enuns.SystemMessages;
import br.com.cdb.java.grupo4.eightbankspring.exceptions.ClientNotFoundException;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.usecase.PasswordService;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ClientDAO {

    List<Client> clientList;

    public ClientDAO() {
        this.clientList = new ArrayList<>();
    }

    public void save(Client client){
        client.setId(clientList.size() + 1);
        clientList.add(client);
    }

    public List<Client> listAll(){
        return this.clientList;
    }



    public void addClient(Client client) {
        client.setId(clientList.size() + 1);
        clientList.add(client);
    }

    public void listClients() {
        for (Client client : this.clientList) {
            if (client instanceof Client) {
                System.out.println(((Client) client));
            }
        }
    }

    public Client searchClientByEmail(String email) {
        for (Client client : clientList) {
            if (Objects.equals(client.getEmail(), email)) {
                return client;
            }
        }
        return null;
    }

    public boolean findClientByEmail(String email) {
        for (Client client : this.clientList) {
            if (client.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public Client searchClientById(int id) {
        for (Client client : clientList) {
            if (client.getId() == id) {
                return client;
            }
        }
        return null;
    }

    public boolean verifyClientPassword(String cpf, String inputPassword) {
        for (Client client : clientList) {
            if (client.getCpf().equals(cpf)) {
                try {
                    // Usa PasswordService para validar a senha
                    if (PasswordService.validatePassword(inputPassword, client.getPassword())) {
                        return true;
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public boolean searchClientByCPF(String cpf) {
        boolean finderStatus = false;

        for (Client client : clientList) {

            if (!client.getCpf().equals(cpf)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                finderStatus = true;
                break;
            }

        }
        return finderStatus;
    }

    public void updateClientProfileName(Client client, String value)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.setName(value);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }

    }

    public void updateClientProfileEmail(Client client, String value)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.setEmail(value);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }

    }

    public void updateClientProfilePassword(Client client, String value)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.setPassword(value);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfilePhoneNumber(Client client, String value)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.setPhoneNumber(value);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfileGrossMonthlyIncome(Client client, double value)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.setGrossMonthlyIncome(value);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }

    }

    public void updateClientProfileAddressStreetName(Client client, String newStreetName)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setStreetName(newStreetName);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }

    }

    public void updateClientProfileAddressNumber(Client client, long newNumber)
            throws ClientNotFoundException {
        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setNumber(newNumber);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfileAddressDistrictName(Client client, String newDistrictName)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setDistrict(newDistrictName);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfileAddressCityName(Client client, String newCityName)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setCity(newCityName);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfileAddressStateName(Client client, String newStateName)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setState(newStateName);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfileAddressCep(Client client, String newZipCode)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setZipCode(newZipCode);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }

    public void updateClientProfileAddressComplement(Client client, String newAddressComplement)
            throws ClientNotFoundException {

        boolean clientSearchResult = false;

        for (Client c : this.clientList) {
            if (!c.equals(client)) {
                System.out.println(SystemMessages.PROCESSING_PT_BR.getFieldName());
            } else {
                clientSearchResult = true;
                c.getAddress().setAddressComplement(newAddressComplement);
            }
        }

        if (!clientSearchResult) {
            throw new ClientNotFoundException("Não encontramos seu cadastro :/");
        }
    }
}
