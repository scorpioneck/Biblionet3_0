package it.unisa.biblionet.model.dao.blog;

import it.unisa.biblionet.model.entity.blog.Commento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentoDAO extends JpaRepository<Commento,Integer>{

    @Query("Select c from Commento c")
    List<Commento> findAllCommenti();

    @Query("DELETE FROM Commento c WHERE c.id = :commentoId")
    void deleteByCommentoId(int commentoId);



}
