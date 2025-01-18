package it.unisa.biblionet.chatbot.controller;

import it.unisa.biblionet.chatbot.service.ChatBotService;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.chatbot.*;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatBotControllerTest {

    @MockBean
    private ChatBotService chatBotService;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("provideChatBot")
    public void testChatBot(ChatBot chatBot, UtenteRegistrato utente, String expectedView) throws Exception {
        if (utente == null) {
            // Simula il caso in cui l'utente non è loggato
            this.mockMvc.perform(get("/bot/risposte"))
                    .andExpect(status().isBadRequest()) // Verifica che lo stato sia 400
                    .andExpect(result -> {
                        // Verifica che l'eccezione sia una ResponseStatusException
                        assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                        // Verifica il messaggio dell'eccezione
                        assertEquals("Utente non loggato.",
                                ((ResponseStatusException) result.getResolvedException()).getReason());
                    });
        } else {
            // Configura il mock del servizio per restituire il chatbot corretto
            when(chatBotService.identificaUtente(chatBot.getId())).thenReturn(chatBot);

            // Simula una richiesta con l'utente loggato
            this.mockMvc.perform(get("/bot/risposte")
                            .sessionAttr("loggedUser", utente))
                    .andExpect(status().isOk()) // Verifica che lo stato sia 200
                    .andExpect(model().attributeExists("chatB")) // Verifica che il modello contenga "chatBot"
                    .andExpect(model().attribute("chatB", chatBot)) // Verifica il contenuto di "chatBot"
                    .andExpect(model().attributeExists("risposte")) // Verifica che il modello contenga "domande"
                    .andExpect(model().attribute("risposte", chatBot.getRisposta2())) // Verifica le domande pregenerate
                    .andExpect(view().name(expectedView)); // Verifica la vista restituita

        }
    }

    @Test
    public void loadChatBotFirstException() throws Exception {

        this.mockMvc.perform(get("/bot/risposte"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);

                    assertEquals("Utente non loggato.",
                            ((ResponseStatusException) result.getResolvedException()).getReason());
                });
    }

    @ParameterizedTest
    @MethodSource("provideChatBotRisposte")
    void testGetRisposte(Risposta risposta, UtenteRegistrato utente, String expectedView) throws Exception {
        if (utente == null) {
            // Caso utente non loggato
            this.mockMvc.perform(get("/bot/" + risposta.getIdRisposta() + "/domanda"))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        assertTrue(result.getResolvedException() instanceof ResponseStatusException);
                        assertEquals("Utente non loggato.",
                                ((ResponseStatusException) result.getResolvedException()).getReason());
                    });
        } else {
            // Mock della risposta
            when(chatBotService.generaRisposta(risposta.getIdRisposta())).thenReturn(risposta);

            // Caso utente loggato
            this.mockMvc.perform(get("/bot/" + risposta.getIdRisposta() + "/domanda")
                            .sessionAttr("loggedUser", utente))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("domande"))
                    .andExpect(model().attribute("domande", risposta.getDomande()))
                    .andExpect(view().name(expectedView));
        }
    }

    @Test
    void testCaricaPrimaOpzione_UtenteNonLoggato() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(get("/bot/questionario/inizia"))
                .andExpect(status().isBadRequest()) // Verifica che la risposta sia 400
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals("Utente non loggato.", ex.getReason());
                });
    }

    @Test
    void testCaricaPrimaOpzione_QuestionarioVuoto() throws Exception {
        // Mock dell'utente loggato
        UtenteRegistrato utente = new UtenteRegistrato();
        utente.setTipo("Lettore");
        utente.setEmail("utente@test.com");

        // Mock del ChatBot con questionario vuoto
        ChatBot chatBot = new ChatBot();
        chatBot.setRisposta1(Collections.emptyList());

        when(chatBotService.identificaUtente(1)).thenReturn(chatBot);

        // Simula la richiesta HTTP
        this.mockMvc.perform(get("/bot/questionario/inizia")
                        .sessionAttr("loggedUser", utente))
                .andExpect(status().isOk()) // Verifica che la risposta sia 200
                .andExpect(jsonPath("$.questionarioCompletato").value(true)) // Verifica che il questionario sia completato
                .andExpect(jsonPath("$.prossimaRisposta").doesNotExist()); // Non ci deve essere nessuna risposta
    }

    @Test
    void testGetNextOpzione_ChatBotNonPresente() throws Exception {
        // Simula la richiesta senza ChatBot in sessione
        this.mockMvc.perform(get("/bot/nextDomanda/0")
                        .param("domandaId", "1"))
                .andExpect(status().isBadRequest()) // Verifica che lo stato sia 400
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals("ChatBot non trovato nella sessione.", ex.getReason());
                });
    }

    @Test
    void testGetNextOpzione_ProssimaDomandaDisponibile() throws Exception {
        // Mock del ChatBot in sessione con più risposte
        Risposta risposta1 = new Risposta();
        risposta1.setIdRisposta(1);
        risposta1.setContenuto("Prima risposta");

        Risposta risposta2 = new Risposta();
        risposta2.setIdRisposta(2);
        risposta2.setContenuto("Seconda risposta");

        ChatBot chatBot = new ChatBot();
        chatBot.setRisposta1(List.of(risposta1, risposta2));

        // Simula la sessione con il ChatBot e un'opzione ID iniziale
        List<Integer> opzioniId = new ArrayList<>();
        this.mockMvc.perform(get("/bot/nextDomanda/0")
                        .sessionAttr("chatBot", chatBot)
                        .sessionAttr("opzioniId", opzioniId)
                        .param("domandaId", "1"))
                .andExpect(status().isOk()) // Verifica che lo stato sia 200
                .andExpect(jsonPath("$.questionarioCompletato").value(false)) // Verifica che il questionario non sia completato
                .andExpect(jsonPath("$.prossimaRisposta.contenuto").value("Seconda risposta")) // Contenuto della prossima risposta
                .andExpect(jsonPath("$.indiceRisposta").value(1)); // Indice aggiornato
    }


    @Test
    void testCalcolaGenere_Success() throws Exception {
        // Mock dell'utente loggato
        UtenteRegistrato utente = new UtenteRegistrato();
        utente.setTipo("Lettore");
        utente.setEmail("utente@test.com");

        // Mock delle opzioni selezionate dall'utente
        List<Integer> opzioniId = List.of(1, 2, 3);

        // Mock del genere calcolato
        Genere genereMock = new Genere();
        genereMock.setNome("Avventura");

        // Configura il comportamento del service
        when(chatBotService.calcolaGenerePreferito(opzioniId)).thenReturn(genereMock);

        // Simula la richiesta HTTP con utente loggato e opzioni in sessione
        this.mockMvc.perform(post("/bot/calcolaGenere")
                        .sessionAttr("loggedUser", utente)
                        .sessionAttr("opzioniId", opzioniId))
                .andExpect(status().isOk()) // Verifica che la risposta abbia stato 200
                .andExpect(content().string("Il genere preferito è: Avventura")); // Verifica il messaggio della risposta
    }

    @Test
    void testCalcolaGenere_UtenteNonLoggato() throws Exception {
        // Simula la richiesta HTTP senza utente loggato
        this.mockMvc.perform(post("/bot/calcolaGenere"))
                .andExpect(status().isBadRequest()) // Verifica che la risposta abbia stato 400
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals("Utente non loggato.", ex.getReason());
                });
    }


    static Stream<Arguments> provideChatBot() {
          Categoria cat = Categoria.AVVENTURA;

        ChatBot chatBLettore = new ChatBot();
        chatBLettore.setId(1);
        chatBLettore.setRisposta2(List.of(new Risposta("Risposta per Lettore",cat,chatBLettore)));


        UtenteRegistrato utenteLettore = new UtenteRegistrato();
        utenteLettore.setTipo("Lettore");

        ChatBot chatBEsperto = new ChatBot();
        chatBEsperto.setId(2);
        chatBEsperto.setRisposta2(List.of(new Risposta("Risposta per Esperto",cat,chatBEsperto)));


        UtenteRegistrato utenteEsperto = new UtenteRegistrato();
        utenteEsperto.setTipo("Esperto");


        return Stream.of(
                Arguments.of(chatBLettore, null, null),
                Arguments.of(chatBLettore, utenteLettore, "chatbot/risposteFragment :: risposteList"),
                Arguments.of(chatBEsperto, utenteEsperto, "chatbot/risposteFragment :: risposteList")
        );
    }

    static Stream<Arguments> provideChatBotRisposte() {
        Categoria categoria = Categoria.AVVENTURA;

        // ChatBot con Risposte per Lettore
        ChatBot chatBLettore = new ChatBot();
        chatBLettore.setId(1);
        Risposta rispostaLettore = new Risposta("Risposta per Lettore", categoria, chatBLettore);
        rispostaLettore.setDomande(List.of(
                new Domanda(),
                new Domanda()
        ));
        chatBLettore.setRisposta2(List.of(rispostaLettore));

        UtenteRegistrato utenteLettore = new UtenteRegistrato();
        utenteLettore.setTipo("Lettore");

        // ChatBot con Risposte per Esperto
        ChatBot chatBEsperto = new ChatBot();
        chatBEsperto.setId(2);
        Risposta rispostaEsperto = new Risposta("Risposta per Esperto", categoria, chatBEsperto);
        rispostaEsperto.setDomande(List.of(
                new Domanda(),
                new Domanda()
        ));
        chatBEsperto.setRisposta2(List.of(rispostaEsperto));

        UtenteRegistrato utenteEsperto = new UtenteRegistrato();
        utenteEsperto.setTipo("Esperto");

        return Stream.of(
                Arguments.of(rispostaLettore, utenteLettore, "chatbot/domandeFragment :: domandeList"),
                Arguments.of(rispostaEsperto, utenteEsperto, "chatbot/domandeFragment :: domandeList"),
                Arguments.of(rispostaLettore, null, null) // Utente non loggato
        );
    }


}