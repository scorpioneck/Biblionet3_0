package it.unisa.biblionet.blog.service;

import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import it.unisa.biblionet.model.entity.blog.Recensione;

import java.util.List;
import java.util.Optional;

public interface BlogService  {

    /**
     * Implementa la funzionalità che permette
     * di Visualizzare tutte le recensioni nel blog
     * per tutti gli utenti.
     * @return tutte le recensioni trovate.
     */

    List<Recensione> visualizzaRecensioni();

    /**
     * Implementa la funzionalità che permette
     * di recuperare una Recensione tramite l'id.
     * @param id la recensione da recuperare.
     * @return La recensione recuperata.
     */

    Recensione trovaRecensioneById(int id);


    /**
     * Implementa la funzionalità che permette
     * ad un Esperto di scrivere una Recensione.
     * @param recensione la recensione da memorizzare
     * @return La recensione appena creata
     */

    Recensione creaRecensione(Recensione recensione);

    /**
     * Implementa la funzionalità che permette
     * di modificare ed
     * effettuare l'update di una recensione da parte dell'Esperto.
     * @param recensione Il club da modificare
     * @return la recensione modificata
     */

    Recensione modificaRecensione(Recensione recensione);


    /**
     * Implementa la funzionalità di eliminare una recensione.
     * @param recensione l' esperto preso in esame
     * @return recensione elimanata.
     */

    Recensione eliminaRecensione(Recensione recensione);

    /**
     * Implementa la funzionalità di Visualizzare tutti
     * i commenti disponibili.
     * @return la lista dei commenti
     */

     List<Commento> visualizzaCommenti();


    /**
     * Implementa la funzionlità di scrivere i commenti.
     * @param commento crea il commento
     * @return il commento
     */


     Commento scriviCommento(Commento commento);

    /**
     * Implementa la funzionalità di eliminare un commento.
     * @param commento preso in esame
     * @return commento elimanato.
     */

    Commento eliminaCommento(Commento commento);


    /**
     * Implementa la funzionalità di rispondere al
     * commento pubblicato.
     * @param risposta preso in esame,
     * @return la risposta Commento
     */

    CommentoRisposta rispostaCommento(CommentoRisposta risposta);


    /**
     * Implementa la funzionalità di rispondere al
     * commento pubblicato.
     * @param id preso in esame,
     * @return il commento
     */

    Optional<Commento> trovaCommentoById(int id);

    /**
     * Implementa la funzionalità di rispondere al
     * commento pubblicato.
     *
     * @return lista CommentiRisposta
     */

    List<CommentoRisposta> visualizzaRisposte();

    /**
     * Trova tutti i libri inseriti nel sistema.
     *
     * @return lista Libro
     */
    List<Libro> findAllLibri();

    /**
     * Trova tutti i libri inseriti nel sistema.
     *
     * @return lista Libro
     */

    Libro findLibroById(final int id);

    /**
     * Implementa la funzionalità dell'esperto di eliminare
     * il commento.
     * @return lista CommentiRisposta
     */

    CommentoRisposta eliminaRisposta(final CommentoRisposta commento);

    /**
     * Implementa la funzionalità di recuperare la risposta al commento
     * dal database.
     * @return lista CommentiRisposta
     */

    Optional<CommentoRisposta> trovaRispostaById(final int idRisposta);


}