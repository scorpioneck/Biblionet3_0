package it.unisa.biblionet.chatbot.service;

import it.unisa.biblionet.model.dao.chatbot.DomandaDAO;
import it.unisa.biblionet.model.dao.chatbot.ChatBotDAO;
import it.unisa.biblionet.model.dao.chatbot.RispostaDAO;
import it.unisa.biblionet.model.entity.chatbot.Risposta;
import it.unisa.biblionet.model.entity.chatbot.ChatBot;
import it.unisa.biblionet.model.entity.chatbot.Domanda;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ChatBotServiceImpTest {

    @InjectMocks
    private ChatBotServiceImp chatBotService;

    @Mock
    private ChatBotDAO chatBotDAO;

    @Mock
    private RispostaDAO rispostaDAO;


    /**
     * Testa la funzionlità di recuperare il chatbot
     * per il lettore o per l'esperto
     * @return Chatbot;
     */

    @Test
    public void getTrovaUtente() {
        ChatBot chat = new ChatBot();
        chat.setRisposta2(new ArrayList<>());

        chat.setId(1);

        when(chatBotDAO.findById(1)).thenReturn(Optional.of(chat));

        ChatBot chatResult = chatBotService.identificaUtente(1);

        assertNotNull(chatResult);
        assertEquals(1,chatResult.getId());
        assertNotNull(chatResult.getRisposta2());
        verify(chatBotDAO,times(1)).findById(1);

    }

    /**
     * Testa la funzionlità di recuperare le risposte
     * per il lettore o per l'esperto
     * @return;
     */

   @Test
  public void visualizzaAllRisposta() {
        Risposta risposta1 = new Risposta();
        risposta1.setIdRisposta(1);
        risposta1.setContenuto("Risposta 1");

        Risposta risposta2 = new Risposta();
        risposta2.setIdRisposta(2);
        risposta2.setContenuto("Risposta 2");

        List<Risposta> mockDomande = Arrays.asList(risposta1, risposta2);

        // Configurazione del mock
        when(rispostaDAO.findAllRisposta()).thenReturn(mockDomande);

        // Chiamata del metodo da testare
        List<Risposta> result = chatBotService.visualizzaAllRisposta();

        // Asserzioni
        assertNotNull(result); // Assicura che il risultato non sia nullo
        assertEquals(2, result.size()); // Controlla che la dimensione sia corretta
        assertEquals("Risposta 1", result.get(0).getContenuto()); // Controlla il contenuto della prima domanda
        assertEquals("Risposta 2", result.get(1).getContenuto()); // Controlla il contenuto della seconda domanda

        // Verifica che il metodo del DAO sia stato chiamato
        verify(rispostaDAO, times(1)).findAllRisposta();

    }

    /**
     * Testa la funzionlità di recuperare la risposta tramite l'id
     * @return;
     */


    @Test
    public void trovaRispostaById() {
        // Dati di esempio
        Risposta risposta = new Risposta();
        risposta.setIdRisposta(1);
        risposta.setContenuto("Risposta di non trovata");

        // Configurazione del mock
        when(rispostaDAO.findById(1)).thenReturn(Optional.of(risposta));

        // Chiamata del metodo da testare
        Risposta result = chatBotService.generaRisposta(1);

        // Asserzioni
        assertNotNull(result); // Assicura che il risultato non sia nullo
        assertEquals("Risposta di non trovata", result.getContenuto()); // Verifica il contenuto corretto

        // Verifica che il metodo del DAO sia stato chiamato
        verify(rispostaDAO, times(1)).findById(1);
    }

    /**
     * Testa la funzionlità di recuperare la risposta tramite l'id
     * @return;
     */

    @Test
    public void findByIdRisposta_NotFound() {
        // Configurazione del mock per simulare un'entità non trovata
        when(rispostaDAO.findById(99)).thenReturn(Optional.empty());

        // Test per verificare che venga lanciata l'eccezione
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            chatBotService.generaRisposta(99);
        });

        // Asserzioni sull'eccezione
        assertEquals("Risposta non trovata99", exception.getMessage());

        // Verifica che il metodo del DAO sia stato chiamato
        verify(rispostaDAO, times(1)).findById(99);
    }


}
