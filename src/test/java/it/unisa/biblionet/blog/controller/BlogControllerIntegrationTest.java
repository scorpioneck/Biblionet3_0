package it.unisa.biblionet.blog.controller;

import it.unisa.biblionet.BiblionetApplication;
import it.unisa.biblionet.model.dao.blog.RecensioneDAO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test di integrazione per il controller BlogController.
 * @throws Exception Eccezione per MovkMvc
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BiblionetApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BlogControllerIntegrationTest {

    @Autowired
    @Setter
    @Getter
    private ApplicationContext applicationContext;

    @Autowired
    private RecensioneDAO recensioneDAO;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        BiblionetApplication.init(applicationContext);
    }

    @Test
    public void visualizzaRecensioni() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/blog"))
                .andExpect(status().isOk()) // Verifica che la risposta sia 200 OK
                .andExpect(model().attributeExists("recensioni")) // Verifica l'attributo del modello
                .andExpect(view().name("blog/visualizza-recensioni")) // Verifica la vista restituita
                .andExpect(model().attribute("recensioni", hasSize(3))); // Verifica la dimensione della lista
    }
}