package it.unisa.biblionet.utils.session;

import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSession implements Serializable {

    private List<Message> visibleMessages = new ArrayList<>(); // Messaggi visibili nella chat
    private List<PhaseMessage> phaseMessages = new ArrayList<>(); // Messaggi usati per la gestione delle fasi

    private String currentFase; // Es. "CHOICE", "ANSWER", "END"
    private Integer ultimaDomandaId; // ID dell'ultima domanda selezionata
    private Integer ultimaRispostaId; // ID dell'ultima risposta selezionata (se necessario)

    private UtenteRegistrato utenteCorrente;

    // ------------------------
    // Messaggi visibili
    // ------------------------
    public void addIncomingMessage(String testo) {
        visibleMessages.add(new Message("incoming", testo));
    }

    public void addOutgoingMessage(String testo) {
        visibleMessages.add(new Message("outgoing", testo));
    }

    // ------------------------
    // Messaggi di fase
    // ------------------------
    public void addPhaseMessage(String fase, Integer domandaId, Integer rispostaId) {
        phaseMessages.add(new PhaseMessage(fase, domandaId, rispostaId));
    }

    // Aggiorna lo stato e l'utente corrente
    public void aggiornaStato(String fase, Integer domandaId, Integer rispostaId, UtenteRegistrato utente) {
        this.currentFase = fase;
        this.ultimaDomandaId = domandaId;
        this.ultimaRispostaId = rispostaId;
        this.utenteCorrente = utente; // Aggiorna l'utente corrente
    }

    public void resetSession() {
        this.visibleMessages.clear();
        this.phaseMessages.clear();
        this.currentFase = null;
        this.ultimaDomandaId = null;
        this.ultimaRispostaId = null;
        this.utenteCorrente = null; // Reset dell'utente corrente
    }

    // ------------------------
    // Classi interne
    // ------------------------

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message implements Serializable {
        private String type; // "incoming" o "outgoing"
        private String text; // Testo del messaggio
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhaseMessage implements Serializable {
        private String fase; // Nome della fase
        private Integer domandaId; // ID della domanda (opzionale)
        private Integer rispostaId; // ID della risposta (opzionale)
    }
}
