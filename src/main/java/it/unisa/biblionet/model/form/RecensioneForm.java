package it.unisa.biblionet.model.form;

import it.unisa.biblionet.model.entity.Libro;
import jakarta.persistence.Column;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class RecensioneForm {

    @Column(nullable = false)
    @NonNull
    private String titolo;

    @Column(nullable = false)
    @NonNull
    private String descrizione;

    @Column(nullable = false)
    @NonNull
    private Libro libro;

}
