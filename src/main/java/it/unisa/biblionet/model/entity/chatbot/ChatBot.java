package it.unisa.biblionet.model.entity.chatbot;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unisa.biblionet.utils.Length;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ChatBot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @NonNull
    @Column(length = Length.LENGTH_30)
    private String titolo;

    @NonNull
    private boolean isEsperto;

    @OneToMany(mappedBy = "chatbot")
    @Where(clause = "categoria != 'QUESTIONARIO'") // Domande che NON sono QUESTIONARIO
    @JsonManagedReference
    List<Domanda> domandeBot;

    @OneToMany(mappedBy = "chatbot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "categoria = 'QUESTIONARIO'")
    @JsonManagedReference
    List<Domanda> domandeQuestionario;

}