package it.unisa.biblionet.blog.service;

import it.unisa.biblionet.model.dao.LibroDAO;
import it.unisa.biblionet.model.dao.blog.CommentoDAO;
import it.unisa.biblionet.model.dao.blog.CommentoRispostaDAO;
import it.unisa.biblionet.model.dao.blog.RecensioneDAO;
import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import it.unisa.biblionet.model.entity.blog.Recensione;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Esperto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BlogServiceImpTest {

    /**
     * Inject del service per simulare le operazioni.
     */

    @InjectMocks
    private BlogServiceImp blogService;


    /**
     * Mocking del dao per simulare le
     * CRUD.
     */

    @Mock
    private RecensioneDAO recensioneDAO;

    /**
     * Mocking del dao per simulare le
     * CRUD.
     */

    @Mock
    private CommentoDAO commentoDAO;


    /**
     * Mocking del dao per simulare le
     * CRUD.
     */

    @Mock
    private CommentoRispostaDAO commentoRispostaDAO;

    /**
     * Mocking del dao per simulare le
     * CRUD.
     */

    @Mock
    private LibroDAO libroDAO;

    /**
     * Si esegue il test unitario per il corretto funzionamento
     * dei servizi.
     */

    @Test
    public void visualizzaRecensioni(){
        List<Recensione> recensioniMock = List.of(new Recensione(), new Recensione());
        when(recensioneDAO.findAllRecensioni()).thenReturn(recensioniMock);

        List<Recensione> recensioni = blogService.visualizzaRecensioni();

        assertNotNull(recensioni);
        assertEquals(2, recensioni.size());
        verify(recensioneDAO, times(1)).findAllRecensioni();
    }


    @Test
    public void creaRecensioni() {
        Recensione recensioneMock = new Recensione();
        recensioneMock.setTitolo("Nuova Recensione");
        when(recensioneDAO.save(any(Recensione.class))).thenReturn(recensioneMock);

        Recensione recensione = blogService.creaRecensione(recensioneMock);
        assertNotNull(recensione);
        assertEquals("Nuova Recensione", recensione.getTitolo());
        verify(recensioneDAO, times(1)).save(recensioneMock);

    }

    @Test
    public void trovaRecensione() {
        Recensione recensione = new Recensione();
        recensione.setId(1);
        when(recensioneDAO.findById(1)).thenReturn(Optional.of(recensione));

        Recensione result = blogService.trovaRecensioneById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(recensioneDAO, times(1)).findById(1);

    }

    @Test
    public void eliminaRecensione() {
        Recensione recensioneMock = new Recensione();
        recensioneMock.setId(1);

        Commento commentoMock = new Commento();
        commentoMock.setId(1);
        commentoMock.setRecensione(recensioneMock);

        List<Commento> commenti = List.of(commentoMock);
        recensioneMock.setCommenti(commenti); // Inizializza i commenti

        when(recensioneDAO.findById(1)).thenReturn(Optional.of(recensioneMock));

        blogService.eliminaRecensione(recensioneMock);

        verify(recensioneDAO, times(1)).deleteById(1);
    }

    @Test
    public void modificaRecensione() {

        Recensione recensioneMock = new Recensione();
        recensioneMock.setId(1);
        recensioneMock.setTitolo("Recensione Modificata");
        when(recensioneDAO.save(any(Recensione.class))).thenReturn(recensioneMock);

        Recensione result = blogService.modificaRecensione(recensioneMock);

        assertNotNull(result);
        assertEquals("Recensione Modificata", result.getTitolo());
        verify(recensioneDAO, times(1)).save(recensioneMock);
    }

    @Test
    public void visualizzaCommenti() {

        List<Commento> commentiMock = List.of(new Commento(), new Commento());
        when(commentoDAO.findAllCommenti()).thenReturn(commentiMock);

        List<Commento> result = blogService.visualizzaCommenti();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentoDAO, times(1)).findAllCommenti();

    }

    @Test
    public void scriviCommento() {
        Commento commentoMock = new Commento();
        commentoMock.setTitle("Nuovo Commento");
        when(commentoDAO.save(any(Commento.class))).thenReturn(commentoMock);

        Commento result = blogService.scriviCommento(commentoMock);

        assertNotNull(result);
        assertEquals("Nuovo Commento", result.getTitle());
        verify(commentoDAO, times(1)).save(commentoMock);

    }


    @Test
    public void eliminaCommento() {
        Recensione recensioneMock = new Recensione();
        recensioneMock.setId(1);

        Commento commentoMock = new Commento();
        commentoMock.setId(1);
        commentoMock.setRecensione(recensioneMock); // Associa la recensione

        when(commentoDAO.findById(1)).thenReturn(Optional.of(commentoMock));

        blogService.eliminaCommento(commentoMock);

        verify(commentoDAO, times(1)).deleteById(1);

    }

    @Test
    public void trovaCommentoById() {
        Commento commentoMock = new Commento();
        commentoMock.setId(1);
        when(commentoDAO.findById(1)).thenReturn(Optional.of(commentoMock));

        Optional<Commento> result = blogService.trovaCommentoById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(commentoDAO, times(1)).findById(1);

    }

    @Test
    public void rispostaCommento() {
        CommentoRisposta rispostaMock = new CommentoRisposta();
        rispostaMock.setTitle("Risposta al commento");
        when(commentoRispostaDAO.save(any(CommentoRisposta.class))).thenReturn(rispostaMock);

        CommentoRisposta result = blogService.rispostaCommento(rispostaMock);

        assertNotNull(result);
        assertEquals("Risposta al commento", result.getTitle());
        verify(commentoRispostaDAO, times(1)).save(rispostaMock);
    }

    @Test
    public void eliminaCommentoRisposta() {
        CommentoRisposta commentoMock = new CommentoRisposta();
        commentoMock.setId(1);

        blogService.eliminaRisposta(commentoMock);

        verify(commentoRispostaDAO, times(1)).deleteById(1);

    }

    @Test
    public void trovaCommentoRisposta() {
        CommentoRisposta commentoMock = new CommentoRisposta();
        commentoMock.setId(1);
        when(commentoRispostaDAO.findById(1)).thenReturn(Optional.of(commentoMock));

        Optional<CommentoRisposta> result = blogService.trovaRispostaById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(commentoRispostaDAO, times(1)).findById(1);

    }


    @Test
    public void findAllCommentoRisposta() {

        List<CommentoRisposta> risposteMock = List.of(new CommentoRisposta(), new CommentoRisposta());
        when(commentoRispostaDAO.findAllRisposta()).thenReturn(risposteMock);

        List<CommentoRisposta> result = blogService.visualizzaRisposte();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentoRispostaDAO, times(1)).findAllRisposta();
    }

    @Test
    public void findAllLibri() {
        List<Libro> libriMock = List.of(new Libro(), new Libro());
        when(libroDAO.findAllLibro()).thenReturn(libriMock);

        List<Libro> result = blogService.findAllLibri();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(libroDAO, times(1)).findAllLibro();

    }

    @Test
    public void findLibroById() {
        Libro libroMock = new Libro();
        libroMock.setIdLibro(1);
        when(libroDAO.findById(1)).thenReturn(Optional.of(libroMock));

        Libro result = blogService.findLibroById(1);

        assertNotNull(result);
        assertEquals(1, result.getIdLibro());
        verify(libroDAO, times(1)).findById(1);
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

}



