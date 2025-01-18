package it.unisa.biblionet.model.dao;

import it.unisa.biblionet.model.entity.Genere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Questa classe rappresenta il DAO di un Genere.
 */
@Repository
public interface GenereDAO extends JpaRepository<Genere, String> {

    /**
     * Query custom made per mappare nome
     * del genere ad un oggetto Genere.
     * @param genere Il nome come stringa
     * @return Il genere come oggetto
     */

    @Query("SELECT g FROM Genere g WHERE UPPER(g.nome) = UPPER(?1)")
    Genere findByName(String genere);
}
