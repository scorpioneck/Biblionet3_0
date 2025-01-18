package it.unisa.biblionet.model.dao.blog;

import it.unisa.biblionet.model.entity.blog.Risposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RispostaDAO extends JpaRepository<Risposta, Integer> {

    @Query("Select com from Risposta com")
    List<Risposta> findAllRisposta();
}
