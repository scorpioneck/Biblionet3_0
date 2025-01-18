package it.unisa.biblionet.model.dao.chatbot;


import it.unisa.biblionet.model.entity.chatbot.DomandaRisposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomandaRispostaDAO extends JpaRepository<DomandaRisposta, Integer> {

    @Query("Select re from Recensione re")
    List<DomandaRisposta> findAllDomandaRisposta();

}
