package it.unisa.biblionet.blog.service;

import it.unisa.biblionet.BiblionetApplication;
import it.unisa.biblionet.model.dao.blog.CommentoDAO;
import it.unisa.biblionet.model.dao.blog.CommentoRispostaDAO;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BiblionetApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlogServiceIntegrationImpTest {
    @Autowired
    @Setter
    @Getter
    private ApplicationContext applicationContext;


    @Autowired
    private CommentoDAO commentoDAO;

    @Autowired
    private CommentoRispostaDAO commentoRispostaDAO;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        BiblionetApplication.init(applicationContext);
    }

    @Test
    public void visualizzaCommenti() {
        List<Commento> commenti = commentoDAO.findAllCommenti();

        // Assert: Verifichiamo che i commenti siano correttamente recuperati
        assertNotNull(commenti);
        assertTrue(commenti.size() > 1);


    }

    @Test
    public void findAllCommentoRisposta() {
        List<CommentoRisposta> risposte = commentoRispostaDAO.findAllRisposta();

        // Assert: Verifichiamo che i commenti siano correttamente recuperati
        assertNotNull(risposte);
        assertTrue(risposte.size() > 1);

    }

}





