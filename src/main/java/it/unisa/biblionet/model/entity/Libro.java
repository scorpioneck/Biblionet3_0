package it.unisa.biblionet.model.entity;

import it.unisa.biblionet.model.entity.blog.Recensione;
import it.unisa.biblionet.utils.Length;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Questa classe rappresenta un libro.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Libro {

    /**
     * Rappresenta l'ID autogenerato di un libro.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idLibro;

    /**
     * Rappresenta il titolo di un libro.
     */
    @NonNull
    @Column(length = Length.LENGTH_90)
    private String titolo;

    /**
     * Rappresenta l'autore di un libro.
     */
    @NonNull
    @Column(length = Length.LENGTH_60)
    private String autore;

    /**
     * Rappresenta il codice ISBN di un libro se presente.
     */
    @Column(unique = true, length = Length.LENGTH_13)
    @NonNull
    private String isbn;

    /**
     * Rappresenta l'anno di pubblicazione di un libro.
     */
    @Column(nullable = false)
    @NonNull
    private LocalDateTime annoDiPubblicazione;

    /**
     * Rappresenta la descrizione di un libro.
     */
    @Column(nullable = false, length = Length.LENGTH_144)
    @NonNull
    private String descrizione;

    /**
     * Rappresenta la casa editrice di un libro.
     */
    @Column(nullable = false, length = Length.LENGTH_30)
    @NonNull
    private String casaEditrice;

    /**
     * Rappresenta l'immagine di copertina del libro.
     */
    @Lob
    @ToString.Exclude
    private String immagineLibro;

    /**
     * Rappresenta i tickets di cui fa parte il libro.
     */
    @OneToMany(mappedBy = "libro")
    @ToString.Exclude
    private List<TicketPrestito> tickets;

    /**
     * Rappresenta i generi di un libro.
     */
    @ManyToMany
    @ToString.Exclude
    private List<Genere> generi;

    /**
     * Rappresente gli eventi di cui un libro è parte.
     */
    @OneToMany(mappedBy = "libro")
    @ToString.Exclude
    private List<Evento> eventi;

    /**
     * Rappresenta la relazione di possesso con una biblioteca.
     */
    @OneToMany(mappedBy = "possessoID.libroID")
    @ToString.Exclude
    private List<Possesso> possessi;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Recensione> recensioni;
}
