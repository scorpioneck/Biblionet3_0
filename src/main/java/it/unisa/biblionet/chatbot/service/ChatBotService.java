package it.unisa.biblionet.chatbot.service;


import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.chatbot.ChatBot;
import it.unisa.biblionet.model.entity.chatbot.Risposta;
import it.unisa.biblionet.model.entity.chatbot.Domanda;

import java.util.List;


public interface ChatBotService {

    /**
     * Implementa la funzionlità di recuperare il chatbot
     * per il lettore o per l'esperto
     * @param id
     * @return Chatbot;
     */
    ChatBot identificaUtente(int id);

    //IMPLEMENTATE FUNZIONALITA PER PATH DIREZIONALI

    /**
     * Implementa la funzionlità di recuperare le domande
     * per il lettore o per l'esperto
     *
     * @return tutte le liste;
     */

    List<Risposta> visualizzaAllRisposta();

    /**
     * Implementa la funzionlità di recuperare la domanda
     * tramite l'id
     * @param idRisposta
     * @return tutte le liste;
     */

    Risposta trovaRispostaById(int idRisposta);

    /**
     * Calcola il genere preferito basato sulle risposte dell'utente.
     * @param rispostaId l'ID delle risposte.
     * @return il genere preferito.
     */

    Genere calcolaGenerePreferito(List<Integer> rispostaId);

}