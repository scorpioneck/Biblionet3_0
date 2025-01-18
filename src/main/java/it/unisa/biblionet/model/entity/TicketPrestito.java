package it.unisa.biblionet.model.entity;

import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Lettore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Questa classe rappresenta un Ticket Prestito.
 * Un ticket di prestito posside un ID che lo identifica,
 * uno stato che rapprenseta lo stato del prestito
 * e uan data di richiesta e restituzione del libro.
 * Un ticket è correlato al libro di cui si richiede il prestito,
 * alla biblioteca da cui si prende in prestito,
 * e il lettore che lo prende.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class TicketPrestito {

    /**
     * Rappresenta l'ID autogenerato di un ticket.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idTicket;

    /**
     * Rappresenta i vari stati che può avere un ticket.
     */
    public enum Stati {

        /**
         * Rappresenta lo stato In attesa di conferma di un TicketPrestito.
         */
        IN_ATTESA_DI_CONFERMA,

        /**
         * Rappresenta lo stato In attesa di restituzione di un TicketPrestito.
         */
        IN_ATTESA_DI_RESTITUZIONE,

        /**
         * Rappresenta lo stato Chiuso di un TicketPrestito.
         */
        CHIUSO,

        /**
         * Rappresenta lo stato Rifiutato di un TicketPrestito.
         */
        RIFIUTATO }

    /**
     * Rappresenta lo stato del ticket.
     */
    @NonNull
    @Column(nullable = false)
    private Stati stato;

    /**
     * Rappresenta la data in cui è stata fatta la richiesta di prestito.
     */
    @NonNull
    @Column(nullable = false)
    private LocalDateTime dataRichiesta;

    /**
     * Rappresenta la data di restituzione del libro.
     */
    private LocalDateTime dataRestituzione;

    /**
     * Rappresenta il libro che si prende il prestito.
     */
    @NonNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Libro libro;

    /**
     * Rappresenta la biblioteca da cui si prende il prestito il libro.
     */
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @ToString.Exclude
    private Biblioteca biblioteca;

    /**
     * Rappresenta il lettore che prende in prestito il libro.
     */
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @ToString.Exclude
    private Lettore lettore;

}
