package it.unisa.biblionet.model.entity.compositeKey;

import it.unisa.biblionet.model.entity.Possesso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import java.io.Serializable;


/**
 * Questa classe rappresenta la chiave composta di Possesso.
 * Sono presenti due campi:
 * <ul>
 *     <li> <strong>bibliotecaID</strong> è la mail della
 *              biblioteca dove il libro è conservato</li>
 *     <li> <strong>libroID</strong> è l'ID del libro</li>
 * </ul>
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PossessoId implements Serializable {

    /**
     * Rappresenta la chiave primaria di una biblioteca.
     */
    @Basic
    private String bibliotecaID;

    /**
     * Rappresenta la chiave primaria di un libro.
     */
    @Basic
    private int libroID;



}
