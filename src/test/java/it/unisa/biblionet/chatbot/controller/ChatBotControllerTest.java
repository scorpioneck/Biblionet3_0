package it.unisa.biblionet.chatbot.controller;

import it.unisa.biblionet.chatbot.service.ChatBotService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
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
            this.mockMvc.perform(MockMvcRequestBuilders.get("/bot/risposte"))
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
            this.mockMvc.perform(MockMvcRequestBuilders.get("/bot/risposte")
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

        this.mockMvc.perform(MockMvcRequestBuilders.get("/bot/risposte"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertTrue(result.getResolvedException() instanceof ResponseStatusException);

                    assertEquals("Utente non loggato.",
                            ((ResponseStatusException) result.getResolvedException()).getReason());
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

}