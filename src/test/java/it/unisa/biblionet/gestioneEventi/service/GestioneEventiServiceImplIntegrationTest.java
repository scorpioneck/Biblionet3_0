package it.unisa.biblionet.gestioneEventi.service;

import it.unisa.biblionet.BiblionetApplication;
import it.unisa.biblionet.model.entity.Evento;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Implementa l'integration testing del service per il sottosistema
 * Gestione Eventi.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BiblionetApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GestioneEventiServiceImplIntegrationTest {

    @Autowired
    @Setter
    @Getter
    private ApplicationContext applicationContext;

    @Autowired
    private GestioneEventiService gestioneEventiService;

    @BeforeEach
    public void init() {
        BiblionetApplication.init(applicationContext);
    }

    @Test
    public void modificaEventoIntegrationTest(){
        Optional<Evento> evento = gestioneEventiService.getEventoById(2);
        assertEquals(evento,gestioneEventiService.modificaEvento(evento.get()));
    }

    @Test
    public void modificaEventoFaultIntegrationTest(){
        Evento evento = new Evento();
            evento.setIdEvento(-1);
            evento.setNomeEvento("Prova");
            evento.setDescrizione("EventoDiProvaPerTesting");
            evento.setDataOra(LocalDateTime.of(2026, 10, 1, 10, 30));
            assertEquals(Optional.empty(),gestioneEventiService.modificaEvento(evento));
    }



}
