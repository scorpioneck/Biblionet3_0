package it.unisa.biblionet.model.dao;

import it.unisa.biblionet.model.dao.customQueriesResults.ILibroIdAndName;
import it.unisa.biblionet.model.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Questa classe rappresenta il DAO di un Libro.
 */
@Repository
public interface LibroDAO extends JpaRepository<Libro, Integer> {

    /**
     * Query custom per il recupero dal DB di una lista
     * di libri che contengono la stringa passata
     * come parametro.
     * @param titolo La stringa che deve essere
     *               contenuta
     * @return La lista dei libri che contengono
     *          la stringa
     */
    @Query("SELECT l FROM Libro l "
            + "WHERE UPPER(l.titolo) LIKE UPPER(concat('%', ?1,'%'))")
    List<Libro> findByTitoloLike(String titolo);

    /**
     * Query custom per il recupero dal DB di una lista
     * di libri che contengono la stringa passata
     * come parametro.
     * @param titolo La stringa che deve essere
     *               contenuta
     * @return La lista dei libri che contengono
     *          la stringa
     */
    @Query("SELECT l.idLibro AS idLibro, l.titolo AS titolo "
            + "FROM Libro l "
            + "WHERE UPPER(l.titolo) LIKE UPPER(concat('%', ?1,'%'))")
    List<ILibroIdAndName> findByTitoloContains(String titolo);


    @Query("SELECT l FROM Libro l")
    List<Libro> findAllLibro();
}
