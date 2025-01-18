package it.unisa.biblionet.model.dao.chatbot;


import it.unisa.biblionet.model.entity.chatbot.Domanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomandaDAO extends JpaRepository<Domanda, Integer> {

    @Query("Select d from Domanda d")
    List<Domanda> findAllDomanda();

}
