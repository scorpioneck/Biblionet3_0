package it.unisa.biblionet.prenotazioneLibri.controller;


import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import it.unisa.biblionet.model.form.LibroForm;
import it.unisa.biblionet.prenotazioneLibri.service.PrenotazioneLibriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Implementa il controller per il sottosistema
 * PrenotazioneLibri, in particolare la gestione
 * delle Biblioteche.
 */
@SessionAttributes("loggedUser")
@Controller
@RequiredArgsConstructor
@RequestMapping("/biblioteca")
public class BibliotecaController {

    /**
     * Il service per effettuare le operazioni di
     * persistenza.
     */
    private final PrenotazioneLibriService prenotazioneService;

    /**
     * Implementa la funzionalità che permette di
     * visualizzare tutte le biblioteche iscritte.
     * @param model Il model in cui salvare la lista
     * @return La view per visualizzare le biblioteche
     */
    @RequestMapping(value = "/visualizza-biblioteche",
            method = RequestMethod.GET)
    public String visualizzaListaBiblioteche(final Model model) {

        model.addAttribute("listaBiblioteche",
                prenotazioneService.getAllBiblioteche());

        return "/biblioteca/visualizza-lista-biblioteche";
    }

    /**
     * Implementa la funzionalità che permette di
     * visualizzare la pagina per l'inserimento di
     * nuovi libri prenotabili.
     * @param model Il model per recuperare l'utente
     * @return La view
     */
    @RequestMapping(value = "/inserisci-nuovo-libro",
                            method = RequestMethod.GET)
    public String visualizzaInserimentoLibro(final Model model) {

        UtenteRegistrato utente =
                (UtenteRegistrato) model.getAttribute("loggedUser");
        if (utente == null || utente.getTipo() != "Biblioteca") {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<Libro> listaLibri =
                prenotazioneService.visualizzaListaLibriCompleta();
        model.addAttribute("listaLibri", listaLibri);

        List<Genere> listaGeneri = prenotazioneService.getAllGeneri();
        model.addAttribute("listaGeneri", listaGeneri);

        return "/biblioteca/inserimento-nuovo-libro-prenotabile";
    }

    /**
     * Implementa la funzionalità che permette inserire
     * un libro tramite l'isbn ed una Api di Google.
     * @param isbn l'isbn del libro
     * @param generi la lista dei generi del libro
     * @param numCopie il numero di copie possedute
     * @param model Il model per recuperare l'utente
     * @return La view per visualizzare il libro
     */
    @RequestMapping(value = "/inserimento-isbn",
                        method = RequestMethod.POST)
    public String inserisciPerIsbn(final Model model,
                                   @RequestParam final String isbn,
                                   @RequestParam final String[] generi,
                                   @RequestParam final int numCopie) {

        if (isbn == null) {
            return "redirect:/biblioteca/inserisci-nuovo-libro";
        }
        UtenteRegistrato utente =
                (UtenteRegistrato) model.getAttribute("loggedUser");
        if (utente == null || utente.getTipo() != "Biblioteca") {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Biblioteca b = (Biblioteca) utente;
        List<String> glist = Arrays.asList(generi.clone());
        Libro l = prenotazioneService.inserimentoPerIsbn(
                isbn, b.getEmail(), numCopie, glist);
        if (l == null) {
            return "redirect:/biblioteca/inserisci-nuovo-libro";
        }
        return "redirect:/prenotazione-libri/" + l.getIdLibro()
                + "/visualizza-libro";
    }

    /**
     * Implementa la funzionalità che permette inserire
     * un libro alla lista dei possessi preso
     * dal db.
     * @param idLibro l'ID del libro
     * @param numCopie il numero di copie possedute
     * @param model Il model per recuperare l'utente
     * @return La view per visualizzare il libro
     */
    @RequestMapping(value = "/inserimento-archivio",
                        method = RequestMethod.POST)
    public String inserisciDaDatabase(final Model model,
                                   @RequestParam final int idLibro,
                                   @RequestParam final int numCopie) {

        UtenteRegistrato utente =
                (UtenteRegistrato) model.getAttribute("loggedUser");
        if (utente == null || utente.getTipo() != "Biblioteca") {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Biblioteca b = (Biblioteca) utente;
        Libro l = prenotazioneService.inserimentoDalDatabase(
                idLibro, b.getEmail(), numCopie);
        return "redirect:/prenotazione-libri/" + l.getIdLibro()
                + "/visualizza-libro";
    }

    /**
     * Implementa la funzionalità che permette inserire
     * un libro manualmente tramite form.
     * @param model Il model per recuperare l'utente
     * @param libro Il libro da salvare
     * @param numCopie il numero di copie possedute
     * @param annoPubblicazione l'anno di pubblicazione
     * @return La view per visualizzare il libro
     */
    @RequestMapping(value = "/inserimento-manuale",
                        method = RequestMethod.POST)
    public String inserisciManualmente(final Model model,
                                       final LibroForm libro,
                                       final int numCopie,
                                       final String annoPubblicazione) {

        UtenteRegistrato utente =
                (UtenteRegistrato) model.getAttribute("loggedUser");
        if (utente == null || utente.getTipo() != "Biblioteca") {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Biblioteca b = (Biblioteca) utente;
        Libro l = new Libro();
        l.setTitolo(libro.getTitolo());
        if (libro.getIsbn() != null) {
            l.setIsbn(libro.getIsbn());
        }
        if (libro.getDescrizione() != null) {
            l.setDescrizione(libro.getDescrizione());
        }
        l.setCasaEditrice(libro.getCasaEditrice());
        l.setAutore(libro.getAutore());
        if (libro.getImmagineLibro() != null) {
            try {
                byte[] imageBytes = libro.getImmagineLibro().getBytes();
                String base64Image =
                        Base64.getEncoder().encodeToString(imageBytes);
                l.setImmagineLibro(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LocalDateTime anno = LocalDateTime.of(
                Integer.parseInt(annoPubblicazione), 1, 1, 1, 1);
        l.setAnnoDiPubblicazione(anno);
        Libro newLibro = prenotazioneService.inserimentoManuale(
                l, b.getEmail(), numCopie, libro.getGeneri());
        return "redirect:/prenotazione-libri/" + newLibro.getIdLibro()
                + "/visualizza-libro";
    }

    /**
     * Implementa la funzionalità che permette di
     * visualizzare le biblioteche filtrate.
     *
     * @param stringa La stringa di ricerca
     * @param filtro  L'informazione su cui filtrare
     * @param model   Il model per salvare la lista
     * @return La view che visualizza la lista
     */
    @RequestMapping(value = "/ricerca", method = RequestMethod.GET)
    public String visualizzaListaFiltrata(
            @RequestParam("stringa") final String stringa,
            @RequestParam("filtro") final String filtro,
            final Model model) {

        switch (filtro) {
            case "nome":
                model.addAttribute("listaBiblioteche", prenotazioneService.
                        getBibliotecheByNome(stringa));
                break;
            case "citta":
                model.addAttribute("listaBiblioteche", prenotazioneService.
                        getBibliotecheByCitta(stringa));
                break;
            default:
                model.addAttribute("listaBiblioteche",
                        prenotazioneService.getAllBiblioteche());
                break;
        }
        return "biblioteca/visualizza-lista-biblioteche";
    }

    /**
     * Implementa la funzionalitá di visualizzazione
     * del profilo di una singola biblioteca.
     * @param email della biblioteca
     * @param model Per salvare la biblioteca
     * @return La view di visualizzazione singola biblioteca
     */
    @RequestMapping(value = "/{email}",
            method = RequestMethod.GET)
    public String visualizzaDatiBiblioteca(final @PathVariable String email,
                                           final Model model) {
        model.addAttribute("biblioteca",
                prenotazioneService.getBibliotecaById(email));
        return "biblioteca/visualizza-singola-biblioteca";
    }
}
