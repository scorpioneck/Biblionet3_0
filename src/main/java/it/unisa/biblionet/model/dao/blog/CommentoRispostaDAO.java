package it.unisa.biblionet.model.dao.blog;

import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentoRispostaDAO extends JpaRepository<CommentoRisposta, Integer> {

    @Query("Select com from CommentoRisposta com")
    List<CommentoRisposta> findAllRisposta();
}
