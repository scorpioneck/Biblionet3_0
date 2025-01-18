package it.unisa.biblionet.utils.session;

import it.unisa.biblionet.model.entity.chatbot.Domanda;
import it.unisa.biblionet.model.entity.chatbot.DomandaRisposta;
import lombok.Data;

import java.util.List;

// Classe per incapsulare i dati della sessione
@Data
public class SessionData {
    private List<ChatSession.Message> messages;
    private String faseCorrente;
    private Integer ultimaDomanda;
    private Integer ultimaRisposta;

    private List<Domanda> domande; // Domande da visualizzare (se fase è DISPLAY_DOMANDE)
    private List<DomandaRisposta> risposte; // Risposte da visualizzare (se fase è DISPLAY_RISPOSTE)
    private boolean utenteValido;
}