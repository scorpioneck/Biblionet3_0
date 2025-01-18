package it.unisa.biblionet.blog.service;


import it.unisa.biblionet.model.dao.LibroDAO;
import it.unisa.biblionet.model.dao.blog.CommentoDAO;
import it.unisa.biblionet.model.dao.blog.CommentoRispostaDAO;
import it.unisa.biblionet.model.dao.blog.RecensioneDAO;
import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import it.unisa.biblionet.model.entity.blog.Recensione;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogServiceImp implements BlogService {

    /**
     * Si occupa delle operazioni CRUD per una recensione.
     */

    private final RecensioneDAO recensioneDAO;

    /**
     * Si occupa delle operazioni CRUD per una recensione.
     */

    private final CommentoDAO commentoDAO;

    /**
     * Si occupa delle operazioni CRUD per una risposta.
     */


    private final CommentoRispostaDAO commentoRispostaDAO;

    /**
     * Si occupa delle operazioni CRUD per un libro.
     */


    private final LibroDAO libroDAO;


    /** Implementa la funzionalità visualizzaRecensioni
     * @return recensioni
     */

    @Override
    public List<Recensione> visualizzaRecensioni() {
        List<Recensione> recensioni = recensioneDAO.findAllRecensioni();
        return recensioni;
    }

    /**
     * @param id identificativo per la recensione
     * @return recensione recuperata altrimenti lancia l'eccezione.
     */
    @Override
    public Recensione trovaRecensioneById(final int id) {
        return recensioneDAO.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Recensione non trovato"+ id)
        );
    }

    /**
     * Testa la funzionalità creaRecensione
     * @param recensione
     * @return recensione creata
     */

    @Override
    public Recensione creaRecensione(final Recensione recensione) {
        return this.recensioneDAO.save(recensione);
    }

    /**
     * Implementa la funzionalità modificaRecensione
     * @param recensione
     * @return recensione modificata
     */

    @Override
    public Recensione modificaRecensione(final Recensione recensione) {
        return this.recensioneDAO.save(recensione);
    }

    /**
     * Implementa la funzionalità eliminaRecensione
     * @return recensione eliminata
     */

    @Override
    public Recensione eliminaRecensione(final Recensione recensione) {
        if (recensione != null) {
            List<Commento> commenti = new ArrayList<>(recensione.getCommenti());
            for (Commento commento : commenti) {
                if (commentoDAO.existsById(commento.getId())) {
                    this.eliminaCommento(commento);
                }
            }
        }

        recensioneDAO.deleteById(recensione.getId());
        return recensione;
    }

    /**
     * Implementa la funzionalità visualizzaCommenti
     * @return commenti lista
     */

    @Override
    public List<Commento> visualizzaCommenti() {
       List<Commento> commenti = commentoDAO.findAllCommenti();

       return commenti;
    }

    /**
     * Implementa la funzionalità scriviCommenti
     * @return commento creato
     */

    @Override
    public Commento scriviCommento(final Commento commento) {
        return this.commentoDAO.save(commento);
    }

    /**
     * Implementa la funzionalità eliminaCommento
     * @return commento cancellato
     */

    @Override
    public Commento eliminaCommento(final Commento commento) {
        Optional<Recensione> optionalRecensione = recensioneDAO.findById(commento.getRecensione().getId());

        if (optionalRecensione.isPresent()) {
            Recensione recensione = optionalRecensione.get();
            recensione.getCommenti().remove(commento);
        }

        commentoDAO.deleteById(commento.getId());
        return commento;
    }
    /**
     * Implementa la funzionalità eliminaRisposta
     * @param commento
     * @return risposta al commento cancellato
     */
    @Override
    public CommentoRisposta eliminaRisposta(final CommentoRisposta commento) {
         Optional<Commento> optionalCommentoPadre = commentoDAO.findById(commento.getId());

         if(optionalCommentoPadre.isPresent()) {
             Commento commentoPadre = optionalCommentoPadre.get();
             commentoPadre.getRisposte().remove(commento);
         }

         commentoRispostaDAO.deleteById(commento.getId());
         return commento;
    }

    /**Implementa la funzionalità di rispondere al commento
     * @param risposta
     * @return risposta al commento
     */

    @Override
    public CommentoRisposta rispostaCommento(final CommentoRisposta risposta) {

        return commentoRispostaDAO.save(risposta);
    }

    /**
     * Implementa la funzionalità di trovare la risposta al commento tramite l'id
     * @param idRisposta
     * @return risposta al commento
     */

    @Override
    public Optional<CommentoRisposta> trovaRispostaById(final int idRisposta) {
        return commentoRispostaDAO.findById(idRisposta);
    }

    /** Implementa la funzionalità di trovare il commento tramite l'id
     * @param id
     * @return commento
     */


    @Override
    public Optional<Commento> trovaCommentoById(final int id) {
        return commentoDAO.findById(id);
    }

    /** Implementa la funzionalità di visualizzare le risposte al commento
     * @return risposte
     */

    @Override
    public List<CommentoRisposta> visualizzaRisposte() {
        return commentoRispostaDAO.findAllRisposta();
    }

    /** Implementa la funzionalità di trovare i libri da caricare per la recensione
     * @return risposte
     */

    @Override
    public List<Libro> findAllLibri() {
        return libroDAO.findAllLibro();
    }

    /** Implementa la funzionalità di recuperare un libro dal db
     * @param
     * @return libro
     */

    @Override
    public Libro findLibroById(final int id) {
        return libroDAO.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Libro non trovato"+ id)
        );
    }


}