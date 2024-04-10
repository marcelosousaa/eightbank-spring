package br.com.cdb.java.grupo4.eightbankspring.dao;

import br.com.cdb.java.grupo4.eightbankspring.model.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardDAO {
    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    // Assume que Card agora tem um campo 'clientCPF'
    public List<Card> findCardsByClientCPF(String cpf) {
        return cards.stream()
                .filter(card -> card.getClientCPF().equals(cpf))
                .collect(Collectors.toList());
    }

    public boolean updateCardStatus(String cpf, boolean isActive) {
        List<Card> cardsByCpf = findCardsByClientCPF(cpf);
        if (cardsByCpf.isEmpty()) {
            return false;
        }
        cardsByCpf.forEach(card -> card.setActive(isActive));
        return true;
    }

    public boolean removeCard(String number) {
        return cards.removeIf(card -> card.getNumber().equals(number));
    }
}
