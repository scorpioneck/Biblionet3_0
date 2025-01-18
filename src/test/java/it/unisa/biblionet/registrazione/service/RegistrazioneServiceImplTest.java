package it.unisa.biblionet.registrazione.service;

import it.unisa.biblionet.autenticazione.service.AutenticazioneServiceImpl;
import it.unisa.biblionet.model.dao.GenereDAO;
import it.unisa.biblionet.model.dao.utente.BibliotecaDAO;
import it.unisa.biblionet.model.dao.utente.EspertoDAO;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.dao.utente.LettoreDAO;
import it.unisa.biblionet.model.entity.utente.Lettore;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RegistrazioneServiceImplTest {

    /**
     * Si occupa di gestire le operazioni CRUD dell'Esperto.
     */
    @Mock
    private EspertoDAO espertoDAO;

    /**
     * Si occupa di gestire le operazioni CRUD della biblioteca.
     */
    @Mock
    private BibliotecaDAO bibliotecaDAO;

    /**
     * Mocking del dao per simulare le
     * CRUD.
     */
    @Mock
    private LettoreDAO lettoreDAO;

    /**
     * Mocking del dao per simulare le
     * CRUD.
     */
    @Mock
    private GenereDAO genereDAO;

    /**
     * Inject del service per simulare
     * le operazioni.
     */
    @InjectMocks
    private RegistrazioneServiceImpl registrazioneService;

    /**
     * Inject del service per simulare
     * le operazioni.
     */
    @Mock
    private AutenticazioneServiceImpl autenticazioneService;

    /**
     * Testa la funzionalità di registrazione di un Esperto.
     */
    @Test
    public void registraEsperto() {
        Esperto esperto = new Esperto();
        when(espertoDAO.save(esperto)).thenReturn(esperto);
        assertEquals(esperto, registrazioneService.registraEsperto(esperto));
    }

    /**
     * Testa la funzionalità di registrazione di una Biblioteca.
     */
    @Test
    public void registraBiblioteca() {
        Biblioteca biblioteca = new Biblioteca();
        when(bibliotecaDAO.save(biblioteca)).thenReturn(biblioteca);
        assertEquals(biblioteca, registrazioneService.
                registraBiblioteca(biblioteca));
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di registrare un
     * lettore nel service.
     */
    @Test
    public void registraLettore() {
        Lettore lettore = new Lettore();
        lettore.setEmail("a");
        lettore.setUsername("a");
        lettore.setNome("a");
        lettore.setCognome("a");
        when(lettoreDAO.save(lettore)).thenReturn(lettore);
        assertEquals(lettore, registrazioneService.registraLettore(lettore));
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di ricerca di un
     * genere facendo 0 iterazioni nel service.
     */
    @Test
    public void findGeneriByName0IT() {
        List<Genere> list = new ArrayList<>();
        String[] generi = {""};
        assertEquals(list, registrazioneService.findGeneriByName(generi));
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di ricerca di un
     * genere facendo una iterazione nel service.
     */
    @Test
    public void findGeneriByName1IT() {

        List<Genere> list = new ArrayList<>();
        list.add(new Genere());
        String[] generi = {"test"};
        when(genereDAO.findByName("test")).thenReturn(new Genere());
        assertEquals(list, registrazioneService.findGeneriByName(generi));
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di ricerca di un
     * genere facendo due iterazioni nel service.
     */
    @Test
    public void findGeneriByName2IT() {

        List<Genere> list = new ArrayList<>();
        list.add(new Genere());
        list.add(new Genere());
        String[] generi = {"test", "test2"};
        when(genereDAO.findByName("test")).thenReturn(new Genere());
        when(genereDAO.findByName("test2")).thenReturn(new Genere());
        assertEquals(list, registrazioneService.findGeneriByName(generi));
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di ricerca di un genere
     * facendo fallire l'if nel service.
     */
    @Test
    public void findGeneriByName() {

        List<Genere> list = new ArrayList<>();
        String[] generi = {"test"};
        when(genereDAO.findByName("test")).thenReturn(null);
        assertEquals(list, registrazioneService.findGeneriByName(generi));
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di ricerca di un genere
     * facendo riuscire l'if nel service.
     */
    @Test
    public void findGeneriByName2() {

        List<Genere> list = new ArrayList<>();
        list.add(new Genere());
        String[] generi = {"test"};
        when(genereDAO.findByName("test")).thenReturn(new Genere());
        assertEquals(list, registrazioneService.findGeneriByName(generi));
    }

    /**
     * Metodo che testa la funzione di ricerca
     * di una mail se già esistente con una lista
     * vuota di utenti in input
     */
    @Test
    public void isEmailRegistrata(){

        ArrayList<UtenteRegistrato> utenti = new ArrayList<>();
        when(lettoreDAO.findAll()).thenReturn(utenti);
        assertFalse(registrazioneService.isEmailRegistrata("test@test"));

    }

    /**
     * Metodo che testa la funzione di ricerca
     * di una mail se già esistente con una lista
     * con un solo utente in input.
     * Ritorna true perchè la mail usata esiste nella lista.
     */
    @Test
    public void isEmailRegistrata2(){

        Lettore lettore = new Lettore();
        lettore.setEmail("test@test");
        ArrayList<UtenteRegistrato> utenti = new ArrayList<>();
        utenti.add(lettore);
        when(lettoreDAO.findAll()).thenReturn(utenti);
        assertTrue(registrazioneService.isEmailRegistrata("test@test"));

    }

    /**
     * Metodo che testa la funzione di ricerca
     * di una mail se già esistente con una lista
     * con un solo utente in input.
     * Ritorna true perchè la mail usata esiste nella lista.
     */
    @Test
    public void isEmailRegistrata3(){

        Lettore lettore = new Lettore();
        lettore.setEmail("test");
        ArrayList<UtenteRegistrato> utenti = new ArrayList<>();
        utenti.add(lettore);
        when(lettoreDAO.findAll()).thenReturn(utenti);
        assertFalse(registrazioneService.isEmailRegistrata("test@test"));

    }





}
