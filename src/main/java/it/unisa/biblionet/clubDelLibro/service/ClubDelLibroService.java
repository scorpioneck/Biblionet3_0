package it.unisa.biblionet.clubDelLibro.service;

import it.unisa.biblionet.model.entity.ClubDelLibro;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.entity.utente.Lettore;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Implementa l'interfaccia service
 * per il sottosistema ClubDelLibro.
 */
public interface ClubDelLibroService {

    /**
     * Implementa la funzionalità che permette
     * ad un Esperto di creare un Club del Libro.
     * @param club Il Club del Libro da memorizzare
     * @return Il Club del Libro appena creato
     */
    ClubDelLibro creaClubDelLibro(ClubDelLibro club);

    /**
     * Implementa la funzionalità che permette
     * di visualizzare tutti i club del libro.
     * @return La lista dei club
     */
    List<ClubDelLibro> visualizzaClubsDelLibro();


    /**
     * Implementa la funzionalità che permette
     * di filtrare tutti i club del libro.
     * @param filtro Un predicato che descrive come filtrare i Club
     * @return La lista dei club
     */
    List<ClubDelLibro> visualizzaClubsDelLibro(Predicate<ClubDelLibro> filtro);

    /**
     * Implementa la funzionalità che permette
     * di recuperare un oggetto
     * della classe genere dato il nome.
     * @param generi Lista dei generi sottoforma di stringa
     * @return Lista dei generi sottoforma di entità
     */
    List<Genere> getGeneri(List<String> generi);

    /**
     * Implementa la funzionalità che permette
     * di modificare ed
     * effettuare l'update di un club.
     * @param club Il club da modificare
     * @return Il club modificato
     */
    ClubDelLibro modificaDatiClub(ClubDelLibro club);

    /**
     * Implementa la funzionalità che permette
     * di recuperare un
     * club dato il suo ID.
     * @param id L'ID del club da recuperare
     * @return Il club recuperato
     */
    ClubDelLibro getClubByID(int id);

    /**
     * Implementa la funzionalità che permette
     * ad un lettore di effettuare
     * l'iscrizione ad un club del libro.
     * @param club Il club al quale iscriversi
     * @param lettore Il lettore che si iscrive
     * @return true se è andato a buon fine, false altrimenti
     */
    Boolean partecipaClub(ClubDelLibro club, Lettore lettore);

    /**
     * Funzione di utilità che permette di leggere la città
     * in cui si trova un Club del Libro.
     * @param club il club da cui prendere la città
     * @return la città del Club
     */
    String getCittaFromClubDelLibro(ClubDelLibro club);

    /**
     * Restituisce tutti i generi nel sistema.
     * @return Tutti i generi nel sistema.
     */
    Set<String> getTuttiGeneri();

    /**
     * Restituisce tutte le citta nel sistema.
     * @return Tutte le citta nel sistema.
     */
    Set<String> getCitta();

    /**
     * Implementa la funzionalità di prendere una lista di club
     * del libro a cui un lettore partecipa.
     * @param lettore il lettore preso in esame
     * @return la lista dei club del libro a cui partecipa
     */
    List<ClubDelLibro> findAllByLettori(Lettore lettore);

    /**
     * Implementa la funzionalità di prendere una lista di club
     * del libro di cui un esperto è proprietario.
     * @param esperto l' esperto preso in esame
     * @return la lista dei club del libro a cui partecipa
     */
    List<ClubDelLibro> findAllByEsperto(Esperto esperto);

    /**
     * Implementa la funzionalità che permette
     * ad un Esperto di eliminare un evento.
     * @param id L'id dell'evento da eliminare
     * @return L'evento che è stato eliminato, o
     *         un Optional vuoto se l'evento non
     *         esiste.
     */




}
