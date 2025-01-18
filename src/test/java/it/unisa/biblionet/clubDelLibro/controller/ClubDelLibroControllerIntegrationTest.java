package it.unisa.biblionet.clubDelLibro.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import it.unisa.biblionet.BiblionetApplication;
import it.unisa.biblionet.model.dao.ClubDelLibroDAO;
import it.unisa.biblionet.model.dao.utente.BibliotecaDAO;
import it.unisa.biblionet.model.dao.utente.LettoreDAO;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Lettore;
import lombok.Getter;
import lombok.Setter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;


/**
 * Implementa l'integration testing del controller per il sottosistema
 * ClubDelLibro.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BiblionetApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ClubDelLibroControllerIntegrationTest {
    @Autowired
    @Setter
    @Getter
    private ApplicationContext applicationContext;

    @Autowired
    private ClubDelLibroDAO clubDAO;

    @Autowired
    private LettoreDAO lettoreDAO;

    @Autowired
    private BibliotecaDAO bibliotecaDAO;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        BiblionetApplication.init(applicationContext);
    }

    @Test
    public void visualizzaListaEventiClubInesistenteTest() throws Exception {
        var club = clubDAO.getOne(-1);
        var lettore = (Lettore) this.lettoreDAO.findAll()
                                               .stream()
                                               .filter(
                                                 x -> x.getTipo() == "Lettore"
                                               ).findFirst().get();
        var id = club.getIdClub();

        this.mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/club-del-libro/" + id + "/eventi"
            ).sessionAttr("loggedUser", lettore)
        ).andExpect(
            status().isNotFound()
        );
    }

    @Test
    public void visualizzaListaEventiComeBibliotecaTest() throws Exception {
        var club = clubDAO.findAll().get(0);
        var biblioteca = (Biblioteca) this.bibliotecaDAO.findAll()
                                               .stream()
                                               .filter(
                                                 x -> x.getTipo() == "Biblioteca"
                                               ).findFirst().get();
        var id = club.getIdClub();

        this.mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/club-del-libro/" + id + "/eventi"
            ).sessionAttr("loggedUser", biblioteca)
        ).andExpect(
            status().isUnauthorized()
        );
    }


    @Test
    public void visualizzaListaEventiClubTest() throws Exception {
        var club = clubDAO.findAll().get(0);
        var lettore = (Lettore) this.lettoreDAO.findAll()
                                               .stream()
                                               .filter(
                                                 x -> x.getTipo() == "Lettore"
                                               ).findFirst().get();
        var id = club.getIdClub();

        this.mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/club-del-libro/" + id + "/eventi"
            ).sessionAttr("loggedUser", lettore)
        ).andExpect(
            model().attribute(
                "loggedUser",
                lettore
            )
        ).andExpect(
            model().attribute(
                "club",
                Matchers.hasProperty(
                    "idClub",
                    Matchers.equalTo(club.getIdClub())
                )
            )
        ).andExpect(
            model().attributeExists(
                "eventi",
                "mieiEventi"
            )
        );
    }

}
