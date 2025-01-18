package it.unisa.biblionet.model.dao.chatbot;

import it.unisa.biblionet.model.entity.chatbot.Risposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RispostaDAO extends JpaRepository<Risposta, Integer> {

    @Query("Select r from Risposta r")
    List<Risposta> findAllRisposta();
}
