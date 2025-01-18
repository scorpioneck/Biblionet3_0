package it.unisa.biblionet.model.form;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CommentoForm {

    @Column(nullable = false)
    @NonNull
    private String title;

    @Column(nullable = false)
    @NonNull
    private String description;


    private int commentoPadreId;

    private String idUtente;

}
