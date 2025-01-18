package it.unisa.biblionet.model.entity.blog;

import it.unisa.biblionet.utils.Length;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, length = Length.LENGTH_30)
    @NonNull
    private String title;

    @Column(nullable = false, length = Length.LENGTH_255)
    @NonNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "recensione_id")
    @NonNull
    private Recensione recensione;

    @OneToMany(mappedBy = "commentoPadre",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CommentoRisposta> risposte;

    @NonNull
    private String utente;

}

