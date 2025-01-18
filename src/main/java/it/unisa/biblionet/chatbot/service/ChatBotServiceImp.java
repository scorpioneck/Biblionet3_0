package it.unisa.biblionet.chatbot.service;


import it.unisa.biblionet.model.dao.GenereDAO;
import it.unisa.biblionet.model.dao.chatbot.ChatBotDAO;
import it.unisa.biblionet.model.dao.chatbot.DomandaDAO;
import it.unisa.biblionet.model.dao.chatbot.RispostaDAO;
import it.unisa.biblionet.model.entity.chatbot.ChatBot;
import it.unisa.biblionet.model.entity.chatbot.Risposta;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.chatbot.Domanda;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import it.unisa.biblionet.utils.mapper.CategoriaGenereMapper;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatBotServiceImp implements ChatBotService{

    private final ChatBotDAO chatDAO;

    private final RispostaDAO rispostaDAO;

    private final DomandaDAO domandaDAO;

    private final GenereDAO genereDAO;

    private final CategoriaGenereMapper categoriaGenereMapper;


    /**
     * implementazione della funzionlità di trovaUtente
     * @param id
     * @return chatBot
     */

    @Override
    public ChatBot identificaUtente(final int id) {
        return chatDAO.findById(id).orElseThrow(() -> new EntityNotFoundException("ChatBot not found"));
    }


    @Override
    public Risposta trovaRispostaById(int idRisposta) {
       return rispostaDAO.findById(idRisposta).orElseThrow(
               () -> new EntityNotFoundException("Risposta non trovata" + idRisposta));
    }

    /**Implementa la funzionalità di visualizzare tutte le risposte alle domande
     * @return tutte le risposte
     */

    @Override
    public List<Risposta> visualizzaAllRisposta() {
        return rispostaDAO.findAllRisposta();
    }

    /**
     * Implementa la funzionlità di calcola il genere Preferito della persona
     * @param risposteId una lista di interi
     * @return domandaRisposta
     */

    @Override
    public Genere calcolaGenerePreferito(List<Integer> risposteId) {
        // Recupera le risposte dal database
        List<Domanda> risposte = domandaDAO.findAllById(risposteId);

        // Associa ogni risposta al genere
        Map<String, Long> conteggioGeneri = risposte.stream()
                .map(Domanda::getContenuto)            // Ottieni il testo della risposta
                .map(categoriaGenereMapper::getGenereByRisposta) // Ottieni il genere dalla mappa aggiornata
                .filter(Objects::nonNull)                      // Filtra risposte senza mapping
                .collect(Collectors.groupingBy(genere -> genere, Collectors.counting()));

        // Trova il genere preferito
        String generePreferitoNome = conteggioGeneri.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalArgumentException("Nessun genere predominante trovato!"))
                .getKey();

        // Recupera il genere dal database
        return genereDAO.findById(generePreferitoNome)
                .orElseThrow(() -> new IllegalArgumentException("Genere non trovato: " + generePreferitoNome));
    }
}

