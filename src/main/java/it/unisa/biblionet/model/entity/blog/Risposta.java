package it.unisa.biblionet.model.entity.blog;

import it.unisa.biblionet.utils.Length;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    @Column(nullable = false,length = Length.LENGTH_30)
    private String title;                                                  //Cambiare in username

    @NonNull
    @Column(nullable = false, length = Length.LENGTH_255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "commentoPadre_id")
    @NonNull
    private Commento commentoPadre;

    @NonNull
    private String idUtente;

}
