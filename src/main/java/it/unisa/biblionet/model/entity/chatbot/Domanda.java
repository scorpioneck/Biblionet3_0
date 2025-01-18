package it.unisa.biblionet.model.entity.chatbot;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Domanda {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idDomanda;

    @NonNull
    @Size(min = 1, max = 2000)
    private String contenuto;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "rispostaPadre_id")
    @NonNull
    @JsonBackReference
    @ToString.Exclude
    private  Risposta rispostaPadre;
}
