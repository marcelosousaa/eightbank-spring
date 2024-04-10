package br.com.cdb.java.grupo4.eightbankspring.controller;

import br.com.cdb.java.grupo4.eightbankspring.dtos.ClientDTO;
import br.com.cdb.java.grupo4.eightbankspring.model.client.Client;
import br.com.cdb.java.grupo4.eightbankspring.usecase.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")

public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/add")
    public void addClient(@RequestBody ClientDTO clientDTO){
        Client convertedClient = convertToEntity(clientDTO);
        clientService.addClient(convertedClient);
    }

    private Client convertToEntity(ClientDTO clientDTO){
        return modelMapper.map(clientDTO, Client.class);
    }

    @GetMapping("/all")
    public List<Client> getAllClients(){
        return clientService.getClients();
    }




}
