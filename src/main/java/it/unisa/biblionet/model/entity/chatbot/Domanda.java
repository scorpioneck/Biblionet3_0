package it.unisa.biblionet.model.entity.chatbot;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unisa.biblionet.utils.Length;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties({"faq"}) // Ignora il campo FAQ durante la serializzazione
public class Domanda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idDomanda;

    @NonNull
    @Column(length = Length.LENGTH_255)
    private String contenuto;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "chatbot_id")
    @NonNull
    @JsonBackReference
    @ToString.Exclude
    private ChatBot chatbot;

    @Column(length = Length.LENGTH_30)
    private String mapLink;

    @OneToMany(mappedBy = "domandaPadre", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DomandaRisposta> risposte;

}
