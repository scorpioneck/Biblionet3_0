package it.unisa.biblionet.model.dao;

import it.unisa.biblionet.model.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Questa classe rappresenta il DAO di un Evento.
 */
@Repository
public interface EventoDAO extends JpaRepository<Evento, Integer> {
}
