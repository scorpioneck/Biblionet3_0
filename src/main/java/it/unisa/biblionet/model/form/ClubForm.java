package it.unisa.biblionet.model.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Classe che rappresenta il form per la creazione di un club del libro.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ClubForm {

    /**
     * Nome del club.
     */
    @NonNull
    private String nome;

    /**
     * Descrizione del club.
     */
    @NonNull
    private String descrizione;

    /**
     * Lista di generi del club.
     */
    @NonNull
    private List<String> generi;

    /**
     * Copertina del club.
     */
    private MultipartFile copertina;
}
