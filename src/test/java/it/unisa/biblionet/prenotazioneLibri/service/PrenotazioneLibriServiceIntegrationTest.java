package it.unisa.biblionet.prenotazioneLibri.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Implementa l'integration testing del service per il sottosistema
 * Prenotazione Libri.
 */
/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = BiblionetApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PrenotazioneLibriServiceIntegrationTest {

    @Autowired
    @Setter
    @Getter
    private ApplicationContext applicationContext;

    @Autowired
    private PrenotazioneLibriService prenotazioneLibriService;

    @Autowired
    private BibliotecaDAO bibliotecaDAO;

    @BeforeEach
    public void init() {
        BiblionetApplication.init(applicationContext);
    }

    @Test
    public void getBiblioteca(){
        Optional<UtenteRegistrato> optionalBiblioteca = bibliotecaDAO.findById("deliguori@gmail.com");
        assertTrue(optionalBiblioteca.isPresent(), "Biblioteca non trovata nel database");

        Biblioteca biblioteca = (Biblioteca) optionalBiblioteca.get();
        assertEquals(biblioteca.getNomeBiblioteca(),prenotazioneLibriService.getBibliotecaById("deliguori@gmail.com").getNomeBiblioteca());
    }


}*/
