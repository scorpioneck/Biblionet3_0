package it.unisa.biblionet.preferenzeDiLettura.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Implementa l'integration testing del service per il sottosistema
 * Preferenze Di Lettura.
 */

/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = BiblionetApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PreferenzeDiLetturaServiceImplIntegrationTest {

    @Autowired
    @Setter
    @Getter
    private ApplicationContext applicationContext;

    @Autowired
    private PreferenzeDiLetturaService preferenzeDiLetturaService;

    @Autowired
    private GenereDAO genereDAO;

    @BeforeEach
    public void init() {
        BiblionetApplication.init(applicationContext);
    }

    /**
     * Metodo che si occupa di testare
     * la funzione di ricerca di un
     * genere facendo una iterazione nel service.
     */

    /*@Test
    public void getGeneriByName1IT() {
        List<Genere> generiDB = new ArrayList<>();
        generiDB.add(genereDAO.findByName("Fantasy"));
        String[] generi ={"Fantasy"};
        assertEquals(generiDB, preferenzeDiLetturaService.getGeneriByName(generi));
    }*/

//}
