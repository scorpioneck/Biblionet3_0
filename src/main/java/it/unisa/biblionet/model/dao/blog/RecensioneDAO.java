package it.unisa.biblionet.model.dao.blog;

import it.unisa.biblionet.model.entity.blog.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface RecensioneDAO extends JpaRepository<Recensione,Integer> {

    @Query("Select re from Recensione re")
    List<Recensione> findAllRecensioni();

}
