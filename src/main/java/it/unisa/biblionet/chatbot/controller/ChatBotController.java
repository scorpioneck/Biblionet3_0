package it.unisa.biblionet.chatbot.controller;

import it.unisa.biblionet.chatbot.service.ChatBotService;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.chatbot.*;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Implementa il controller per il sottosistema
 * del chatbot.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/bot")
@SessionAttributes("loggedUser")
public class ChatBotController {

    /**
     Il service per effettuare le operazioni di persistenza.
     */

    private final ChatBotService chatBotService;

    /**
     * Serve per verificare che l'utente si sia loggato e passare la richiesta allo script corrispondente.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return una responeEntity con i dati necessari allo script corrispondente.
     */

        @RequestMapping(value = "/verificaUtente", method = RequestMethod.GET)
        public ResponseEntity<?> verificaUtente(final Model model) {
            UtenteRegistrato utente = (UtenteRegistrato) model.getAttribute("loggedUser");

            if (utente == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("logged", false, "message", "Utente non loggato."));
            }

            return ResponseEntity.ok(Map.of(
                    "logged", true,
                    "nome", utente.getEmail(),
                    "tipo", utente.getTipo()
            ));
        }

    /**
     * Serve per caricare le risposte che il chatbot deve fornire all'utente
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return la view con le risposte
     */

    @RequestMapping(value = "/risposte",method = RequestMethod.GET)
    public String loadChatBot(final Model model) {

        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if (utente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utente non loggato.");
        }

        if(utente.getTipo().equals("Lettore")) {
            ChatBot chatBLettore = chatBotService.identificaUtente(1);
            model.addAttribute("chatB", chatBLettore);
            List<Risposta> risposte = chatBLettore.getRisposta2();
            model.addAttribute("risposte", risposte);

        }else if(utente.getTipo().equals("Esperto")) {
            ChatBot chatBEsperto = chatBotService.identificaUtente(2);
            model.addAttribute("chatB", chatBEsperto);
            List<Risposta> risposte = chatBEsperto.getRisposta2();
            model.addAttribute("risposte", risposte);
        }

        return "chatbot/risposteFragment :: risposteList";
    }

    /**
     * Serve per caricare la possibile risposta alla domanda che l'utente potrebbe porsi
     * @param idRisposta  per identificare la risposta selezionata
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return la view con le risposte alle possibili domande
     */

    @RequestMapping(value = "/{idRisposta}/domanda", method = RequestMethod.GET)
    public String getRisposte(@PathVariable int idRisposta, Model model) {

        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if (utente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utente non loggato.");
        }

        if (!utente.getTipo().equals("Lettore") && !utente.getTipo().equals("Esperto")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tipo di utente non autorizzato.");
        }

            // Recupera la domanda dal database
            Risposta risposta = chatBotService.generaRisposta(idRisposta);
            List<Domanda> domande = risposta.getDomande();

            model.addAttribute("domande", domande);


        return  "chatbot/domandeFragment :: domandeList";
    }


    /**
     * Serve per iniziare il questionare per determinare il genere preferito..
     * @param session per registrare gli attributi di sessione.
     * @param model  L'oggetto model usato per inserire gli attributi
     * @return la view con le risposte alle possibili domande
     */


    @RequestMapping(value = "/questionario/inizia", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> caricaPrimaOpzione(final Model model,final HttpSession session) {
        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");

        if (utente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utente non loggato.");
        }

        ChatBot chatBLettore = utente.getTipo().equals("Lettore") ?
                chatBotService.identificaUtente(1) : chatBotService.identificaUtente(2);

        List<Integer> opzioniId = new ArrayList<>();
        session.setAttribute("opzioniId", opzioniId);
        List<Risposta> questionari = chatBLettore.getRisposta1();

        if (questionari.isEmpty()) {
            return ResponseEntity.ok(Map.of("questionarioCompletato", true));
        }

        session.setAttribute("chatBot",chatBLettore);

        // Restituisce la prima domanda
        Risposta primaRisposta = questionari.get(0);
        return ResponseEntity.ok(Map.of(
                "prossimaRisposta", primaRisposta,
                "indiceRisposta", 0,
                "questionarioCompletato", false
        ));
    }

    /**
     * Restituisce la prossima risposta alla domanda in base all'ID della risposta selezionata.
     @param indiceRisposta per memorizzare la risposta precedente.
     @param opzioneId per memorizza la risposta alla possibile domanda dell'utente.
     return ResponseEntity con i parametri da passare allo script corrispondente.
     */

    @RequestMapping(value = "/nextDomanda/{indiceRisposta}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getNextOpzione(
            final HttpSession session,
            @PathVariable(value = "indiceRisposta") int indiceRisposta,
            @RequestParam(value = "domandaId", required = false) Integer opzioneId) {

        ChatBot chatBot = (ChatBot) session.getAttribute("chatBot");
        if (chatBot == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ChatBot non trovato nella sessione.");
        }

        indiceRisposta++;

        List<Integer> opzioniId = (List<Integer>) session.getAttribute("opzioniId");
        opzioniId.add(opzioneId);

        List<Risposta> questionari = chatBot.getRisposta1();

        if (indiceRisposta >= questionari.size()) {
            return ResponseEntity.ok(Map.of("questionarioCompletato", true));
        }

        Risposta prossimaRisposta= questionari.get(indiceRisposta);
        return ResponseEntity.ok(Map.of(
                "prossimaRisposta", prossimaRisposta,
                "indiceRisposta", indiceRisposta,
                "questionarioCompletato", false
        ));
    }

    /**
     * Calcola il genere predominante in base alle risposte selezionate.
     * @param model contiene i vari attributi di sessione
     * @param session contiene i vari attributi della sessione.
     * return ResponseEntity con il genere preferito dell'utente
     */

    @RequestMapping(value = "/calcolaGenere", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> calcolaGenere(
            final Model model,HttpSession session) {

        // Recupera l'utente loggato
        var utente = (UtenteRegistrato) model.getAttribute("loggedUser");
        if (utente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utente non loggato.");
        }

        List<Integer> opzioniId = (List<Integer>) session.getAttribute("opzioniId");

        if (opzioniId == null || opzioniId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessuna opzione selezionata.");
        }

        // Calcola il genere predominante
        Genere genere = chatBotService.calcolaGenerePreferito(opzioniId);

        return ResponseEntity.ok("Il genere preferito è: " + genere.getNome());
    }

}