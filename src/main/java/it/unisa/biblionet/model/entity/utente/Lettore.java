package it.unisa.biblionet.model.entity.utente;

import it.unisa.biblionet.model.entity.ClubDelLibro;
import it.unisa.biblionet.model.entity.Evento;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.TicketPrestito;
import it.unisa.biblionet.utils.Length;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.util.List;

/**
 * Questa classe rappresenta un Lettore.
 * Un Lettore può essere interessato a più generi,
 * può partecipare a più eventi,
 * e far parte di più club.
 */
@Entity
@SuperBuilder
@Data
@NoArgsConstructor
public class Lettore extends UtenteRegistrato implements HaGenere {

    /**
     * Rappresenta un lettore sulla piattaforma.
     */
    @NonNull
    @Column(nullable = false, length = Length.LENGTH_30)
    private String username;

    /**
     * Rappresenta il nome del lettore.
     */
    @NonNull
    @Column(nullable = false, length = Length.LENGTH_30)
    private String nome;

    /**
     * Rappresenta il cognome di un lettore.
     */
    @NonNull
    @Column(nullable = false, length = Length.LENGTH_30)
    private String cognome;

    /**
     * Rappresenta i generi che interessano ad un lettore.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Genere> generi;

    /**
     * Rappresenta i clubs a cui il lettore appartiene.
     */
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @ToString.Exclude
    private List<ClubDelLibro> clubs;

    /**
     * Rappresenta gli eventi a cui prende parte.
     */
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Evento> eventi;

    /**
     * Rappresenta i tickets a cui è collegato.
     */
    @OneToMany
    @ToString.Exclude
    private List<TicketPrestito> tickets;

    /**
     * Rappresenta il tipo di utente.
     */
    @Transient
    private String tipo = "Lettore";


    /**
     *
     * @param email la email del lettore.
     * @param password la password del lettore.
     * @param provincia la provincia dove vive
     * @param citta la città del lettore.
     * @param via la via dove vive.
     * @param recapitoTelefonico il recapito del lettore.
     * @param username l'usurname del lettore.
     * @param nome il nome del lettore.
     * @param cognome il cognome del lettore.
     */
    public Lettore(final String email, final String password,
                   final String provincia, final String citta,
                   final String via, final String recapitoTelefonico,
                   final String username, final String nome,
                   final String cognome) {
        super(email, password, provincia, citta, via, recapitoTelefonico);
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            Lettore lettore = (Lettore) obj;
            return (this.getEmail().equals(lettore.getEmail())
                    && this.getUsername().equals(lettore.getUsername())
                    && this.getCognome().equals(lettore.getCognome())
                    && this.getNome().equals(lettore.getNome()));
        }
        return false;
    }


}
