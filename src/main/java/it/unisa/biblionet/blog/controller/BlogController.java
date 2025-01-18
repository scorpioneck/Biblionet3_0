package it.unisa.biblionet.blog.controller;


import it.unisa.biblionet.blog.service.BlogService;
import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import it.unisa.biblionet.model.entity.blog.Recensione;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.entity.utente.Lettore;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import it.unisa.biblionet.model.form.CommentoForm;
import it.unisa.biblionet.model.form.RecensioneForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * Implementa il controller per il sottosistema
 * del blog.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/blog")
@SessionAttributes("loggedUser")
public class BlogController {

    /**
     * Il service per effettuare le operazioni di persistenza.
     */

    private final BlogService blogService;

    /**
     * Metodo di utilità che serve per caricare le recensioni del blog.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return la view inserita.
     */


    @RequestMapping(value = "",method = RequestMethod.GET)
    public String visualizzaRecensioni(final Model model) {

        List<Recensione> recensioneList = blogService.visualizzaRecensioni();

         if(recensioneList == null || recensioneList.isEmpty()){
             model.addAttribute("messaggio", "Non ci sono recensioni");
         }else {
             model.addAttribute("recensioni", recensioneList);
         }

    return "blog/visualizza-recensioni";
}

    /**
     * Metodo di utilità che serve per vusualizzare la recensione selezionata
     * del blog.
     * @param id necessario per individuare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return la view inserita.
     */

    @RequestMapping(value = "/{id}/visualizzaRecensione",method = RequestMethod.GET)
    public String visualizzaRecensione(@PathVariable int id, final Model model) {

        Recensione recensione = blogService.trovaRecensioneById(id);
        CommentoForm commentoForm = new CommentoForm();
        List<CommentoRisposta> rispostaList = blogService.visualizzaRisposte();


        model.addAttribute("commentoForm", commentoForm);
        model.addAttribute("recensione", recensione);
        model.addAttribute("commento", new Commento());
        model.addAttribute("commentoRisposta", new CommentoRisposta());
        model.addAttribute("commentoRispostaList", rispostaList);

        return "blog/visualizza-singola-rec";

    }

    /**
     * Implementa la funzionalità di visualizzare la pagina di creazione di
     * una recesione
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param recensioneForm   Il form in cui inserire i dati della recensione
     * @return la view inserita.
     */

    @RequestMapping(value= "/inizializzaCreaR",method = RequestMethod.GET)
    public String inizializzacreaRecensione(final Model model, @ModelAttribute RecensioneForm recensioneForm) {

        var utente = (UtenteRegistrato) model.getAttribute(("loggedUser"));
        if (utente == null || !utente.getTipo().equals("Esperto")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<Libro> libroList = blogService.findAllLibri();

        if(libroList == null || libroList.isEmpty()){
            libroList = new ArrayList<>();
            LocalDateTime specificDateTime = LocalDateTime.of(2021, 12, 30, 14, 30); // 30 di
            libroList.add(new Libro("non si è trovato","un cazzo","ritenta",specificDateTime,"nulla","non esiste"));
        }

        model.addAttribute("recensione", recensioneForm);
        model.addAttribute("listaLibri", libroList);

        return "blog/inserimento-recensione";
    }

    /**
     * Funzionalità di creazione di una recensione
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param recensioneForm   Il form in cui inserire i dati della recensione
     * @param idLibro per selezionare l'id da recensire.
     * @return la pagina del blog .
     */


    @RequestMapping(value= "/creaRecensione",method = RequestMethod.POST)
    public String creaRecensione(final Model model, @ModelAttribute RecensioneForm recensioneForm
    ,@RequestParam("idLibro") final int idLibro) {
        var utente = (UtenteRegistrato) model.getAttribute(("loggedUser"));

        if (utente == null || !utente.getTipo().equals("Esperto")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // Controllo sulla lunghezza del titolo
        if (recensioneForm.getTitolo() == null || recensioneForm.getTitolo().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titolo troppo lungo");
        }

        // Controllo sulla lunghezza della descrizione
        if (recensioneForm.getDescrizione() == null || recensioneForm.getDescrizione().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrizione troppo lunga");
        }

        Recensione recensione = new Recensione();

        var esperto = (Esperto) utente;
        Libro libro = blogService.findLibroById(idLibro);

        recensione.setDescrizione(recensioneForm.getDescrizione());
        recensione.setTitolo(recensioneForm.getTitolo());
        recensione.setEsperto(esperto);
        if(libro != null) {
            recensione.setLibro(libro);
        }
        if(recensione != null) {
            blogService.creaRecensione(recensione);
        }

        return "redirect:/blog" ;
    }

    /**
     * Funzionalità di creazione di una recensione
     * @param id l'id necessaria per trovare la recensione selezionata
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param recensioneForm   Il form in cui inserire i dati della recensione.
     * @return il form per inserire i dati .
     */

    @RequestMapping(value= "{id}/inizializzaModificaR",method = RequestMethod.GET)
    public String inizializzaModificaRecensione(final Model model,@PathVariable int id ,@ModelAttribute RecensioneForm recensioneForm) {

        var utente = (UtenteRegistrato) model.getAttribute(("loggedUser"));
        if (utente == null || !utente.getTipo().equals("Esperto")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<Libro> libroList = new ArrayList<>();

        Recensione recensione = blogService.trovaRecensioneById(id);
        recensioneForm.setTitolo(recensione.getTitolo());
        recensioneForm.setDescrizione(recensione.getDescrizione());

        model.addAttribute("recensione", recensioneForm);
        model.addAttribute("id",id);
        model.addAttribute("listaLibri", libroList);

        return "blog/modifica-recensione";
    }

    /**
     * Funzionalità di modifica della recensione
     * @param id necessaria per modificare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param recensioneForm   Il form in cui inserire i dati della recensione.
     * @param idLibro obsoleto da rimuovere...
     * @return la pagina del blog.
     */


    @RequestMapping(value= "{id}/modificaRecensione",method = RequestMethod.POST)
    public String gestisciRecensione(@PathVariable final int id,final Model model,
                                     @ModelAttribute RecensioneForm recensioneForm,
                                     @RequestParam("idLibro") final int idLibro) {

        Consumer<Recensione> operazione = recensione -> {
            // Operazioni personalizzate su recensione
            blogService.modificaRecensione(recensione);
        };


        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");
        if (utente == null || !utente.getTipo().equals("Esperto")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // Controllo sulla lunghezza del titolo
        if (recensioneForm.getTitolo() == null || recensioneForm.getTitolo().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Titolo troppo lungo");
        }

        // Controllo sulla lunghezza della descrizione
        if (recensioneForm.getDescrizione() == null || recensioneForm.getDescrizione().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrizione troppo lunga");
        }

        Recensione recensione = blogService.trovaRecensioneById(id);
        recensione.setTitolo(recensioneForm.getTitolo());
        recensione.setDescrizione(recensioneForm.getDescrizione());

        operazione.accept(recensione);

        return "redirect:/blog";
    }


    /**
     * Funzionalità di gestione del commento, serve per permettere all'utente
     * di scrivere un commento
     * @param id necessaria per visualizzare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param commento  Il form in cui inserire i dati del commento.
     * @return la pagina della recensione
     */

    @RequestMapping(value= "/{id}/gestisciCommento",method = RequestMethod.POST)
    public String gestisciCommento(@PathVariable int id, final Model model, @ModelAttribute CommentoForm commento) {
    var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if (utente == null || (!utente.getTipo().equals("Esperto") && !utente.getTipo().equals("Lettore"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // Controllo sulla lunghezza della descrizione
        if (commento.getDescription() == null || commento.getDescription().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrizione del commento troppo lunga");
        }

        Recensione recensione = blogService.trovaRecensioneById(id);

        Commento com = new Commento();
        com.setDescription(commento.getDescription());
        com.setRecensione(recensione);
        com.setUtente(utente.getEmail());

        if(utente.getTipo().equals("Esperto")){
            Esperto esperto = (Esperto) model.getAttribute("loggedUser");
            com.setTitle(esperto.getUsername());
        }else{
            Lettore lettore = (Lettore) model.getAttribute("loggedUser");
            com.setTitle(lettore.getUsername());
        }

        blogService.scriviCommento(com);

        model.addAttribute("commentoForm", new CommentoForm());
        model.addAttribute("recensione", recensione);
        model.addAttribute("commento", new Commento());

        return "redirect:/blog/"+id+"/visualizzaRecensione";
    }


    /**
     * Funzionalità di gestione del commento, serve per permettere all'utente
     * di rispondere al commento
     * @param id necessaria per visualizzare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param commentoPadreId  Il form in cui inserire i dati del commento.
     * @return la pagina di visualizza recensione .
     */


    @RequestMapping(value= "/{id}/gestisciRisposta",method = RequestMethod.POST)
    public String gestisciCommentoRisposta(@PathVariable int id, final Model model, @ModelAttribute CommentoForm commento,
                                           @RequestParam("commentoPadreId") Integer commentoPadreId) {

        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if (utente == null || (!utente.getTipo().equals("Esperto") && !utente.getTipo().equals("Lettore"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // Controllo sulla lunghezza della descrizione
        if (commento.getDescription() == null || commento.getDescription().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrizione del commento troppo lunga");
        }


        Recensione recensione = blogService.trovaRecensioneById(id);
        CommentoRisposta risposta = new CommentoRisposta();
        Optional<Commento> commentoOptional = blogService.trovaCommentoById(commentoPadreId);
        if(commentoOptional.isPresent()) {
            Commento com2 = commentoOptional.get();

            risposta.setDescription(commento.getDescription());
            risposta.setCommentoPadre(com2);
            risposta.setUtente(utente.getEmail());

            if(utente.getTipo().equals("Esperto")){
                Esperto esperto = (Esperto) model.getAttribute("loggedUser");
                risposta.setTitle(esperto.getUsername());
            }else{
                Lettore lettore = (Lettore) model.getAttribute("loggedUser");
                risposta.setTitle(lettore.getUsername());
            }

            blogService.rispostaCommento(risposta);
        }

        model.addAttribute("commentoForm", new CommentoForm());
        model.addAttribute("recensione", recensione);
        model.addAttribute("commentoRisposta", new CommentoRisposta());

        return "redirect:/blog/"+id+"/visualizzaRecensione";
    }

    /**
     * Funzionalità che permette di cancellare un commento
     * @param id necessaria per trovare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param idCommento  l'id per trovare il commento.
     * @return la pagina di visualizza recensione .
     */


    @RequestMapping(value= "{id}/cancellaCommento", method = RequestMethod.POST)
    public String cancellaCommenti(@PathVariable final int id,final Model model,
                                   @RequestParam("idCommento") final int idCommento) {

        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if(utente == null || !utente.getTipo().equals("Esperto")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Commento> commentoOptional = blogService.trovaCommentoById(idCommento);

        if (commentoOptional.isPresent()) {
         Commento commento = commentoOptional.get();
            blogService.eliminaCommento(commento);
        }else{
            return "redirect:/blog/" + id + "/visualizzaRecensione";
        }


    return "redirect:/blog/"+id+"/visualizzaRecensione";
    }

    /**
     * Funzionalità che permette di cancellare un commento
     * @param id necessaria per trovare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @param idCommentoRisposta per trovare la risposta.
     * @return la pagina di visualizza recensione .
     */


    @RequestMapping(value= "{id}/cancellaCommentoRisposta", method = RequestMethod.POST)
    public String cancellaRisposta(@PathVariable final int id,final Model model,
                                           @RequestParam("idCommentoRisposta") final int idCommentoRisposta) {

        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if(utente == null || !utente.getTipo().equals("Esperto")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Optional<CommentoRisposta> comRispostaOptional = blogService.trovaRispostaById(idCommentoRisposta);

        if(comRispostaOptional.isPresent()){
            CommentoRisposta comRisposta = comRispostaOptional.get();
            blogService.eliminaRisposta(comRisposta);
        }
        else{
            return "redirect:/blog/" + id + "/visualizzaRecensione";
        }

        return "redirect:/blog/"+id+"/visualizzaRecensione";
    }

    /**
     * Funzionalità che permette di cancellare una Recensione
     * @param id necessaria per trovare la recensione selezionata.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return la pagina del blog .
     */



    @RequestMapping(value= "{id}/cancellaRecensione", method = RequestMethod.POST)
        public String cancellaRecensione(@PathVariable final int id,final Model model) {

          var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

           if(utente == null || !utente.getTipo().equals("Esperto")){
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
           }

           Recensione recensione = blogService.trovaRecensioneById(id);
           blogService.eliminaRecensione(recensione);

           return "redirect:/blog";
    }

}
