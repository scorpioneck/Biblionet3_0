package it.unisa.biblionet.blog.controller;

import it.unisa.biblionet.BiblionetApplication;
import it.unisa.biblionet.blog.service.BlogService;
import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.Recensione;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.entity.utente.Lettore;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import it.unisa.biblionet.model.form.CommentoForm;
import it.unisa.biblionet.model.form.RecensioneForm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Implementa il testing di unità per la classe
 * BlogController.
 */


@SpringBootTest(classes = BiblionetApplication.class)
@AutoConfigureMockMvc
public class BlogControllerTest {

    /**
     * Mock del service per simulare
     * le operazioni dei metodi.
     */

    @MockBean
    private BlogService blogService;

    /**
     * Inject di MockMvc per simulare
     * le richieste http.
     */
    @Autowired
    private MockMvc mockMvc;

/**************************Tests for visualizzaModificaEvento*************************/

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per visualizzazione delle recensioni simulando
     * una richiesta http
     * @param listaRecensioni Un club per la simulazione
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource({"provideBlog"})
    void visualizzaRecensione(final List<Recensione> listaRecensioni,final Esperto esperto1) throws Exception {
        // Estrai la recensione dal mock
        Recensione recensione = listaRecensioni.get(0);

        // Configura i mock per il servizio
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);
        when(blogService.visualizzaRisposte()).thenReturn(new ArrayList<>()); // Lista vuota di CommentoRisposta

        // Simula la richiesta HTTP
        this.mockMvc.perform(get("/blog/" + recensione.getId() + "/visualizzaRecensione")
                        .sessionAttr("loggedUser", esperto1)) // Simula l'utente loggato
                .andExpect(status().isOk()) // Verifica che la richiesta abbia successo
                .andExpect(model().attributeExists("commentoForm")) // Verifica l'esistenza del commentoForm nel modello
                .andExpect(model().attributeExists("recensione")) // Verifica l'esistenza della recensione nel modello
                .andExpect(model().attribute("recensione", recensione)) // Controlla che la recensione sia corretta
                .andExpect(model().attributeExists("commento")) // Verifica l'esistenza del commento
                .andExpect(model().attributeExists("commentoRisposta")) // Verifica l'esistenza del commentoRisposta
                .andExpect(model().attributeExists("commentoRispostaList")) // Verifica l'esistenza della lista di commenti risposta
                .andExpect(model().attribute("commentoRispostaList", new ArrayList<>())) // Controlla che la lista sia vuota
                .andExpect(view().name("blog/visualizza-singola-rec")); // Verifica che la vista restituita sia corretta
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per inizializzare l'inserimento di una recensione.
     * @param esperto Un club per la simulazione
     * @throws Exception Eccezione per MovkMvc
     */


    @ParameterizedTest
    @MethodSource("provideEsperti")
    void inizializzaRecensione(final UtenteRegistrato esperto) throws Exception {
        // Simula una lista vuota di libri
        List<Libro> libriVuoti = new ArrayList<>();
        when(blogService.findAllLibri()).thenReturn(libriVuoti);

        // Esegui la richiesta HTTP
        this.mockMvc.perform(get("/blog/inizializzaCreaR")
                        .sessionAttr("loggedUser", esperto))
                .andExpect(status().isOk()) // Verifica che la richiesta abbia successo
                .andExpect(model().attributeExists("recensione")) // Verifica che il modello contenga l'attributo "recensione"
                .andExpect(model().attributeExists("listaLibri")) // Verifica che il modello contenga l'attributo "listaLibri"
                .andExpect(view().name("blog/inserimento-recensione")); // Verifica la vista restituita
    }

    @Test
    void testInizializzaRecensione_LibriNonTrovati() throws Exception {
        // Mock dei libri non trovati
        when(blogService.findAllLibri()).thenReturn(Collections.emptyList());

        // Creazione di un utente esperto valido
        Esperto esperto = new Esperto();
        esperto.setTipo("Esperto");
        esperto.setEmail("test@example.com");

        // Simula la richiesta HTTP con utente loggato
        this.mockMvc.perform(get("/blog/inizializzaCreaR")
                        .sessionAttr("loggedUser", esperto)) // Aggiungi utente esperto nella sessione
                .andExpect(status().isOk()) // Ora lo stato sarà 200
                .andExpect(model().attributeExists("recensione"))
                .andExpect(model().attributeExists("listaLibri"))
                .andExpect(model().attribute("listaLibri", Matchers.empty()))
                .andExpect(view().name("blog/inserimento-recensione"));
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di una recensione.
     * @param recensioni lista di recensioni
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */


    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCreaRecensione_FirstException(List<Recensione> recensioni, Esperto esperto) throws Exception {
        // Mock della recensione
        Recensione recensione = new Recensione();
        recensione.setTitolo("Titolo molto molto molto molto lungo"); // Oltre 30 caratteri
        recensione.setDescrizione("Descrizione valida");

        // Simula la selezione del primo libro dalla lista
        Libro libro = recensioni.get(0).getLibro();

        // Configura i mock
        when(blogService.findLibroById(libro.getIdLibro())).thenReturn(libro);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/creaRecensione")
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(libro.getIdLibro()))
                        .param("titolo", recensione.getTitolo())
                        .param("descrizione", recensione.getDescrizione()))
                .andExpect(status().isBadRequest()); // Verifica che il sistema risponda con un errore
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di una recensione.
     * @param recensioni lista di recensioni
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */


    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCreaRecensione_SecondException(List<Recensione> recensioni, Esperto esperto) throws Exception {
        // Mock della recensione
        Recensione recensione = new Recensione();
        recensione.setTitolo("Titolo valido"); // Titolo valido
        recensione.setDescrizione("a".repeat(256)); // Descrizione di 256 caratteri
                                                         // (oltre il limite di 255)

        // Simula la selezione del primo libro dalla lista
        Libro libro = recensioni.get(0).getLibro();

        // Configura i mock
        when(blogService.findLibroById(libro.getIdLibro())).thenReturn(libro);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/creaRecensione")
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(libro.getIdLibro()))
                        .param("titolo", recensione.getTitolo())
                        .param("descrizione", recensione.getDescrizione()))
                .andExpect(status().isBadRequest()); // Verifica che il sistema risponda con un errore
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di una recensione.
     * @param recensioni lista di recensioni
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */


    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCreaRecensione(List<Recensione> recensioni, Esperto esperto) throws Exception {
        // Mock della recensione
        Recensione recensione = new Recensione();
        recensione.setTitolo("Titolo valido"); // Titolo valido
        recensione.setDescrizione("Descrizione valida e dettagliata"); // Descrizione valida

        // Simula la selezione del primo libro dalla lista
        Libro libro = recensioni.get(0).getLibro();

        // Configura i mock
        when(blogService.findLibroById(libro.getIdLibro())).thenReturn(libro);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/creaRecensione")
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(libro.getIdLibro())) // Libro selezionato
                        .param("titolo", recensione.getTitolo()) // Titolo valido
                        .param("descrizione", recensione.getDescrizione())) // Descrizione valida
                .andExpect(status().is3xxRedirection()) // Verifica che ci sia un redirect
                .andExpect(redirectedUrl("/blog")); // Verifica che il redirect sia verso "/blog"

    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inizializzazione della modifica di una recensione.
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testInizializzaModificaRecensione(Esperto esperto) throws Exception {
        // Arrange
        int id = 1;
        Recensione recensione = new Recensione();
        recensione.setTitolo("Titolo di esempio");
        recensione.setDescrizione("Descrizione di esempio");

        // Simulazione di blogService.trovaRecensioneById
        when(blogService.trovaRecensioneById(id)).thenReturn(recensione);

        // Crea un oggetto RecensioneForm
        RecensioneForm recensioneForm = new RecensioneForm();
        recensioneForm.setTitolo("Titolo di esempio");
        recensioneForm.setDescrizione("Descrizione di esempio");

        // Act e Assert
                mockMvc.perform(get("/blog/{id}/inizializzaModificaR", id)
                                .sessionAttr("loggedUser", esperto)
                                .param("recensioneForm.titolo", "Titolo di esempio") // Inizializza i valori del form
                                .param("recensioneForm.descrizione", "Descrizione di esempio")) // Aggiungi i parametri necessari
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog/modifica-recensione"))
                        .andExpect(model().attributeExists("recensione"))
                        .andExpect(model().attributeExists("id"))
                        .andExpect(model().attribute("id", id))
                        .andExpect(model().attributeExists("listaLibri"));
    }

    @Test
    void testInizializzaModificaRecensione_LibriNonTrovati() throws Exception {
        // Mock di una recensione valida
        Recensione recensione = new Recensione();
        recensione.setId(1);
        recensione.setTitolo("Titolo di prova");
        recensione.setDescrizione("Descrizione di prova");

        // Mock del servizio per trovare la recensione
        when(blogService.trovaRecensioneById(1)).thenReturn(recensione);

        // Creazione di un utente esperto valido
        Esperto esperto = new Esperto();
        esperto.setTipo("Esperto");
        esperto.setEmail("test@example.com");

        // Simula la richiesta HTTP con utente loggato
        this.mockMvc.perform(get("/blog/1/inizializzaModificaR")
                        .sessionAttr("loggedUser", esperto)) // Aggiungi utente esperto nella sessione
                .andExpect(status().isOk()) // Verifica che lo stato sia 200
                .andExpect(model().attributeExists("recensione")) // Verifica che il modello contenga "recensione"
                .andExpect(model().attribute("recensione", Matchers.hasProperty("titolo", Matchers.is("Titolo di prova"))))
                .andExpect(model().attribute("recensione", Matchers.hasProperty("descrizione", Matchers.is("Descrizione di prova"))))
                .andExpect(model().attributeExists("id")) // Verifica che il modello contenga "id"
                .andExpect(model().attribute("id", 1)) // Verifica che l'id sia corretto
                .andExpect(model().attributeExists("listaLibri")) // Verifica che il modello contenga "listaLibri"
                .andExpect(model().attribute("listaLibri", Matchers.empty())) // Verifica che la lista di libri sia vuota
                .andExpect(view().name("blog/modifica-recensione")); // Verifica che la vista sia corretta
    }

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciRecensione_UtenteNonAutenticato(List<Recensione> recensioni) throws Exception {
        Recensione recensione = recensioni.get(0);
        Libro libro = new Libro();
        libro.setIdLibro(1);
        recensione.setLibro(libro);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaRecensione", recensione.getId())
                        .param("titolo", "Titolo valido")
                        .param("descrizione", "Descrizione valida")
                        .param("idLibro", String.valueOf(1)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode()); // usa ex.getStatusCode() o ex.getRawStatusCode()
                });
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per la modifica di una recensione.
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciRecensione_FirstException(List<Recensione> recensioni, Esperto esperto) throws Exception {
        // Mock della recensione da modificare
        Recensione recensione = recensioni.get(0); // Simula che stiamo modificando la prima recensione
        recensione.setTitolo("Titolo molto molto molto molto lungo"); // Oltre 30 caratteri
        recensione.setDescrizione("Descrizione valida");

        // Simula la selezione del libro associato
        Libro libro = recensione.getLibro();

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);
        when(blogService.findLibroById(libro.getIdLibro())).thenReturn(libro);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaRecensione",
                                recensione.getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(libro.getIdLibro())) // Libro valido
                        .param("titolo", recensione.getTitolo()) // Titolo troppo lungo
                        .param("descrizione", recensione.getDescrizione())) // Descrizione valida
                .andExpect(status().isBadRequest()); // Verifica che venga restituito un errore
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per la modifica di una recensione.
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */


    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciRecensioneSecondException(List<Recensione> recensioni, Esperto esperto) throws Exception {
        // Mock della recensione da modificare
        Recensione recensione = recensioni.get(0); // Simula che stiamo modificando la prima recensione
        recensione.setTitolo("Titolo valido"); // Titolo valido
        recensione.setDescrizione("a".repeat(256)); // Descrizione di 256 caratteri (oltre il limite di 255)

        // Simula la selezione del libro associato
        Libro libro = recensione.getLibro();

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);
        when(blogService.findLibroById(libro.getIdLibro())).thenReturn(libro);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaRecensione", recensione.getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(libro.getIdLibro())) // Libro valido
                        .param("titolo", recensione.getTitolo()) // Titolo valido
                        .param("descrizione", recensione.getDescrizione())) // Descrizione troppo lunga
                .andExpect(status().isBadRequest()); // Verifica che venga restituito un errore
    }


    /**
     * Implementa il test della funzionalità gestita dal
     * controller per la modifica di una recensione.
     * @param esperto un esperto
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciRecensione(List<Recensione> recensioni, Esperto esperto) throws Exception {
        // Mock della recensione da modificare
        Recensione recensione = recensioni.get(0);
        recensione.setTitolo("Titolo valido");
        recensione.setDescrizione("Descrizione valida");

        // Simula la selezione del libro associato
        Libro libro = recensione.getLibro();

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaRecensione", recensione.getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(libro.getIdLibro()))
                        .param("titolo", recensione.getTitolo())
                        .param("descrizione", recensione.getDescrizione()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog"));

        // Verifica che il servizio `modificaRecensione` sia stato chiamato
        verify(blogService).modificaRecensione(argThat(r ->
                r.getTitolo().equals(recensione.getTitolo()) &&
                        r.getDescrizione().equals(recensione.getDescrizione())));
    }


    @Test
    void testGestisciCommento_UtenteNonAutenticato() throws Exception {
        // Simula un oggetto CommentoForm
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setDescription("Descrizione valida");

        // Simula l'assenza di un utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciCommento", 1)
                        .flashAttr("commentoForm", commentoForm)) // Aggiungi il form al Model
                .andExpect(status().isUnauthorized()) // Verifica che venga restituito 401
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
                });
    }



    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di un commento.
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciCommentoFirstException(List<Recensione> recensioni, UtenteRegistrato utente) throws Exception {
        // Mock della recensione associata
        Recensione recensione = recensioni.get(0);

        // Mock del form del commento
        CommentoForm commento = new CommentoForm();
        commento.setTitle("Titolo valido");
        commento.setDescription("a".repeat(256)); // Descrizione di 256 caratteri (oltre il limite)

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciCommento", recensione.getId())
                        .sessionAttr("loggedUser", utente)
                        .param("title", commento.getTitle())
                        .param("description", commento.getDescription()))
                .andExpect(status().isBadRequest()) // Verifica che venga restituito un errore
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals("Descrizione del commento troppo lunga", ex.getReason());
                });

    }

    @Test
    void testGestisciCommentoRisposta_UtenteNonAutenticato() throws Exception {
        // Simula una richiesta con parametri
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciRisposta", 1)
                        .param("description", "Descrizione valida") // Simula il campo della descrizione del form
                        .param("commentoPadreId", "2")) // Simula l'ID del commento padre
                .andExpect(status().isUnauthorized()) // Verifica che venga restituito 401
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof ResponseStatusException);
                    ResponseStatusException ex = (ResponseStatusException) resolvedException;
                    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
                });
    }


    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di un commento.
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciCommento(List<Recensione> recensioni, UtenteRegistrato utente) throws Exception {
        // Mock della recensione associata
        Recensione recensione = recensioni.get(0);

        // Mock del form del commento
        CommentoForm commento = new CommentoForm();
        commento.setTitle("Titolo valido");
        commento.setDescription("Descrizione valida e dettagliata");

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciCommento", recensione.getId())
                        .sessionAttr("loggedUser", utente)
                        .param("title", commento.getTitle()) // Aggiungi il titolo
                        .param("description", commento.getDescription())) // Aggiungi la descrizione
                .andExpect(status().is3xxRedirection()) // Verifica che ci sia un redirect
                .andExpect(redirectedUrl("/blog/" + recensione.getId() + "/visualizzaRecensione")); // Verifica il redirect

        // Verifica che il servizio `scriviCommento` sia stato chiamato con i parametri corretti
        ArgumentCaptor<Commento> commentoCaptor = ArgumentCaptor.forClass(Commento.class);
        verify(blogService).scriviCommento(commentoCaptor.capture());

        // Verifica i valori dell'oggetto Commento
        Commento capturedCommento = commentoCaptor.getValue();
        assertEquals(commento.getDescription(), capturedCommento.getDescription());
        assertEquals(recensione, capturedCommento.getRecensione());
        assertEquals(utente.getEmail(), capturedCommento.getUtente());

        if (utente instanceof Esperto) {
            Esperto esperto = (Esperto) utente;
            assertEquals(esperto.getUsername(), capturedCommento.getTitle());
        } else if (utente instanceof Lettore) {
            Lettore lettore = (Lettore) utente;
            assertEquals(lettore.getUsername(), capturedCommento.getTitle());
        }

    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di una risposta.
     * @throws Exception Eccezione per MovkMvc
     */


    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciRispostaFirstException(List<Recensione> recensioni, UtenteRegistrato utente) throws Exception {
        // Mock della recensione associata
        Recensione recensione = recensioni.get(0);

        // Mock del form del commento risposta
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setTitle("Titolo valido");
        commentoForm.setDescription("a".repeat(256)); // Descrizione di 256 caratteri (oltre il limite)

        // Mock del commento padre
        Commento commentoPadre = new Commento();
        commentoPadre.setId(1);

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);
        when(blogService.trovaCommentoById(1)).thenReturn(Optional.of(commentoPadre));

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciRisposta", recensione.getId())
                        .sessionAttr("loggedUser", utente)
                        .param("commentoPadreId", "1")
                        .param("description", commentoForm.getDescription())) // Manca il titolo, verifica 400
                .andExpect(status().isBadRequest()); // Verifica che venga restituito uno status 400
    }

    /**
     * Implementa il test della funzionalità gestita dal
     * controller per l'inserimento di una risposta.
     * @throws Exception Eccezione per MovkMvc
     */

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testGestisciRisposta(List<Recensione> recensioni, UtenteRegistrato utente) throws Exception {
        // Mock della recensione associata
        Recensione recensione = recensioni.get(0);

        // Mock del form del commento risposta
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setTitle("Titolo valido");
        commentoForm.setDescription("Descrizione valida e dettagliata");

        // Mock del commento padre
        Commento commentoPadre = new Commento();
        commentoPadre.setId(1);

        // Configura i mock
        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);
        when(blogService.trovaCommentoById(1)).thenReturn(Optional.of(commentoPadre));

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciRisposta", recensione.getId())
                        .sessionAttr("loggedUser", utente)
                        .param("commentoPadreId", "1") // ID del commento padre
                        .param("title", commentoForm.getTitle()) // Titolo del commento risposta
                        .param("description", commentoForm.getDescription())) // Descrizione del commento risposta
                .andExpect(status().is3xxRedirection()) // Verifica che ci sia un redirect
                .andExpect(redirectedUrl("/blog/" + recensione.getId() + "/visualizzaRecensione")); // Verifica il redirect

        // Verifica che il servizio per la scrittura del commento risposta sia stato chiamato correttamente
        ArgumentCaptor<CommentoRisposta> rispostaCaptor = ArgumentCaptor.forClass(CommentoRisposta.class);
        verify(blogService).rispostaCommento(rispostaCaptor.capture());

        CommentoRisposta capturedRisposta = rispostaCaptor.getValue();

        // Verifica i valori dell'oggetto `Risposta`
        assertEquals(commentoForm.getDescription(), capturedRisposta.getDescription());
        assertEquals(commentoPadre, capturedRisposta.getCommentoPadre());
        assertEquals(utente.getEmail(), capturedRisposta.getUtente());

        if (utente instanceof Esperto) {
            Esperto esperto = (Esperto) utente;
            assertEquals(esperto.getUsername(), capturedRisposta.getTitle());
        } else if (utente instanceof Lettore) {
            Lettore lettore = (Lettore) utente;
            assertEquals(lettore.getUsername(), capturedRisposta.getTitle());
        }

    }


    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCancellaCommento_Success(List<Recensione> recensioni, Esperto esperto) throws Exception {
        int idCommento = 1;
        Commento commento = new Commento();
        commento.setId(idCommento);

        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.of(commento));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommento", recensioni.get(0).getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idCommento", String.valueOf(idCommento)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + recensioni.get(0).getId() + "/visualizzaRecensione"));

        verify(blogService).eliminaCommento(commento);
    }

    /**
     * Test per cancellare un commento non trovato.
     */
    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCancellaCommento_NotFound(List<Recensione> recensioni, Esperto esperto) throws Exception {
        int idCommento = 1;

        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.empty());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommento", recensioni.get(0).getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idCommento", String.valueOf(idCommento)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + recensioni.get(0).getId() + "/visualizzaRecensione"));

        verify(blogService, never()).eliminaCommento(any(Commento.class));
    }

    /**
     * Test per cancellare una risposta con successo.
     */
    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCancellaRisposta_Success(List<Recensione> recensioni, Esperto esperto) throws Exception {
        int idRisposta = 1;
        CommentoRisposta risposta = new CommentoRisposta();
        risposta.setId(idRisposta);

        when(blogService.trovaRispostaById(idRisposta)).thenReturn(Optional.of(risposta));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommentoRisposta", recensioni.get(0).getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idCommentoRisposta", String.valueOf(idRisposta)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + recensioni.get(0).getId() + "/visualizzaRecensione"));

        verify(blogService).eliminaRisposta(risposta);
    }

    /**
     * Test per cancellare una risposta non trovata.
     */

    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCancellaRisposta_NotFound(List<Recensione> recensioni, Esperto esperto) throws Exception {
        int idRisposta = 1;

        when(blogService.trovaRispostaById(idRisposta)).thenReturn(Optional.empty());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommentoRisposta", recensioni.get(0).getId())
                        .sessionAttr("loggedUser", esperto)
                        .param("idCommentoRisposta", String.valueOf(idRisposta)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + recensioni.get(0).getId() + "/visualizzaRecensione"));

        verify(blogService, never()).eliminaRisposta(any(CommentoRisposta.class));
    }

    /**
     * Test per cancellare una recensione con successo.
     */
    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCancellaRecensione_Success(List<Recensione> recensioni, Esperto esperto) throws Exception {
        Recensione recensione = recensioni.get(0);

        when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaRecensione", recensione.getId())
                        .sessionAttr("loggedUser", esperto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog"));

        verify(blogService).eliminaRecensione(recensione);
    }

    /**
     * Test per cancellare una recensione non trovata.
     */
    @ParameterizedTest
    @MethodSource("provideBlog")
    void testCancellaRecensione_NotFound(List<Recensione> recensioni, Esperto esperto) throws Exception {
        int idRecensione = 1;

        when(blogService.trovaRecensioneById(idRecensione)).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaRecensione", idRecensione)
                        .sessionAttr("loggedUser", esperto))
                .andExpect(status().isNotFound());

        verify(blogService, never()).eliminaRecensione(any(Recensione.class));
    }


    private static Stream<Arguments> provideBlog() {
        Biblioteca biblioteca = new Biblioteca("fmorlicchio@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Scafati",
                "Via Galileo Galilei 34",
                "0813223238",
                "Biblioteca Francesco Morlicchio");

        Esperto esperto1 = new Esperto(
                "ciromarano@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Via Roma 2",
                "2345678901",
                "ciromarano",
                "Ciro",
                "Marano",
                biblioteca
        );

        Recensione recensione1 = new Recensione();
        recensione1.setId(1);
        recensione1.setTitolo("Recensione 1");
        recensione1.setDescrizione("Testo della recensione 1");
        recensione1.setLibro(new Libro("Titolo Libro 1", "Autore Libro 1",
                "ISBN12345", LocalDateTime.now(), "Editore 1", "Descrizione Libro 1"));
        recensione1.setEsperto(esperto1);

        Recensione recensione2 = new Recensione();
        recensione2.setId(2);
        recensione2.setTitolo("Recensione 2");
        recensione2.setDescrizione("Testo della recensione 2");
        recensione2.setLibro(new Libro("Titolo Libro 2", "Autore Libro 2",
                "ISBN67890", LocalDateTime.now(), "Editore 2", "Descrizione Libro 2"));
        recensione2.setEsperto(esperto1);

        List<Recensione> listaRecensioni = Arrays.asList(recensione1, recensione2);

        return Stream.of(Arguments.of(listaRecensioni, esperto1));
    }

    private static Stream<Arguments> provideEsperti() {
        Biblioteca biblioteca = new Biblioteca("fmorlicchio@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Scafati",
                "Via Galileo Galilei 34",
                "0813223238",
                "Biblioteca Francesco Morlicchio");

        Esperto esperto = new Esperto(
                "ciromaiorino@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Via Roma 2",
                "2345678901",
                "ciromaiorino",
                "Ciro",
                "Maiorino",
                biblioteca
        );

        return Stream.of(Arguments.of(esperto));
    }

}