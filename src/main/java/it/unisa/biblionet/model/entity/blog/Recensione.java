package it.unisa.biblionet.model.entity.blog;

import it.unisa.biblionet.model.entity.Libro;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.utils.Length;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Recensione{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, length = Length.LENGTH_30)
    @NonNull
    private String titolo;

    @Column(nullable = false, length = Length.LENGTH_255)
    @NonNull
    private String descrizione;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    @NonNull
    private Libro libro;

    @OneToMany(mappedBy = "recensione")
    private List<Commento> commenti;

    @ManyToOne
    @JoinColumn(name = "esperto_id")
    @NonNull
    private Esperto esperto;
}