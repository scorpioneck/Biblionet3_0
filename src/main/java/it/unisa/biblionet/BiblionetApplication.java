package it.unisa.biblionet;

import it.unisa.biblionet.model.dao.*;
import it.unisa.biblionet.model.dao.chatbot.DomandaDAO;
import it.unisa.biblionet.model.dao.chatbot.RispostaDAO;
import it.unisa.biblionet.model.entity.*;
import it.unisa.biblionet.model.entity.chatbot.Categoria;
import it.unisa.biblionet.model.entity.chatbot.ChatBot;
import it.unisa.biblionet.model.entity.chatbot.Risposta;
import it.unisa.biblionet.model.dao.blog.CommentoDAO;
import it.unisa.biblionet.model.dao.blog.CommentoRispostaDAO;
import it.unisa.biblionet.model.dao.blog.RecensioneDAO;
import it.unisa.biblionet.model.dao.chatbot.ChatBotDAO;
import it.unisa.biblionet.model.dao.utente.BibliotecaDAO;
import it.unisa.biblionet.model.dao.utente.EspertoDAO;
import it.unisa.biblionet.model.dao.utente.LettoreDAO;
import it.unisa.biblionet.model.entity.blog.Commento;
import it.unisa.biblionet.model.entity.blog.CommentoRisposta;
import it.unisa.biblionet.model.entity.blog.Recensione;
import it.unisa.biblionet.model.entity.chatbot.Domanda;
import it.unisa.biblionet.model.entity.compositeKey.PossessoId;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.entity.utente.Lettore;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;
import it.unisa.biblionet.prenotazioneLibri.service.bookApiAdapter.BookApiAdapter;
import it.unisa.biblionet.prenotazioneLibri.service.bookApiAdapter.GoogleBookApiAdapterImpl;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

/**
 * Questa è la main class del progetto, che fa partire l'applicazione e popola
 * il database.
 */
@SpringBootApplication
public class BiblionetApplication {

    private static BookApiAdapter bookApiAdapter = new GoogleBookApiAdapterImpl();

    private static LibroDAO libroDAO = null;

    public static Libro getLibroFromAPI(String isbn, Genere... generi) {
        Libro libro = bookApiAdapter.getLibroDaBookApi(isbn);
        if (libro == null) {
            libro = new Libro("Not Found", "Autore", isbn,
                    LocalDateTime.now(), "Descrizione", "Casa Editrice");
        }
        libro.setGeneri(Arrays.asList(generi));

        return libroDAO.save(libro);
    }

    public static void init(ApplicationContext configurableApplicationContext) {

        UtenteRegistrato utenteRegistrato = new UtenteRegistrato();
//----------------------------Definizione oggetti DAO per ogni entity---------------------------------------------------


        BibliotecaDAO bibliotecaDAO = configurableApplicationContext.getBean(BibliotecaDAO.class);
        EspertoDAO espertoDAO = configurableApplicationContext.getBean(EspertoDAO.class);
        LettoreDAO lettoreDAO = configurableApplicationContext.getBean(LettoreDAO.class);
        ClubDelLibroDAO clubDelLibroDAO = configurableApplicationContext.getBean(ClubDelLibroDAO.class);
        EventoDAO eventoDAO = configurableApplicationContext.getBean(EventoDAO.class);
        GenereDAO genereDAO = configurableApplicationContext.getBean(GenereDAO.class);
        libroDAO = configurableApplicationContext.getBean(LibroDAO.class);
        PossessoDAO possessoDAO = configurableApplicationContext.getBean(PossessoDAO.class);
        TicketPrestitoDAO ticketPrestitoDAO = configurableApplicationContext.getBean(TicketPrestitoDAO.class);
        RecensioneDAO recensioneDAO = configurableApplicationContext.getBean(RecensioneDAO.class);
        CommentoDAO commentoDAO = configurableApplicationContext.getBean(CommentoDAO.class);
        CommentoRispostaDAO commentoRispostaDAO = configurableApplicationContext.getBean(CommentoRispostaDAO.class);
        ChatBotDAO chatBotDAO = configurableApplicationContext.getBean(ChatBotDAO.class);
        RispostaDAO rispostaDAO= configurableApplicationContext.getBean(RispostaDAO.class);
        DomandaDAO domandaDAO = configurableApplicationContext.getBean(DomandaDAO.class);

        Logger out = Logger.getLogger(BiblionetApplication.class);

//------------------------------Definizione oggetti per popolamento Database--------------------------------------------


//-------------------------------Definizione ed inserimento Biblioteche-------------------------------------------------


        out.info("***************************INIZIALIZZAZIONE DI BIBLIONET IN CORSO...***************************");
        Biblioteca biblioteca = new Biblioteca(
                "bibliotecacarrisi@gmail.com",
                "BibliotecaPassword",
                "Napoli",
                "Torre del Greco",
                "Via Carrisi 47",
                "1234567890",
                "Biblioteca Carrisi"
        );

        Biblioteca biblioteca1 = new Biblioteca(
                "bunnapoli@libero.it",
                "BibliotecaPassword",
                "Napoli",
                "Napoli",
                "Via Giovanni Paladino",
                "0815517025",
                "BUN - Biblioteca Universitaria di Napoli"
        );

        Biblioteca biblioteca2 = new Biblioteca(
                "villacarrara@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Salerno",
                "Via Posidonia 47",
                "3341278415",
                "Biblioteca Villa Carrara"
        );

        Biblioteca biblioteca3 = new Biblioteca(
                "centrodilettura@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Fisciano",
                "Via Roma 1",
                "3341276715",
                "Biblioteca del Centro di Lettura"
        );

        Biblioteca biblioteca4 = new Biblioteca(
                "unisapoloscientifico@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Fisciano",
                "Università di Salerno",
                "0815168272",
                "Biblioteca del Polo scientifico e tecnologico"
        );

        Biblioteca biblioteca5 = new Biblioteca(
                "comunenocera@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Nocera Inferiore",
                "Corso Vittorio Emanuele II 52",
                "0813235611",
                "Biblioteca Comunale di Nocera Inferiore"
        );

        Biblioteca biblioteca6 = new Biblioteca(
                "aldomoronocera@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Nocera Inferiore",
                "Piazza Aldo Moro",
                "0814535611",
                "Biblioteca Aldo Moro"
        );

        Biblioteca biblioteca7 = new Biblioteca(
                "deliguori@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Pagani",
                "Piazza Sant'Alfonso",
                "0815436114",
                "Biblioteca S. Alfonso dè Liguori"
        );

        Biblioteca biblioteca8 = new Biblioteca(
                "lpepepompei@gmail.com",
                "BibliotecaPassword",
                "Napoli",
                "Pompei",
                "Via Aldo Moro II",
                "081324357",
                "Biblioteca L. Pepe"
        );

        Biblioteca biblioteca9 = new Biblioteca(
                "fmorlicchio@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Scafati",
                "Via Galileo Galilei 34",
                "0813223238",
                "Biblioteca Francesco Morlicchio"
        );

        bibliotecaDAO.save(biblioteca);
        bibliotecaDAO.save(biblioteca1);
        bibliotecaDAO.save(biblioteca2);
        bibliotecaDAO.save(biblioteca3);
        bibliotecaDAO.save(biblioteca4);
        bibliotecaDAO.save(biblioteca5);
        bibliotecaDAO.save(biblioteca6);
        bibliotecaDAO.save(biblioteca7);
        bibliotecaDAO.save(biblioteca8);
        bibliotecaDAO.save(biblioteca9);

        out.info("*************************** Biblioteche create 1/9 ***************************");



//----------------------Definizione ed inserimento esperti--------------------------------------------------------------


        Esperto esperto = new Esperto(
                "eliagianpaolo@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Torre del Greco",
                "Via Roma 2",
                "2345678901",
                "Espertissimo",
                "Elia",
                "Gianpaolo",
                biblioteca
        );

        Esperto esperto1 = new Esperto(
                "ciromarano@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Via Roma 2",
                "2345678911",
                "ciromarano",
                "Ciro",
                "Marano",
                biblioteca1
        );

        Esperto esperto2 = new Esperto(
                "giulioMedici@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Torre del Greco",
                "Via CasaDiGiulio 2",
                "3127403398",
                "giuliomedici",
                "Giulio",
                "Medici",
                biblioteca2
        );

        Esperto esperto3 = new Esperto(
                "lucatopo@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Ischia",
                "Via Roma 2",
                "3912479852",
                "lucatopo",
                "Luca",
                "Topo",
                biblioteca3
        );

        Esperto esperto4 = new Esperto(
                "vivianaperugia@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Piazza S.Alfonso 2",
                "3789652345",
                "vivianaperugia",
                "Viviana",
                "Perugia",
                biblioteca4
        );

        Esperto esperto5 = new Esperto(
                "gianmariolivorno@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Agropoli",
                "Via Bonora 41",
                "3961294784",
                "gianmariolivorno",
                "Gianmario",
                "Livorno",
                biblioteca5
        );

        Esperto esperto6 = new Esperto(
                "stefanodepadova@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Salerno",
                "Via Torrione",
                "2345608901",
                "stefanodePadova",
                "Stefano",
                "De Padova",
                biblioteca6
        );

        Esperto esperto7 = new Esperto(
                "alessiopaoloo@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Somma Vesuviana",
                "Via Vesuvio 69",
                "3336977441",
                "alessiopaolo",
                "Alessio",
                "Paolo",
                biblioteca7
        );

        Esperto esperto8 = new Esperto(
                "antoniodellaporta@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Nocera Inferiore",
                "Via Nocerina 45",
                "3963636963",
                "antoniodellaporta",
                "Antonio",
                "Della Porta",
                biblioteca8
        );

        Esperto esperto9 = new Esperto(
                "nicolapagliara@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Salerno",
                "Via Torrione",
                "3694578963",
                "nicolapagliara",
                "Nicola",
                "Pagliara",
                biblioteca9
        );

        Esperto esperto10 = new Esperto(
                "paolobonolis@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Paestum",
                "Via Avanti un Altro",
                "3694578963",
                "paolobonolis",
                "Paolo",
                "Bonolis",
                biblioteca3
        );

        Esperto esperto11 = new Esperto(
                "lucalaurenti@gmail.com",
                "EspertoPassword",
                "Caserta",
                "Marcianise",
                "Via della Reggia",
                "3694578963",
                "lucalaurenti",
                "Luca",
                "Laurenti",
                biblioteca1
        );

        Esperto esperto12 = new Esperto(
                "alfonsosignorini@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Castellammare di Stabia",
                "Via delle Sabbie 45",
                "3694578963",
                "alfonsosignorini",
                "Alfonso",
                "Signorini",
                biblioteca2
        );

        Esperto esperto13 = new Esperto(
                "antoninocannavacciuolo@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Napoli",
                "Via Villa Crespi",
                "3694578963",
                "antoninocannavacciuolo",
                "Antonino",
                "Cannavacciuolo",
                biblioteca3
        );

        Esperto esperto14 = new Esperto(
                "brunobarbieri@gmail.com",
                "EspertoPassword",
                "Avellino",
                "Avellino",
                "Via Tortellino 54",
                "3694578963",
                "brunobarbieri",
                "Bruno",
                "Barbieri",
                biblioteca4
        );

        Esperto esperto15 = new Esperto(
                "carlocracco@gmail.com",
                "EspertoPassword",
                "Avellino",
                "Ariano Irpino",
                "Via Nuova Pizza 4",
                "3694578963",
                "carlocracco",
                "Carlo",
                "Cracco",
                biblioteca5
        );

        Esperto esperto16 = new Esperto(
                "joebastianich@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Pompei",
                "Via Mc Donald 1",
                "3694578963",
                "joebastianich",
                "Joe",
                "Bastianich",
                biblioteca6
        );

        Esperto esperto17 = new Esperto(
                "iginiomassari@gmail.com",
                "EspertoPassword",
                "Napoli",
                "Torre Annunziata",
                "Via Pasticcino 26",
                "3694578963",
                "iginiomassari",
                "Iginio",
                "Massari",
                biblioteca7
        );

        Esperto esperto18 = new Esperto(
                "gaiaclerici@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Nocera Superiore",
                "Via Del Cuoco 78",
                "3294578961",
                "gaiaclerici",
                "Gaia",
                "Clerici",
                biblioteca8
        );

        Esperto esperto19 = new Esperto(
                "alessandroborghese@gmail.com",
                "EspertoPassword",
                "Salerno",
                "San Marzano sul Sarno",
                "Via Ristoranti 4",
                "3694578963",
                "alessandroborghese",
                "Alessandro",
                "Borghese",
                biblioteca9
        );

        Esperto esperto20 = new Esperto(
                "gennarodecarmini@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Vietri sul Mare",
                "Via Buon Appetito 5",
                "3694578063",
                "gennaroesposito",
                "Gennaro",
                "De Carmini",
                biblioteca2
        );


        espertoDAO.save(esperto);
        espertoDAO.save(esperto1);
        espertoDAO.save(esperto2);
        espertoDAO.save(esperto3);
        espertoDAO.save(esperto4);
        espertoDAO.save(esperto5);
        espertoDAO.save(esperto6);
        espertoDAO.save(esperto7);
        espertoDAO.save(esperto8);
        espertoDAO.save(esperto9);
        espertoDAO.save(esperto10);
        espertoDAO.save(esperto11);
        espertoDAO.save(esperto12);
        espertoDAO.save(esperto13);
        espertoDAO.save(esperto14);
        espertoDAO.save(esperto15);
        espertoDAO.save(esperto16);
        espertoDAO.save(esperto17);
        espertoDAO.save(esperto18);
        espertoDAO.save(esperto19);
        espertoDAO.save(esperto20);

        out.info("*************************** Esperti creati 2/9 ***************************");


//----------------------Definizione ed inserimento lettori--------------------------------------------------------------

        Lettore lettore = new Lettore(
                "antoniorenatomontefusco@gmail.com",
                "LettorePassword",
                "Napoli",
                "Somma Vesuviana",
                "Via Vesuvio 33",
                "3456789012",
                "antoniomontefusco",
                "Antonio",
                "Montefusco"
        );

        Lettore lettore1 = new Lettore(
                "jacopoviceconti@gmail.com",
                "LettorePassword",
                "Salerno",
                "Pagani",
                "Via San Domenico 10",
                "3451349012",
                "jacopoviceconti",
                "Jacopo",
                "Viceconti"
        );

        Lettore lettore2 = new Lettore(
                "pamelaclerici@gmail.com",
                "LettorePassword",
                "Milano",
                "Milano",
                "Viale dei Navigli",
                "3451343794",
                "pamelaclerici",
                "Pamela",
                "Clerici"
        );

        Lettore lettore3 = new Lettore(
                "angelomirko@gmail.com",
                "LettorePassword",
                "Napoli",
                "Saomma Vesuviana",
                "Via Dalluni 22",
                "3459036412",
                "angelomirko",
                "Angelo",
                "Mirko"
        );

        Lettore lettore4 = new Lettore(
                "giuseppetombola@gmail.com",
                "LettorePassword",
                "Napoli",
                "Saomma Vesuviana",
                "Via Rochet Lig 55",
                "3459013082",
                "giuseppetombola",
                "Giuseppe",
                "Tombola"
        );

        Lettore lettore5 = new Lettore(
                "paoloapostolico@gmail.com",
                "LettorePassword",
                "Salerno",
                "Castel San Giorgio",
                "Via Bertini 42",
                "3459013412",
                "paoloapostolico",
                "Paolo",
                "Apostolico"
        );

        Lettore lettore6 = new Lettore(
                "marcocostante@gmail.com",
                "LettorePassword",
                "Avellino",
                "Ariano Irpino",
                "Via Costante K",
                "3459013412",
                "marcocostante",
                "Marco",
                "Costante"
        );

        Lettore lettore7 = new Lettore(
                "mafaldaingenito@gmail.com",
                "LettorePassword",
                "Napoli",
                "Gragnano",
                "Via Franco 6",
                "3459013412",
                "mafaldaingenito",
                "Mafalda",
                "Ingenito"
        );

        Lettore lettore8 = new Lettore(
                "carminegallina@gmail.com",
                "LettorePassword",
                "Caserta",
                "Capua",
                "Via Dalla Spagna 36",
                "3459013412",
                "carminegallina",
                "Carmine",
                "Gallina"
        );

        Lettore lettore9 = new Lettore(
                "vincenzodisanto@gmail.com",
                "LettorePassword",
                "Caserta",
                "San Nicola La Strada",
                "Via Dall'Appello 13",
                "3459013412",
                "vincenzodisanto",
                "Vincenzo",
                "Di Santo"
        );

        Lettore lettore10 = new Lettore(
                "mauriziofabrocile@gmail.com",
                "LettorePassword",
                "Caserta",
                "Caserta",
                "Via Dragoni 23",
                "3459013412",
                "mauriziofabrocile",
                "Maurizio Fabrizio",
                "Fabrocile"
        );

        Lettore lettore11 = new Lettore(
                "giuseppeferrari@gmail.com",
                "LettorePassword",
                "Salerno",
                "Salerno",
                "Via Per Sempre 1",
                "3459133412",
                "giuseppeferrari",
                "Giuseppe",
                "Ferrari"
        );

        Lettore lettore12 = new Lettore(
                "prova29@gmail.com",
                "LettorePassword",
                "Salerno",
                "Pagani",
                "Via Ferrante 2",
                "3459013412",
                "carminedileo",
                "Carmine",
                "Di Leo"
        );

        lettoreDAO.save(lettore);
        lettoreDAO.save(lettore1);
        lettoreDAO.save(lettore2);
        lettoreDAO.save(lettore3);
        lettoreDAO.save(lettore4);
        lettoreDAO.save(lettore5);
        lettoreDAO.save(lettore6);
        lettoreDAO.save(lettore7);
        lettoreDAO.save(lettore8);
        lettoreDAO.save(lettore9);
        lettoreDAO.save(lettore10);
        lettoreDAO.save(lettore11);
        lettoreDAO.save(lettore12);

        out.info("*************************** Esperti creati 3/9 ***************************");

//----------------------Definizione ed inserimento generi---------------------------------------------------------------

        Genere fantasy = new Genere(
                "Fantasy",
                "Genere fantastico"
        );

        Genere azione = new Genere(
                "Azione",
                "Genere molto movimentato"
        );

        Genere space = new Genere(
                "Space",
                "Genere spaziale"
        );

        Genere biografico = new Genere(
                "Biografico",
                "Genere introspettivo"
        );

        Genere politico = new Genere(
                "Politico",
                "Genere ingannevole"
        );

        Genere narrativa = new Genere(
                "Narrativa",
                "Genere narrativo"
        );

        Genere romanzo = new Genere(
                "Romanzo",
                "Genere che si descrive da solo"
        );

        Genere storico = new Genere(
                "Storico",
                "Genere sulla storia"
        );

        Genere fantascienza = new Genere(
                "Fantascienza",
                "Genere genere fantastico ma scientifico"
        );

        Genere tecnologia = new Genere(
                "Tecnologia",
                "Genere moderno"
        );

        Genere noir = new Genere(
                "Noir",
                "Genere scuro"
        );

        Genere distopia = new Genere(
                "Distopia",
                "Genere ingannevole"
        );

        Genere romantico = new Genere(
                "Romantico",
                "Genere sdolcinato"
        );

        Genere avventura = new Genere(
                "D'Avventura",
                "Genere intraprendente"
        );

        Genere formazione = new Genere(
                "Di formazione",
                "Genere formativo"
        );

        Genere ragazzi = new Genere(
                "Per ragazzi",
                "Genere giovanile"
        );

        Genere horror = new Genere(
                "Horror",
                "Genere spaventosissimo"
        );

        Genere thriller = new Genere(
                "Thriller",
                "Genere ansioso"
        );

        Genere gotico = new Genere(
                "Gotico",
                "Genere dal gusto gotico"
        );

        Genere giallo = new Genere(
                "Giallo",
                "Genere investigativo"
        );

        Genere scientifico = new Genere(
                "Scientifico",
                "Genere scientifico"
        );

        Genere psicologico = new Genere(
                "Psicologico",
                "Genere psicologico"
        );

        Genere saggio = new Genere(
                "Saggio",
                "Genere sapiente"
        );

        Genere comico = new Genere(
                "Comico",
                "Genere divertente"
        );

        Genere fiabefavole = new Genere(
                "Fiabe e favole",
                "Genere fiabesco"
        );

        genereDAO.save(avventura);
        genereDAO.save(azione);
        genereDAO.save(giallo);
        genereDAO.save(gotico);
        genereDAO.save(biografico);
        genereDAO.save(comico);
        genereDAO.save(distopia);
        genereDAO.save(fantasy);
        genereDAO.save(fantascienza);
        genereDAO.save(fiabefavole);
        genereDAO.save(formazione);
        genereDAO.save(horror);
        genereDAO.save(narrativa);
        genereDAO.save(noir);
        genereDAO.save(politico);
        genereDAO.save(psicologico);
        genereDAO.save(ragazzi);
        genereDAO.save(romantico);
        genereDAO.save(romanzo);
        genereDAO.save(saggio);
        genereDAO.save(scientifico);
        genereDAO.save(space);
        genereDAO.save(storico);
        genereDAO.save(tecnologia);
        genereDAO.save(thriller);

        out.info("*************************** Generi creati 4/9 ***************************");

//----------------------Definizione ed inserimento libri----------------------------------------------------------------

        out.info("*************************** Caricamento libro 1/41 ***************************");
        Libro libro = getLibroFromAPI(
                "9781781101582",
                fantasy,
                ragazzi,
                romanzo
        );

        out.info("*************************** Caricamento libro 2/41 ***************************");
        Libro libro1 = getLibroFromAPI(
                "9781781102121",
                fantasy,
                ragazzi,
                romanzo
        );

        out.info("*************************** Caricamento libro 3/41 ***************************");
        Libro libro2 = getLibroFromAPI(
                "9788804616351",
                fantasy,
                avventura,
                azione
        );

        out.info("*************************** Caricamento libro 4/41 ***************************");
        Libro libro3 = getLibroFromAPI(
                "9780141439570",
                narrativa,
                gotico
        );

        out.info("*************************** Caricamento libro 5/41 ***************************");
        Libro libro4 = getLibroFromAPI(
                "9780141034591",
                saggio
        );

        out.info("*************************** Caricamento libro 6/41 ***************************");
        Libro libro5 = getLibroFromAPI(
                "9788838473463",
                horror,
                thriller
        );

        out.info("*************************** Caricamento libro 7/41 ***************************");
        Libro libro6 = getLibroFromAPI(
                "9780198321668",
                romantico
        );

        out.info("*************************** Caricamento libro 8/41 ***************************");
        Libro libro7 = getLibroFromAPI(
                "9788845295300",
                ragazzi,
                fiabefavole
        );

        out.info("*************************** Caricamento libro 9/41 ***************************");
        Libro libro8 = getLibroFromAPI(
                "9788852049774",
                giallo, 
                romanzo,
                thriller
        );

        out.info("*************************** Caricamento libro 10/41 ***************************");
        Libro libro9 = getLibroFromAPI(
                "9788854122789",
                narrativa
        );

        out.info("*************************** Caricamento libro 11/41 ***************************");
        Libro libro10 = getLibroFromAPI(
                "9788852028489",
                narrativa,
                storico
        );

        out.info("*************************** Caricamento libro 12/41 ***************************");
        Libro libro11 = getLibroFromAPI(
                "9788860816412",
                psicologico
        );

        out.info("*************************** Caricamento libro 13/41 ***************************");
        Libro libro12 = getLibroFromAPI(
                "9788854133587",
                romanzo,
                giallo,
                storico
        );

        out.info("*************************** Caricamento libro 14/41 ***************************");
        Libro libro13 = getLibroFromAPI(
                "9781633397354",
               tecnologia,
               scientifico
        );
            
        
        out.info("*************************** Caricamento libro 15/41 ***************************");
        Libro libro14 = getLibroFromAPI(
                "9788841869741",
               narrativa,
               ragazzi
        );
            
        
        out.info("*************************** Caricamento libro 16/41 ***************************");
        Libro libro15 = getLibroFromAPI(
                "9788864113036",
               fantasy,
               romantico
        );
            
        
        out.info("*************************** Caricamento libro 17/41 ***************************");
        Libro libro16 = getLibroFromAPI(
                "9788852021558",
               distopia,
               fantascienza
        );
            
        
        out.info("*************************** Caricamento libro 18/41 ***************************");
        Libro libro17 = getLibroFromAPI(
                "9788851161439",
               ragazzi,
               fiabefavole
        );
            
        
        out.info("*************************** Caricamento libro 19/41 ***************************");
        Libro libro18 = getLibroFromAPI(
                "9788866327769",
               giallo,
               thriller
        );
            
        
        out.info("*************************** Caricamento libro 20/41 ***************************");
        Libro libro19 = getLibroFromAPI(
                "9788830455320",
               giallo,
               noir
        );
            
        
        out.info("*************************** Caricamento libro 21/41 ***************************");
        Libro libro20 = getLibroFromAPI(
                "9788854513006",
               biografico,
               storico
        );
            
        
        out.info("*************************** Caricamento libro 22/41 ***************************");
        Libro libro21 = getLibroFromAPI(
                "9788858420430",
               biografico,
               storico
        );
            
        
        out.info("*************************** Caricamento libro 23/41 ***************************");
        Libro libro22 = getLibroFromAPI(
                "9788858420423",
               biografico,
               storico
        );
            
        
        out.info("*************************** Caricamento libro 24/41 ***************************");
        Libro libro23 = getLibroFromAPI(
                "9788841878217",
               distopia,
               fantascienza
        );
            
        
        out.info("*************************** Caricamento libro 25/41 ***************************");
        Libro libro24 = getLibroFromAPI(
                "9788854158245",
               romantico,
               fantasy
        );
            
        
        out.info("*************************** Caricamento libro 26/41 ***************************");
        Libro libro25 = getLibroFromAPI(
                "9788858672198",
               comico
        );
            
        
        out.info("*************************** Caricamento libro 27/41 ***************************");
        Libro libro26 = getLibroFromAPI(
                "9788858693322",
               comico,
               narrativa
        );
            
        
        out.info("*************************** Caricamento libro 28/41 ***************************");
        Libro libro27 = getLibroFromAPI(
                "9788865188071",
               scientifico,
               tecnologia
        );
            
        
        out.info("*************************** Caricamento libro 29/41 ***************************");
        Libro libro28 = getLibroFromAPI(
                "9788820096533",
               saggio,
               scientifico
        );
            
        
        out.info("*************************** Caricamento libro 30/41 ***************************");
        Libro libro29 = getLibroFromAPI(
                "9788852022562",
               tecnologia,
               fantascienza,
               narrativa
        );
            
        
        out.info("*************************** Caricamento libro 31/41 ***************************");
        Libro libro30 = getLibroFromAPI(
                "9788852022586",
               fantasy,
               romanzo,
               fantascienza
        );
            
        
        out.info("*************************** Caricamento libro 32/41 ***************************");
        Libro libro31 = getLibroFromAPI(
                "9788868958855",
               tecnologia
        );
            
        
        out.info("*************************** Caricamento libro 33/41 ***************************");
        Libro libro32 = getLibroFromAPI(
                "9788862316019",
               biografico
        );
            
        
        out.info("*************************** Caricamento libro 34/41 ***************************");
        Libro libro33 = getLibroFromAPI(
                "9788852011610",
               fantasy,
               romanzo,
               ragazzi
        );
            
        
        out.info("*************************** Caricamento libro 35/41 ***************************");
        Libro libro34 = getLibroFromAPI(
                "9788868656188",
               romanzo,
               romantico
        );
            
        
        out.info("*************************** Caricamento libro 36/41 ***************************");
        Libro libro35 = getLibroFromAPI(
                "9788854143982",
               avventura
        );
            
        
        out.info("*************************** Caricamento libro 37/41 ***************************");
        Libro libro36 = getLibroFromAPI(
                "9788842922650",
               avventura,
               azione,
               fantasy
        );
            
        
        out.info("*************************** Caricamento libro 38/41 ***************************");
        Libro libro37 = getLibroFromAPI(
                "9788871921501",
               tecnologia
        );
            
        
        out.info("*************************** Caricamento libro 39/41 ***************************");
        Libro libro38 = getLibroFromAPI(
                "9788809906440",
               giallo,
               thriller
        );
            
        
        out.info("*************************** Caricamento libro 40/41 ***************************");
        Libro libro39 = getLibroFromAPI(
                "9781326891220",
               storico,
               biografico
        );
            
        
        out.info("*************************** Caricamento libro 41/41 ***************************");
        Libro libro40 = getLibroFromAPI(
                "9788893128933",
               romanzo,
               fantasy
        );        
                    
        out.info("*************************** Libri creati 5/9 ***************************");

//----------------------Definizione ed inserimento possessi-------------------------------------------------------------

        //BIBLIOTECA 1

        out.info("*************************** Inserimento Possessi biblioteca 1/10 ***************************");

        PossessoId possessoId1B1 = new PossessoId(
                biblioteca.getEmail(),
                libro1.getIdLibro()
        );
        PossessoId possessoId1B2 = new PossessoId(
                biblioteca.getEmail(),
                libro2.getIdLibro()
        );
        PossessoId possessoId1B3 = new PossessoId(
                biblioteca.getEmail(),
                libro3.getIdLibro()
        );
        PossessoId possessoId1B4 = new PossessoId(
                biblioteca.getEmail(),
                libro4.getIdLibro()
        );
        PossessoId possessoId1B5 = new PossessoId(
                biblioteca.getEmail(),
                libro5.getIdLibro()
        );
        PossessoId possessoId1B6 = new PossessoId(
                biblioteca.getEmail(),
                libro6.getIdLibro()
        );
        PossessoId possessoId1B7 = new PossessoId(
                biblioteca.getEmail(),
                libro7.getIdLibro()
        );
        PossessoId possessoId1B8 = new PossessoId(
                biblioteca.getEmail(),
                libro8.getIdLibro()
        );

        Possesso possesso1B1 = new Possesso(
                possessoId1B1,
                10
        );
        Possesso possesso1B2 = new Possesso(
                possessoId1B2,
                10
        );
        Possesso possesso1B3 = new Possesso(
                possessoId1B3,
                10
        );
        Possesso possesso1B4 = new Possesso(
                possessoId1B4,
                10
        );
        Possesso possesso1B5 = new Possesso(
                possessoId1B5,
                10
        );
        Possesso possesso1B6 = new Possesso(
                possessoId1B6,
                10
        );
        Possesso possesso1B7 = new Possesso(
                possessoId1B7,
                10
        );
        Possesso possesso1B8 = new Possesso(
                possessoId1B8,
                10
        );

        possessoDAO.save(possesso1B1);
        possessoDAO.save(possesso1B2);
        possessoDAO.save(possesso1B3);
        possessoDAO.save(possesso1B4);
        possessoDAO.save(possesso1B5);
        possessoDAO.save(possesso1B6);
        possessoDAO.save(possesso1B7);
        possessoDAO.save(possesso1B8);

        //BIBLIOTECA 2
        out.info("*************************** Inserimento Possessi biblioteca 2/10 ***************************");

        PossessoId possessoId2B1 = new PossessoId(
                biblioteca1.getEmail(),
                libro9.getIdLibro()
        );
        PossessoId possessoId2B2 = new PossessoId(
                biblioteca1.getEmail(),
                libro10.getIdLibro()
        );
        PossessoId possessoId2B3 = new PossessoId(
                biblioteca1.getEmail(),
                libro11.getIdLibro()
        );
        PossessoId possessoId2B4 = new PossessoId(
                biblioteca1.getEmail(),
                libro12.getIdLibro()
        );
        PossessoId possessoId2B5 = new PossessoId(
                biblioteca1.getEmail(),
                libro13.getIdLibro()
        );
        PossessoId possessoId2B6 = new PossessoId(
                biblioteca1.getEmail(),
                libro14.getIdLibro()
        );
        PossessoId possessoId2B7 = new PossessoId(
                biblioteca1.getEmail(),
                libro15.getIdLibro()
        );
        PossessoId possessoId2B8 = new PossessoId(
                biblioteca1.getEmail(),
                libro16.getIdLibro()
        );

        Possesso possesso2B1 = new Possesso(
                possessoId2B1,
                10
        );
        Possesso possesso2B2 = new Possesso(
                possessoId2B2,
                10
        );
        Possesso possesso2B3 = new Possesso(
                possessoId2B3,
                10
        );
        Possesso possesso2B4 = new Possesso(
                possessoId2B4,
                10
        );
        Possesso possesso2B5 = new Possesso(
                possessoId2B5,
                10
        );
        Possesso possesso2B6 = new Possesso(
                possessoId2B6,
                10
        );
        Possesso possesso2B7 = new Possesso(
                possessoId2B7,
                10
        );
        Possesso possesso2B8 = new Possesso(
                possessoId2B8,
                10
        );

        possessoDAO.save(possesso2B1);
        possessoDAO.save(possesso2B2);
        possessoDAO.save(possesso2B3);
        possessoDAO.save(possesso2B4);
        possessoDAO.save(possesso2B5);
        possessoDAO.save(possesso2B6);
        possessoDAO.save(possesso2B7);
        possessoDAO.save(possesso2B8);

        //BIBLIOTECA 3
        out.info("*************************** Inserimento Possessi biblioteca 3/10 ***************************");

        PossessoId possessoId3B1 = new PossessoId(
                biblioteca2.getEmail(),
                libro17.getIdLibro()
        );
        PossessoId possessoId3B2 = new PossessoId(
                biblioteca2.getEmail(),
                libro18.getIdLibro()
        );
        PossessoId possessoId3B3 = new PossessoId(
                biblioteca2.getEmail(),
                libro19.getIdLibro()
        );
        PossessoId possessoId3B4 = new PossessoId(
                biblioteca2.getEmail(),
                libro20.getIdLibro()
        );
        PossessoId possessoId3B5 = new PossessoId(
                biblioteca2.getEmail(),
                libro21.getIdLibro()
        );
        PossessoId possessoId3B6 = new PossessoId(
                biblioteca2.getEmail(),
                libro22.getIdLibro()
        );
        PossessoId possessoId3B7 = new PossessoId(
                biblioteca2.getEmail(),
                libro23.getIdLibro()
        );
        PossessoId possessoId3B8 = new PossessoId(
                biblioteca2.getEmail(),
                libro24.getIdLibro()
        );

        Possesso possesso3B1 = new Possesso(
                possessoId3B1,
                10
        );
        Possesso possesso3B2 = new Possesso(
                possessoId3B2,
                10
        );
        Possesso possesso3B3 = new Possesso(
                possessoId3B3,
                10
        );
        Possesso possesso3B4 = new Possesso(
                possessoId3B4,
                10
        );
        Possesso possesso3B5 = new Possesso(
                possessoId3B5,
                10
        );
        Possesso possesso3B6 = new Possesso(
                possessoId3B6,
                10
        );
        Possesso possesso3B7 = new Possesso(
                possessoId3B7,
                10
        );
        Possesso possesso3B8 = new Possesso(
                possessoId3B8,
                10
        );

        possessoDAO.save(possesso3B1);
        possessoDAO.save(possesso3B2);
        possessoDAO.save(possesso3B3);
        possessoDAO.save(possesso3B4);
        possessoDAO.save(possesso3B5);
        possessoDAO.save(possesso3B6);
        possessoDAO.save(possesso3B7);
        possessoDAO.save(possesso3B8);


        //BIBLIOTECA 4
        out.info("*************************** Inserimento Possessi biblioteca 4/10 ***************************");

        PossessoId possessoId4B1 = new PossessoId(
                biblioteca3.getEmail(),
                libro32.getIdLibro()
        );
        PossessoId possessoId4B2 = new PossessoId(
                biblioteca3.getEmail(),
                libro25.getIdLibro()
        );
        PossessoId possessoId4B3 = new PossessoId(
                biblioteca3.getEmail(),
                libro26.getIdLibro()
        );
        PossessoId possessoId4B4 = new PossessoId(
                biblioteca3.getEmail(),
                libro27.getIdLibro()
        );
        PossessoId possessoId4B5 = new PossessoId(
                biblioteca3.getEmail(),
                libro28.getIdLibro()
        );
        PossessoId possessoId4B6 = new PossessoId(
                biblioteca3.getEmail(),
                libro29.getIdLibro()
        );
        PossessoId possessoId4B7 = new PossessoId(
                biblioteca3.getEmail(),
                libro30.getIdLibro()
        );
        PossessoId possessoId4B8 = new PossessoId(
                biblioteca3.getEmail(),
                libro31.getIdLibro()
        );

        Possesso possesso4B1 = new Possesso(
                possessoId4B1,
                10
        );
        Possesso possesso4B2 = new Possesso(
                possessoId4B2,
                10
        );
        Possesso possesso4B3 = new Possesso(
                possessoId4B3,
                10
        );
        Possesso possesso4B4 = new Possesso(
                possessoId4B4,
                10
        );
        Possesso possesso4B5 = new Possesso(
                possessoId4B5,
                10
        );
        Possesso possesso4B6 = new Possesso(
                possessoId4B6,
                10
        );
        Possesso possesso4B7 = new Possesso(
                possessoId4B7,
                10
        );
        Possesso possesso4B8 = new Possesso(
                possessoId4B8,
                10
        );

        possessoDAO.save(possesso4B1);
        possessoDAO.save(possesso4B2);
        possessoDAO.save(possesso4B3);
        possessoDAO.save(possesso4B4);
        possessoDAO.save(possesso4B5);
        possessoDAO.save(possesso4B6);
        possessoDAO.save(possesso4B7);
        possessoDAO.save(possesso4B8);


        //BIBLIOTECA 5
        out.info("*************************** Inserimento Possessi biblioteca 5/10 ***************************");

        PossessoId possessoId5B1 = new PossessoId(
                biblioteca4.getEmail(),
                libro33.getIdLibro()
        );
        PossessoId possessoId5B2 = new PossessoId(
                biblioteca4.getEmail(),
                libro40.getIdLibro()
        );
        PossessoId possessoId5B3 = new PossessoId(
                biblioteca4.getEmail(),
                libro34.getIdLibro()
        );
        PossessoId possessoId5B4 = new PossessoId(
                biblioteca4.getEmail(),
                libro35.getIdLibro()
        );
        PossessoId possessoId5B5 = new PossessoId(
                biblioteca4.getEmail(),
                libro36.getIdLibro()
        );
        PossessoId possessoId5B6 = new PossessoId(
                biblioteca4.getEmail(),
                libro37.getIdLibro()
        );
        PossessoId possessoId5B7 = new PossessoId(
                biblioteca4.getEmail(),
                libro38.getIdLibro()
        );
        PossessoId possessoId5B8 = new PossessoId(
                biblioteca4.getEmail(),
                libro39.getIdLibro()
        );

        Possesso possesso5B1 = new Possesso(
                possessoId5B1,
                10
        );
        Possesso possesso5B2 = new Possesso(
                possessoId5B2,
                10
        );
        Possesso possesso5B3 = new Possesso(
                possessoId5B3,
                10
        );
        Possesso possesso5B4 = new Possesso(
                possessoId5B4,
                10
        );
        Possesso possesso5B5 = new Possesso(
                possessoId5B5,
                10
        );
        Possesso possesso5B6 = new Possesso(
                possessoId5B6,
                10
        );
        Possesso possesso5B7 = new Possesso(
                possessoId5B7,
                10
        );
        Possesso possesso5B8 = new Possesso(
                possessoId5B8,
                10
        );

        possessoDAO.save(possesso5B1);
        possessoDAO.save(possesso5B2);
        possessoDAO.save(possesso5B3);
        possessoDAO.save(possesso5B4);
        possessoDAO.save(possesso5B5);
        possessoDAO.save(possesso5B6);
        possessoDAO.save(possesso5B7);
        possessoDAO.save(possesso5B8);


        //BIBLIOTECA 6
        out.info("*************************** Inserimento Possessi biblioteca 6/10 ***************************");

        PossessoId possessoId6B1 = new PossessoId(
                biblioteca5.getEmail(),
                libro1.getIdLibro()
        );
        PossessoId possessoId6B2 = new PossessoId(
                biblioteca5.getEmail(),
                libro2.getIdLibro()
        );
        PossessoId possessoId6B3 = new PossessoId(
                biblioteca5.getEmail(),
                libro3.getIdLibro()
        );
        PossessoId possessoId6B4 = new PossessoId(
                biblioteca5.getEmail(),
                libro4.getIdLibro()
        );
        PossessoId possessoId6B5 = new PossessoId(
                biblioteca5.getEmail(),
                libro5.getIdLibro()
        );
        PossessoId possessoId6B6 = new PossessoId(
                biblioteca5.getEmail(),
                libro6.getIdLibro()
        );
        PossessoId possessoId6B7 = new PossessoId(
                biblioteca5.getEmail(),
                libro7.getIdLibro()
        );
        PossessoId possessoId6B8 = new PossessoId(
                biblioteca5.getEmail(),
                libro8.getIdLibro()
        );

        Possesso possesso6B1 = new Possesso(
                possessoId6B1,
                10
        );
        Possesso possesso6B2 = new Possesso(
                possessoId6B2,
                10
        );
        Possesso possesso6B3 = new Possesso(
                possessoId6B3,
                10
        );
        Possesso possesso6B4 = new Possesso(
                possessoId6B4,
                10
        );
        Possesso possesso6B5 = new Possesso(
                possessoId6B5,
                10
        );
        Possesso possesso6B6 = new Possesso(
                possessoId6B6,
                10
        );
        Possesso possesso6B7 = new Possesso(
                possessoId6B7,
                10
        );
        Possesso possesso6B8 = new Possesso(
                possessoId6B8,
                10
        );

        possessoDAO.save(possesso6B1);
        possessoDAO.save(possesso6B2);
        possessoDAO.save(possesso6B3);
        possessoDAO.save(possesso6B4);
        possessoDAO.save(possesso6B5);
        possessoDAO.save(possesso6B6);
        possessoDAO.save(possesso6B7);
        possessoDAO.save(possesso6B8);


        //BIBLIOTECA 7
        out.info("*************************** Inserimento Possessi biblioteca 7/10 ***************************");

        PossessoId possessoId7B1 = new PossessoId(
                biblioteca6.getEmail(),
                libro9.getIdLibro()
        );
        PossessoId possessoId7B2 = new PossessoId(
                biblioteca6.getEmail(),
                libro10.getIdLibro()
        );
        PossessoId possessoId7B3 = new PossessoId(
                biblioteca6.getEmail(),
                libro11.getIdLibro()
        );
        PossessoId possessoId7B4 = new PossessoId(
                biblioteca6.getEmail(),
                libro12.getIdLibro()
        );
        PossessoId possessoId7B5 = new PossessoId(
                biblioteca6.getEmail(),
                libro13.getIdLibro()
        );
        PossessoId possessoId7B6 = new PossessoId(
                biblioteca6.getEmail(),
                libro14.getIdLibro()
        );
        PossessoId possessoId7B7 = new PossessoId(
                biblioteca6.getEmail(),
                libro15.getIdLibro()
        );
        PossessoId possessoId7B8 = new PossessoId(
                biblioteca6.getEmail(),
                libro16.getIdLibro()
        );

        Possesso possesso7B1 = new Possesso(
                possessoId7B1,
                10
        );
        Possesso possesso7B2 = new Possesso(
                possessoId7B2,
                10
        );
        Possesso possesso7B3 = new Possesso(
                possessoId7B3,
                10
        );
        Possesso possesso7B4 = new Possesso(
                possessoId7B4,
                10
        );
        Possesso possesso7B5 = new Possesso(
                possessoId7B5,
                10
        );
        Possesso possesso7B6 = new Possesso(
                possessoId7B6,
                10
        );
        Possesso possesso7B7 = new Possesso(
                possessoId7B7,
                10
        );
        Possesso possesso7B8 = new Possesso(
                possessoId7B8,
                10
        );

        possessoDAO.save(possesso7B1);
        possessoDAO.save(possesso7B2);
        possessoDAO.save(possesso7B3);
        possessoDAO.save(possesso7B4);
        possessoDAO.save(possesso7B5);
        possessoDAO.save(possesso7B6);
        possessoDAO.save(possesso7B7);
        possessoDAO.save(possesso7B8);

        //BIBLIOTECA 8
        out.info("*************************** Inserimento Possessi biblioteca 8/10 ***************************");

        PossessoId possessoId8B1 = new PossessoId(
                biblioteca7.getEmail(),
                libro17.getIdLibro()
        );
        PossessoId possessoId8B2 = new PossessoId(
                biblioteca7.getEmail(),
                libro18.getIdLibro()
        );
        PossessoId possessoId8B3 = new PossessoId(
                biblioteca7.getEmail(),
                libro19.getIdLibro()
        );
        PossessoId possessoId8B4 = new PossessoId(
                biblioteca7.getEmail(),
                libro20.getIdLibro()
        );
        PossessoId possessoId8B5 = new PossessoId(
                biblioteca7.getEmail(),
                libro21.getIdLibro()
        );
        PossessoId possessoId8B6 = new PossessoId(
                biblioteca7.getEmail(),
                libro22.getIdLibro()
        );
        PossessoId possessoId8B7 = new PossessoId(
                biblioteca7.getEmail(),
                libro23.getIdLibro()
        );
        PossessoId possessoId8B8 = new PossessoId(
                biblioteca7.getEmail(),
                libro24.getIdLibro()
        );

        Possesso possesso8B1 = new Possesso(
                possessoId8B1,
                10
        );
        Possesso possesso8B2 = new Possesso(
                possessoId8B2,
                10
        );
        Possesso possesso8B3 = new Possesso(
                possessoId8B3,
                10
        );
        Possesso possesso8B4 = new Possesso(
                possessoId8B4,
                10
        );
        Possesso possesso8B5 = new Possesso(
                possessoId8B5,
                10
        );
        Possesso possesso8B6 = new Possesso(
                possessoId8B6,
                10
        );
        Possesso possesso8B7 = new Possesso(
                possessoId8B7,
                10
        );
        Possesso possesso8B8 = new Possesso(
                possessoId8B8,
                10
        );

        possessoDAO.save(possesso8B1);
        possessoDAO.save(possesso8B2);
        possessoDAO.save(possesso8B3);
        possessoDAO.save(possesso8B4);
        possessoDAO.save(possesso8B5);
        possessoDAO.save(possesso8B6);
        possessoDAO.save(possesso8B7);
        possessoDAO.save(possesso8B8);


        //BIBLIOTECA 9
        out.info("*************************** Inserimento Possessi biblioteca 9/10 ***************************");

        PossessoId possessoId9B1 = new PossessoId(
                biblioteca8.getEmail(),
                libro32.getIdLibro()
        );
        PossessoId possessoId9B2 = new PossessoId(
                biblioteca8.getEmail(),
                libro25.getIdLibro()
        );
        PossessoId possessoId9B3 = new PossessoId(
                biblioteca8.getEmail(),
                libro26.getIdLibro()
        );
        PossessoId possessoId9B4 = new PossessoId(
                biblioteca8.getEmail(),
                libro27.getIdLibro()
        );
        PossessoId possessoId9B5 = new PossessoId(
                biblioteca8.getEmail(),
                libro28.getIdLibro()
        );
        PossessoId possessoId9B6 = new PossessoId(
                biblioteca8.getEmail(),
                libro29.getIdLibro()
        );
        PossessoId possessoId9B7 = new PossessoId(
                biblioteca8.getEmail(),
                libro30.getIdLibro()
        );
        PossessoId possessoId9B8 = new PossessoId(
                biblioteca8.getEmail(),
                libro31.getIdLibro()
        );

        Possesso possesso9B1 = new Possesso(
                possessoId9B1,
                10
        );
        Possesso possesso9B2 = new Possesso(
                possessoId9B2,
                10
        );
        Possesso possesso9B3 = new Possesso(
                possessoId9B3,
                10
        );
        Possesso possesso9B4 = new Possesso(
                possessoId9B4,
                10
        );
        Possesso possesso9B5 = new Possesso(
                possessoId9B5,
                10
        );
        Possesso possesso9B6 = new Possesso(
                possessoId9B6,
                10
        );
        Possesso possesso9B7 = new Possesso(
                possessoId9B7,
                10
        );
        Possesso possesso9B8 = new Possesso(
                possessoId9B8,
                10
        );

        possessoDAO.save(possesso9B1);
        possessoDAO.save(possesso9B2);
        possessoDAO.save(possesso9B3);
        possessoDAO.save(possesso9B4);
        possessoDAO.save(possesso9B5);
        possessoDAO.save(possesso9B6);
        possessoDAO.save(possesso9B7);
        possessoDAO.save(possesso9B8);


        //BIBLIOTECA 10
        out.info("*************************** Inserimento Possessi biblioteca 10/10 ***************************");

        PossessoId possessoId10B1 = new PossessoId(
                biblioteca9.getEmail(),
                libro33.getIdLibro()
        );
        PossessoId possessoId10B2 = new PossessoId(
                biblioteca9.getEmail(),
                libro40.getIdLibro()
        );
        PossessoId possessoId10B3 = new PossessoId(
                biblioteca9.getEmail(),
                libro34.getIdLibro()
        );
        PossessoId possessoId10B4 = new PossessoId(
                biblioteca9.getEmail(),
                libro35.getIdLibro()
        );
        PossessoId possessoId10B5 = new PossessoId(
                biblioteca9.getEmail(),
                libro36.getIdLibro()
        );
        PossessoId possessoId10B6 = new PossessoId(
                biblioteca9.getEmail(),
                libro37.getIdLibro()
        );
        PossessoId possessoId10B7 = new PossessoId(
                biblioteca9.getEmail(),
                libro38.getIdLibro()
        );
        PossessoId possessoId10B8 = new PossessoId(
                biblioteca9.getEmail(),
                libro39.getIdLibro()
        );

        Possesso possesso10B1 = new Possesso(
                possessoId10B1,
                10
        );
        Possesso possesso10B2 = new Possesso(
                possessoId10B2,
                10
        );
        Possesso possesso10B3 = new Possesso(
                possessoId10B3,
                10
        );
        Possesso possesso10B4 = new Possesso(
                possessoId10B4,
                10
        );
        Possesso possesso10B5 = new Possesso(
                possessoId10B5,
                10
        );
        Possesso possesso10B6 = new Possesso(
                possessoId10B6,
                10
        );
        Possesso possesso10B7 = new Possesso(
                possessoId10B7,
                10
        );
        Possesso possesso10B8 = new Possesso(
                possessoId10B8,
                10
        );

        possessoDAO.save(possesso10B1);
        possessoDAO.save(possesso10B2);
        possessoDAO.save(possesso10B3);
        possessoDAO.save(possesso10B4);
        possessoDAO.save(possesso10B5);
        possessoDAO.save(possesso10B6);
        possessoDAO.save(possesso10B7);
        possessoDAO.save(possesso10B8);

        out.info("*************************** Possessi creati 6/9 ***************************");

//----------------------Definizione ed inserimento ticket prestiti------------------------------------------------------


        TicketPrestito ticket = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_CONFERMA,
                LocalDateTime.now(),
                libro1,
                biblioteca,
                lettore
        );

        TicketPrestito ticket1 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_RESTITUZIONE,
                LocalDateTime.now(),
                libro2,
                biblioteca,
                lettore2
        );

        TicketPrestito ticket2 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_CONFERMA,
                LocalDateTime.now(),
                libro3,
                biblioteca,
                lettore3
        );

        ticketPrestitoDAO.save(ticket);
        ticketPrestitoDAO.save(ticket1);
        ticketPrestitoDAO.save(ticket2);

        TicketPrestito ticket3 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_CONFERMA,
                LocalDateTime.now(),
                libro8,
                biblioteca1,
                lettore4
        );

        TicketPrestito ticket4 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_RESTITUZIONE,
                LocalDateTime.now(),
                libro9,
                biblioteca1,
                lettore5
        );

        TicketPrestito ticket5 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_CONFERMA,
                LocalDateTime.now(),
                libro10,
                biblioteca1,
                lettore6
        );

        ticketPrestitoDAO.save(ticket3);
        ticketPrestitoDAO.save(ticket4);
        ticketPrestitoDAO.save(ticket5);

        TicketPrestito ticket6 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_CONFERMA,
                LocalDateTime.now(),
                libro17,
                biblioteca2,
                lettore9
        );

        TicketPrestito ticket7 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_RESTITUZIONE,
                LocalDateTime.now(),
                libro18,
                biblioteca2,
                lettore10
        );

        TicketPrestito ticket8 = new TicketPrestito(
                TicketPrestito.Stati.IN_ATTESA_DI_CONFERMA,
                LocalDateTime.now(),
                libro19,
                biblioteca2,
                lettore12
        );

        ticketPrestitoDAO.save(ticket6);
        ticketPrestitoDAO.save(ticket7);
        ticketPrestitoDAO.save(ticket8);

        out.info("*************************** Ticket creati 7/9 ***************************");

//----------------------Definizione ed inserimento clubs----------------------------------------------------------------


        ClubDelLibro clubDelLibro1 = new ClubDelLibro(
                "Fantasticy",
                "Si parla di libri fantasy",
                esperto
        );

        ClubDelLibro clubDelLibro2 = new ClubDelLibro(
                "Storici si diventa",
                "Siamo appassionati di storia",
                esperto1
        );


        ClubDelLibro clubDelLibro3 = new ClubDelLibro(
                "Programmatori uniti",
                "Leggiamo documentazione javadoc",
                esperto3
        );

        ClubDelLibro clubDelLibro4 = new ClubDelLibro(
                "Favole e fiabe",
                "Per tutti gli appassionati delle favole!",
                esperto5
        );

        ClubDelLibro clubDelLibro5 = new ClubDelLibro(
                "The Pirates",
                "Appassionati all'avventura e all'azione!",
                esperto8
        );

        ClubDelLibro clubDelLibro6 = new ClubDelLibro(
                "Gli investigatori di Biblionet",
                "Qui ci sono i migliori intenditori di gialli",
                esperto11
        );

        ClubDelLibro clubDelLibro7 = new ClubDelLibro(
                "Psicologia e scienza",
                "Analizziamo testi psicologici e scientifici",
                esperto13
        );

        ClubDelLibro clubDelLibro8 = new ClubDelLibro(
                "#inRosa",
                "Per chi non ha vergogna di ammettere di amare il rosa",
                esperto20
        );

        ClubDelLibro clubDelLibro9 = new ClubDelLibro(
                "BlackBoys",
                "Ami il thriller e il noir? Unisciti a noi!",
                esperto15
        );

        ClubDelLibro clubDelLibro10 = new ClubDelLibro(
                "BOO!",
                "Per i veri appassionati di horror!",
                esperto17
        );

        clubDelLibro1.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/fantasticy.jpg"));
        clubDelLibro2.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/storia.jpg"));
        clubDelLibro3.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/programmatori.jpg"));
        clubDelLibro4.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/fiabe.jpg"));
        clubDelLibro5.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/pirati.jpg"));
        clubDelLibro6.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/investigatore.png"));
        clubDelLibro7.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/psicologia_scienza.jpg"));
        clubDelLibro8.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/rosa.jpg"));
        clubDelLibro9.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/noir.jpg"));
        clubDelLibro10.setImmagineCopertina(getCopertinaClubFromUrl("src/main/resources/static/image/copertine/horror.jpg"));


        clubDelLibroDAO.save(clubDelLibro1);
        clubDelLibroDAO.save(clubDelLibro2);
        clubDelLibroDAO.save(clubDelLibro3);
        clubDelLibroDAO.save(clubDelLibro4);
        clubDelLibroDAO.save(clubDelLibro5);
        clubDelLibroDAO.save(clubDelLibro6);
        clubDelLibroDAO.save(clubDelLibro7);
        clubDelLibroDAO.save(clubDelLibro8);
        clubDelLibroDAO.save(clubDelLibro9);
        clubDelLibroDAO.save(clubDelLibro10);

        out.info("*************************** Club del Libro creati 8/9 ***************************");

//----------------------Definizione ed inserimento eventi---------------------------------------------------------------

        Evento evento = new Evento(
                "Evento fantastyco",
                "Evento fantastyco per gente fantastyca",
                LocalDateTime.now(),
                clubDelLibro1
        );

        Evento evento2 = new Evento(
                "Analizziamo la GEOGRAFIA",
                "Sembra noioso ma non dovrebbe esserlo troppo",
                LocalDateTime.now(),
                clubDelLibro2
        );

        Evento evento3 = new Evento(
                "Impariamo i Design Patterns",
                "Impariamo i Design Patterns come veri ingegneri del software",
                LocalDateTime.now(),
                clubDelLibro3
        );

        Evento evento4 = new Evento(
                "Fiabe originali vs Disney",
                "Analizziamo le differenze fra i famosi libri di fiabe e i classici Disney",
                LocalDateTime.now(),
                clubDelLibro4
        );

        Evento evento5 = new Evento(
                "Azione 01!",
                "Parliamo dei migliori libri d'azione del mese di gennaio",
                LocalDateTime.now(),
                clubDelLibro5
        );

        Evento evento6 = new Evento(
                "Risolviamo il caso",
                "Chi riesce a indovinare il colpevole prima della fine del libro?",
                LocalDateTime.now(),
                clubDelLibro6
        );

        Evento evento7 = new Evento(
                "Freud: genio o cretino?",
                "Discussione sulle tesi di Freud",
                LocalDateTime.now(),
                clubDelLibro8
        );


        Evento evento8 = new Evento(
                "Classifica romanzi rosa",
                "Discutiamo insieme e stiliamo una classifica dei romanzi rosa preferiti!",
                LocalDateTime.now(),
                clubDelLibro7
        );

        Evento evento9 = new Evento(
                "King's Fanboy",
                "Gloria alle storie di King",
                LocalDateTime.now(),
                clubDelLibro9
        );

        Evento evento10 = new Evento(
                "Maratona Harry Potter",
                "Rileggiamo le parti più belle della saga di Harry Potter",
                LocalDateTime.now(),
                clubDelLibro1
        );

        Evento evento11 = new Evento(
                "Analizziamo la STORIA",
                "Sembra noioso ma non dovrebbe esserlo troppo",
                LocalDateTime.now(),
                clubDelLibro2
        );

        Evento evento12 = new Evento(
                "Le basi per un buon giallo",
                "Un esperto tiene una lezione su come scrivere un buon giallo",
                LocalDateTime.now(),
                clubDelLibro6
        );

        Evento evento13 = new Evento(
                "Thrills and Chills",
                "Quale thriller non ti ha lasciato col fiato sospeso?",
                LocalDateTime.now(),
                clubDelLibro9
        );

        eventoDAO.save(evento);
        eventoDAO.save(evento2);
        eventoDAO.save(evento3);
        eventoDAO.save(evento4);
        eventoDAO.save(evento5);
        eventoDAO.save(evento6);
        eventoDAO.save(evento7);
        eventoDAO.save(evento8);
        eventoDAO.save(evento9);
        eventoDAO.save(evento10);
        eventoDAO.save(evento11);
        eventoDAO.save(evento12);
        eventoDAO.save(evento13);

        out.info("*************************** Eventi creati 9/9 ***************************");

        //-------------------------------Inserimento Recensioni-------------------------------------------------
        out.info("*************************** Recensioni create 3/3 ***************************");

        Recensione recensione1 = new Recensione("Prova","prova",libro1,esperto1);
        Recensione recensione2 = new Recensione("Prova2", "Era Bello1", libro4, esperto4);
        Recensione recensione3 = new Recensione("Prova3", "Era Bello2", libro2, esperto3);

        recensioneDAO.save(recensione1);
        recensioneDAO.save(recensione2);
        recensioneDAO.save(recensione3);


        //-------------------------------Inserimento Commenti-------------------------------------------------


        Commento commento1 = new Commento("carminedileo","è bello",recensione1,"carminedileo");
        Commento commento2 = new Commento("gaiaclerici","è bello",recensione3,"gaiaclerici");
        Commento commento3 = new Commento("giuliomedici","insomma....",recensione2,"giuliomedici");
        Commento commento4 = new Commento("paoloBonolis","sembra bello",recensione1,"paoloBonolis");

        commentoDAO.save(commento1);
        commentoDAO.save(commento2);
        commentoDAO.save(commento3);

//-------------------------------COMMENTO RISPOSTA-------------------------------------------------
        CommentoRisposta risposta1 = new CommentoRisposta("paoloBonolis","non sono d'accordo",commento1,"paoloBonolis");
        CommentoRisposta risposta2 = new CommentoRisposta("giuliomedici","sono d'accordo",commento1,"giuliomedici");
        CommentoRisposta risposta3 = new CommentoRisposta("gaiaclerici","esagerato",commento1,"gaiaclerici");

        commentoRispostaDAO.save(risposta1);
        commentoRispostaDAO.save(risposta2);
        commentoRispostaDAO.save(risposta3);


        //-------------------------------Inserimento FaQ-------------------------------------------------
        out.info("*************************** DomandeBot create 4/4 ***************************");

        ChatBot chatBLettore = new ChatBot("ChatBot",false);
        ChatBot chatBEsperto = new ChatBot("ChatBotEsperto",true);

        chatBotDAO.save(chatBLettore);
        chatBotDAO.save(chatBEsperto);

        //-------------------------------Inserimento DomandeBot-------------------------------------------------
        out.info("*************************** DomandeBot create 4/4 ***************************");

        Risposta rispostaLettore1 = new Risposta("Informazioni per l'utente", Categoria.GESTIONE_UTENTE,chatBLettore);
        Risposta rispostaLettore2 = new Risposta("Informazioni per club del libro",Categoria.GESTIONE_CLUB_LIBRO,chatBLettore);
        Risposta rispostaLettore3 = new Risposta("Informazioni per Prenotazioni Libri",Categoria.PRENOTAZIONE_LIBRO,chatBLettore);
        Risposta rispostaLettore4 = new Risposta("Informazioni per il blog",Categoria.GESTIONE_BLOG,chatBLettore);
        Risposta rispostaLettore5 = new Risposta("Informazioni per la scelta genere",Categoria.PREFERENZA_GENERE,chatBLettore);
        Risposta rispostaLettore6 = new Risposta("Informazioni per comunicare con esperto",Categoria.SEZIONE_ESPERTO,chatBLettore);

        Risposta rispostaEsperto1 = new Risposta("Informazioni per l'utente", Categoria.GESTIONE_UTENTE, chatBEsperto);
        Risposta rispostaEsperto2 = new Risposta("Informazioni per club del libro",Categoria.GESTIONE_CLUB_LIBRO, chatBEsperto);
        Risposta rispostaEsperto4 = new Risposta("Informazioni per il blog",Categoria.GESTIONE_BLOG, chatBEsperto);
        Risposta rispostaEsperto5 = new Risposta("Informazioni per la scelta genere",Categoria.PREFERENZA_GENERE, chatBEsperto);


        rispostaLettore1.setMapLink("/area-utente");
        rispostaLettore2.setMapLink("/club-del-libro");
        rispostaLettore3.setMapLink("/prenotazione-libri");
        rispostaLettore4.setMapLink("/blog");
        rispostaLettore5.setMapLink("/preferenze-di-lettura/generi");
        rispostaLettore6.setMapLink("/comunicazione-esperto");

        rispostaEsperto1.setMapLink("/area-utente");
        rispostaEsperto2.setMapLink("/club-del-libro");
        rispostaEsperto4.setMapLink("/blog");
        rispostaEsperto5.setMapLink("/preferenze-di-lettura/generi");

        rispostaDAO.save(rispostaLettore1);
        rispostaDAO.save(rispostaLettore2);
        rispostaDAO.save(rispostaLettore3);
        rispostaDAO.save(rispostaLettore4);
        rispostaDAO.save(rispostaLettore5);
        rispostaDAO.save(rispostaLettore6);

        rispostaDAO.save(rispostaEsperto1);
        rispostaDAO.save(rispostaEsperto2);
        //domandaDAO.save(domandaEsperto3);
        rispostaDAO.save(rispostaEsperto4);
        rispostaDAO.save(rispostaEsperto5);

        //-------------------------------Inserimento DomandeRisposta -------------------------------------------------
        out.info("*************************** RipsotaBot create 1/5 ***************************");

        Domanda domandaClubL1 = new Domanda("La sezione Club del libro ti permetti di visualizzare i vari clubs..", Categoria.GESTIONE_CLUB_LIBRO,rispostaLettore2);
        Domanda domandaClubL2 = new Domanda("Se scegli di visualizzarne uno puoi iscriverti",Categoria.GESTIONE_CLUB_LIBRO,rispostaLettore2);
        Domanda domandaClubL3 = new Domanda("Se scegli di visualizzarne uno puoi abbandonare se sei iscritto",Categoria.GESTIONE_CLUB_LIBRO,rispostaLettore2);
        Domanda domandaClubL4 = new Domanda("Se scegli di visualizzare e sei iscritto puoi partecipare ad un evento",Categoria.GESTIONE_CLUB_LIBRO,rispostaLettore2);
        Domanda domandaClubL5 = new Domanda("Se scegli di visualizzare e sei iscritto puoi partecipare abbandonare un evento",Categoria.GESTIONE_CLUB_LIBRO,rispostaLettore2);
        Domanda domandaClubL6 = new Domanda("Se scegli di visualizzare e sei iscritto puoi comunicare con gli altri membri premendo l'opzione contatta",Categoria.GESTIONE_CLUB_LIBRO,rispostaLettore2);

        Domanda domandaEClubL1 = new Domanda("La sezione Club del libro ti permette di visualizzare i vari clubs..", Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);
        Domanda domandaEClubL2 = new Domanda("Come Esperto puoi creare nuovi club",Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);
        Domanda domandaEClubL3 = new Domanda("Come esperto puoi modificare i dati del club del libro",Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);
        Domanda domandaEClubL4 = new Domanda("Come esperto puoi eliminare i tuoi club del libro",Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);
        Domanda domandaEClubL5 = new Domanda("Come esperto puoi visualizzare e modificare gli eventi del tuo club del libro",Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);
        Domanda domandaEClubL6 = new Domanda("Come esperto puoi eliminare gli eventi del tuo club del libro",Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);
        Domanda domandaEClubL7 = new Domanda("Come esperto puoi contattare altri utenti presenti nel tuo club del libro",Categoria.GESTIONE_CLUB_LIBRO,rispostaEsperto2);

        domandaDAO.save(domandaClubL1);
        domandaDAO.save(domandaClubL2);
        domandaDAO.save(domandaClubL3);
        domandaDAO.save(domandaClubL4);
        domandaDAO.save(domandaClubL5);
        domandaDAO.save(domandaClubL6);

        domandaDAO.save(domandaEClubL1);
        domandaDAO.save(domandaEClubL2);
        domandaDAO.save(domandaEClubL3);
        domandaDAO.save(domandaEClubL4);
        domandaDAO.save(domandaEClubL5);
        domandaDAO.save(domandaEClubL6);
        domandaDAO.save(domandaEClubL7);

        out.info("*************************** RispostaBot create 2/5 ***************************");

        Domanda domandaUtente1 = new Domanda("Come utente se premi area utente in cima a destra,premi logout per scollegarti", Categoria.GESTIONE_UTENTE,rispostaLettore1);
        Domanda domandaUtente2 = new Domanda("Se premi in cima area utente e premi area utente nel menu,all'interno dell'area Utente puoi modificare i tuoi dati",Categoria.GESTIONE_UTENTE,rispostaLettore1);
        Domanda domandaUtente3 = new Domanda("In base ai tuoi generi,nell'area utenti ti viene presentata la lista degli Esperto per te!",Categoria.GESTIONE_UTENTE,rispostaLettore1);
        Domanda domandaUtente4 = new Domanda("Nell'area Utente puoi visualizzare i tickets delle Prenotazioni Effettuate",Categoria.GESTIONE_UTENTE,rispostaLettore1);

        Domanda domandaEUtente1 = new Domanda("Come utente se premi area utente in cima a destra,premi logout per scollegarti", Categoria.GESTIONE_UTENTE,rispostaEsperto1);
        Domanda domandaEUtente2 = new Domanda("Se premi in cima area utente e premi area utente nel menu,all'interno dell'area Utente puoi modificare i tuoi dati",Categoria.GESTIONE_UTENTE,rispostaEsperto1);
        Domanda domandaEUtente3 = new Domanda("In base ai tuoi generi,nell'area utenti ti viene presentata la lista degli Esperto per te!",Categoria.GESTIONE_UTENTE,rispostaEsperto1);


        domandaDAO.save(domandaUtente1);
        domandaDAO.save(domandaUtente2);
        domandaDAO.save(domandaUtente3);
        domandaDAO.save(domandaUtente4);

        domandaDAO.save(domandaEUtente1);
        domandaDAO.save(domandaEUtente2);
        domandaDAO.save(domandaEUtente3);



        out.info("*************************** RispostaBot create 3/5 ***************************");

        Domanda domandaPre1 = new Domanda("In prenotazioni Libri puoi visualizzare la lista libri disponibile", Categoria.PRENOTAZIONE_LIBRO,rispostaLettore3);
        Domanda domandaPre2 = new Domanda("Nella home Page per la prenotazioni hai un filtro per cercare il libro a " +
                "te più adatto in base al genere,titolo o per biblioteca di appartenenza",
                Categoria.PRENOTAZIONE_LIBRO,rispostaLettore3);

        Domanda domandaPre3 = new Domanda("Cliccando la lenta d'ingradimento puoi scegliere la lista libri da prenotare",Categoria.PRENOTAZIONE_LIBRO,rispostaLettore3);
        Domanda domandaPre4 = new Domanda("Se clicchi la lente ti si apre la pagina dove puoi scegliere la biblioteca a cui mandare la prenotazione",Categoria.PRENOTAZIONE_LIBRO,rispostaLettore3);
        Domanda domandaPre5 = new Domanda("Puoi visualizzare le tue prenotazioni,in homePage sezione VisualizzaTicket",Categoria.PRENOTAZIONE_LIBRO,rispostaLettore3);


        domandaDAO.save(domandaPre1);
        domandaDAO.save(domandaPre2);
        domandaDAO.save(domandaPre3);
        domandaDAO.save(domandaPre4);
        domandaDAO.save(domandaPre5);

        out.info("*************************** RispostaBot create 4/5 ***************************");

        Domanda domandaPrl1 = new Domanda("All'interno dell'area Utente nella sezione gestione genere puoi visualizzare i tuoi generi", Categoria.PREFERENZA_GENERE,rispostaLettore5);
        Domanda domandaPrl2 = new Domanda("Puoi aggiungere i tuoi generi selezionandoli e cliccando il pulsante verde e salvarli",Categoria.PREFERENZA_GENERE,rispostaLettore5);
        Domanda domandaPrl3 = new Domanda("Puoi eliminare tuoi generi selezionandoli e cliccando il menu e salvare la scelta",Categoria.PREFERENZA_GENERE,rispostaLettore5);

        Domanda domandaEPrl1 = new Domanda("All'interno dell'area Utente nella sezione gestione genere puoi visualizzare i tuoi generi", Categoria.PREFERENZA_GENERE,rispostaEsperto5);
        Domanda domandaEPrl2 = new Domanda("Puoi aggiungere i tuoi generi selezionandoli e cliccando il pulsante verde e salvarli",Categoria.PREFERENZA_GENERE,rispostaEsperto5);
        Domanda domandaEPrl3 = new Domanda("Puoi eliminare tuoi generi selezionandoli e cliccando il menu e salvare la scelta",Categoria.PREFERENZA_GENERE,rispostaEsperto5);


        domandaDAO.save(domandaPrl1);
        domandaDAO.save(domandaPrl2);
        domandaDAO.save(domandaPrl3);

        domandaDAO.save(domandaEPrl1);
        domandaDAO.save(domandaEPrl2);
        domandaDAO.save(domandaEPrl3);


        out.info("*************************** RispostaBot create 5/5 ***************************");

        Domanda domandaBlog1 = new Domanda("All'interno del blog puoi visualizzare le recensioni", Categoria.GESTIONE_BLOG,rispostaLettore4);
        Domanda domandaBlog2 = new Domanda("Selezionando una recensione puoi commentare scrivendo nel form in basso e avviare il confronto",Categoria.GESTIONE_BLOG,rispostaLettore4);
        Domanda domandaBlog3 = new Domanda("Puoi rispondere ai vari commenti per confrontarti con altri utenti",Categoria.GESTIONE_BLOG,rispostaLettore4);

        Domanda domandaEBlog1 = new Domanda("All'interno del blog puoi visualizzare,modficare o eliminare le recensioni cliccando l'apposito pulsante", Categoria.GESTIONE_BLOG,rispostaEsperto4);
        Domanda domandaEBlog2 = new Domanda("All'interno del blog puoi creare una nuova recensione cliccando l'apposito pulsante", Categoria.GESTIONE_BLOG,rispostaEsperto4);
        Domanda domandaEBlog3 = new Domanda("Selezionando una recensione puoi commentare ed eliminare ogni commento per moderare",Categoria.GESTIONE_BLOG,rispostaEsperto4);
        Domanda domandaEBlog4 = new Domanda("Puoi rispondere ai vari commenti per confrontarti con altri utenti oppure eliminare le varie risposte per moderare",Categoria.GESTIONE_BLOG,rispostaEsperto4);


        domandaDAO.save(domandaBlog1);
        domandaDAO.save(domandaBlog2);
        domandaDAO.save(domandaBlog3);


        domandaDAO.save(domandaEBlog1);
        domandaDAO.save(domandaEBlog2);
        domandaDAO.save(domandaEBlog3);
        domandaDAO.save(domandaEBlog4);

        Domanda domandaEsp1 = new Domanda("Puoi visualizzare gli esperti e contattarli premendo contatta!",Categoria.SEZIONE_ESPERTO,rispostaLettore6);
        domandaDAO.save(domandaEsp1);
        //-------------------------------Questionario-------------------------------------------------



        Risposta questionario1 = new Risposta("Quale tipo di avventura preferisci vivere attraverso una storia?",
                Categoria.QUESTIONARIO,chatBLettore);

        Risposta questionario2 = new Risposta(" Qual è l’emozione che ti piace provare leggendo una storia?",
                Categoria.QUESTIONARIO,chatBLettore);

        Risposta questionario3 = new Risposta("Qual è il tuo scenario ideale per una storia?",
                Categoria.QUESTIONARIO,chatBLettore);

        Risposta questionario4 = new Risposta("Quale tema ti interessa di più?",
                Categoria.QUESTIONARIO,chatBLettore);

        Risposta questionario5 = new Risposta("Quale personaggio principale ti ispira di più?",
                Categoria.QUESTIONARIO,chatBLettore);


        rispostaDAO.save(questionario1);
        rispostaDAO.save(questionario2);
        rispostaDAO.save(questionario3);
        rispostaDAO.save(questionario4);
        rispostaDAO.save(questionario5);


        Domanda risposta1Q1 = new Domanda("Viaggi in terre misteriose e lontane",Categoria.QUESTIONARIO,questionario1);
        Domanda risposta2Q1 = new Domanda("Esplorazione dello spazio e mondi futuristici",Categoria.QUESTIONARIO,questionario1);
        Domanda risposta3Q1 = new Domanda("Indagini e misteri da risolvere ",Categoria.QUESTIONARIO,questionario1);
        Domanda risposta4Q1 = new Domanda("Un’immersione nei sentimenti e nei legami umani (→ ",Categoria.QUESTIONARIO,questionario1);
        Domanda risposta5Q1 = new Domanda("Conflitti intensi e azione adrenalinica",Categoria.QUESTIONARIO,questionario1);

        Domanda risposta1Q2 = new Domanda("Suspense e brividi ",Categoria.QUESTIONARIO,questionario2);
        Domanda risposta2Q2 = new Domanda("Stupore e meraviglia per mondi immaginari ",Categoria.QUESTIONARIO,questionario2);
        Domanda risposta3Q2 = new Domanda("Divertimento e leggerezza ",Categoria.QUESTIONARIO,questionario2);
        Domanda risposta4Q2 = new Domanda("Riflessione e ispirazione su temi profondi",Categoria.QUESTIONARIO,questionario2);
        Domanda risposta5Q2 = new Domanda("Nostalgia e romanticismo ",Categoria.QUESTIONARIO,questionario2);

        Domanda risposta1Q3 = new Domanda("Un’epoca passata, ricca di eventi storici ",Categoria.QUESTIONARIO,questionario3);
        Domanda risposta2Q3 = new Domanda("Un mondo distopico o tecnologico ",Categoria.QUESTIONARIO,questionario3);
        Domanda risposta3Q3 = new Domanda("Una tranquilla cittadina con misteri da risolvere ",Categoria.QUESTIONARIO,questionario3);
        Domanda risposta4Q3 = new Domanda("Un’avventura magica o fiabesca",Categoria.QUESTIONARIO,questionario3);
        Domanda risposta5Q3 = new Domanda("Una rappresentazione realistica della vita moderna ",Categoria.QUESTIONARIO,questionario3);

        Domanda risposta1Q4 = new Domanda("Lotte di potere e conflitti sociali",Categoria.QUESTIONARIO,questionario4);
        Domanda risposta2Q4 = new Domanda("Scoperte scientifiche e innovazione tecnologica",Categoria.QUESTIONARIO,questionario4);
        Domanda risposta3Q4 = new Domanda("Emozioni forti e storie d’amore",Categoria.QUESTIONARIO,questionario4);
        Domanda risposta4Q4 = new Domanda("Commedia e situazioni divertenti",Categoria.QUESTIONARIO,questionario4);
        Domanda risposta5Q4 = new Domanda("Racconti surreali e fantastici",Categoria.QUESTIONARIO,questionario4);

        Domanda risposta1Q5 = new Domanda("Un detective o un investigatore",Categoria.QUESTIONARIO,questionario5);
        Domanda risposta2Q5 = new Domanda("Un eroe che combatte contro il male",Categoria.QUESTIONARIO,questionario5);
        Domanda risposta3Q5 = new Domanda("Un visionario o scienziato che esplora nuovi orizzonti",Categoria.QUESTIONARIO,questionario5);
        Domanda risposta4Q5 = new Domanda("Una persona comune che affronta grandi sfide emotive",Categoria.QUESTIONARIO,questionario5);
        Domanda risposta5Q5 = new Domanda("Un protagonista divertente e spensierato",Categoria.QUESTIONARIO,questionario5);

        domandaDAO.save(risposta1Q1);
        domandaDAO.save(risposta2Q1);
        domandaDAO.save(risposta3Q1);
        domandaDAO.save(risposta4Q1);
        domandaDAO.save(risposta5Q1);

        domandaDAO.save(risposta1Q2);
        domandaDAO.save(risposta2Q2);
        domandaDAO.save(risposta3Q2);
        domandaDAO.save(risposta4Q2);
        domandaDAO.save(risposta5Q2);

        domandaDAO.save(risposta1Q3);
        domandaDAO.save(risposta2Q3);
        domandaDAO.save(risposta3Q3);
        domandaDAO.save(risposta4Q3);
        domandaDAO.save(risposta5Q3);

        domandaDAO.save(risposta1Q4);
        domandaDAO.save(risposta2Q4);
        domandaDAO.save(risposta3Q4);
        domandaDAO.save(risposta4Q4);
        domandaDAO.save(risposta5Q4);

        domandaDAO.save(risposta1Q5);
        domandaDAO.save(risposta2Q5);
        domandaDAO.save(risposta3Q5);
        domandaDAO.save(risposta4Q5);
        domandaDAO.save(risposta5Q5);



//-------------------------------POPOLAMENTO MANY TO MANY E ONE TO MANY-------------------------------------------------


//-------------------Associo ai lettori un club del libro---------------------------------------------------------------

        lettore.setClubs(Arrays.asList(clubDelLibro1, clubDelLibro2, clubDelLibro3, clubDelLibro4));
        lettore1.setClubs(Arrays.asList(clubDelLibro5, clubDelLibro6, clubDelLibro7, clubDelLibro8));
        lettore2.setClubs(Arrays.asList(clubDelLibro9, clubDelLibro10));
        lettore3.setClubs(Arrays.asList(clubDelLibro1, clubDelLibro6, clubDelLibro9));
        lettore4.setClubs(Arrays.asList(clubDelLibro5, clubDelLibro2, clubDelLibro3, clubDelLibro8));
        lettore5.setClubs(Arrays.asList(clubDelLibro9, clubDelLibro10));
        lettore6.setClubs(Arrays.asList(clubDelLibro1, clubDelLibro6, clubDelLibro9));
        lettore7.setClubs(Arrays.asList(clubDelLibro3, clubDelLibro9, clubDelLibro5, clubDelLibro8));
        lettore8.setClubs(Arrays.asList(clubDelLibro9, clubDelLibro4));
        lettore9.setClubs(Arrays.asList(clubDelLibro1, clubDelLibro7, clubDelLibro9));
        lettore10.setClubs(Arrays.asList(clubDelLibro5, clubDelLibro2, clubDelLibro1, clubDelLibro10));


        lettoreDAO.save(lettore);
        lettoreDAO.save(lettore1);
        lettoreDAO.save(lettore2);
        lettoreDAO.save(lettore3);
        lettoreDAO.save(lettore4);
        lettoreDAO.save(lettore5);
        lettoreDAO.save(lettore6);
        lettoreDAO.save(lettore7);
        lettoreDAO.save(lettore8);
        lettoreDAO.save(lettore9);
        lettoreDAO.save(lettore10);


//------------------Associo dei ticket alle biblioteche-----------------------------------------------------------------

        biblioteca.setTickets(Arrays.asList(ticket, ticket1, ticket2));
        biblioteca1.setTickets(Arrays.asList(ticket3, ticket4, ticket5));
        biblioteca2.setTickets(Arrays.asList(ticket6, ticket7, ticket8));

        bibliotecaDAO.save(biblioteca);
        bibliotecaDAO.save(biblioteca1);
        bibliotecaDAO.save(biblioteca2);

//-----------------Associo degli eventi ai club del libro---------------------------------------------------------------

        clubDelLibro1.setEventi(Arrays.asList(evento));
        clubDelLibro2.setEventi(Arrays.asList(evento2, evento11));
        clubDelLibro3.setEventi(Arrays.asList(evento3));
        clubDelLibro4.setEventi(Arrays.asList(evento4));
        clubDelLibro5.setEventi(Arrays.asList(evento5));
        clubDelLibro6.setEventi(Arrays.asList(evento6, evento12));
        clubDelLibro7.setEventi(Arrays.asList(evento8));
        clubDelLibro8.setEventi(Arrays.asList(evento7));
        clubDelLibro9.setEventi(Arrays.asList(evento9, evento13));
        clubDelLibro10.setEventi(Arrays.asList(evento10));

        clubDelLibroDAO.save(clubDelLibro1);
        clubDelLibroDAO.save(clubDelLibro2);
        clubDelLibroDAO.save(clubDelLibro3);
        clubDelLibroDAO.save(clubDelLibro4);
        clubDelLibroDAO.save(clubDelLibro5);
        clubDelLibroDAO.save(clubDelLibro6);
        clubDelLibroDAO.save(clubDelLibro7);
        clubDelLibroDAO.save(clubDelLibro8);
        clubDelLibroDAO.save(clubDelLibro9);
        clubDelLibroDAO.save(clubDelLibro10);

//------------------Associo dei generi ai club del libro----------------------------------------------------------------

        clubDelLibro1.setGeneri(Arrays.asList(fantasy, fantascienza, romanzo));
        clubDelLibro2.setGeneri(Arrays.asList(storico, saggio, biografico));
        clubDelLibro3.setGeneri(Arrays.asList(tecnologia, saggio, scientifico));
        clubDelLibro4.setGeneri(Arrays.asList(fiabefavole, ragazzi, narrativa, romanzo));
        clubDelLibro5.setGeneri(Arrays.asList(azione, avventura, narrativa));
        clubDelLibro6.setGeneri(Arrays.asList(giallo, thriller, romanzo));
        clubDelLibro7.setGeneri(Arrays.asList(psicologico, scientifico, tecnologia));
        clubDelLibro8.setGeneri(Arrays.asList(romantico, romanzo));
        clubDelLibro9.setGeneri(Arrays.asList(noir, thriller));
        clubDelLibro10.setGeneri(Arrays.asList(horror, thriller));

        clubDelLibroDAO.save(clubDelLibro1);
        clubDelLibroDAO.save(clubDelLibro2);
        clubDelLibroDAO.save(clubDelLibro3);
        clubDelLibroDAO.save(clubDelLibro4);
        clubDelLibroDAO.save(clubDelLibro5);
        clubDelLibroDAO.save(clubDelLibro6);
        clubDelLibroDAO.save(clubDelLibro7);
        clubDelLibroDAO.save(clubDelLibro8);
        clubDelLibroDAO.save(clubDelLibro9);
        clubDelLibroDAO.save(clubDelLibro10);

//------------------Associo degli esperti ai club del libro-------------------------------------------------------------

        esperto.setClubs(Arrays.asList(clubDelLibro1));
        esperto1.setClubs(Arrays.asList(clubDelLibro2));
        esperto3.setClubs(Arrays.asList(clubDelLibro3));
        esperto5.setClubs(Arrays.asList(clubDelLibro4));
        esperto8.setClubs(Arrays.asList(clubDelLibro5));
        esperto11.setClubs(Arrays.asList(clubDelLibro6));
        esperto13.setClubs(Arrays.asList(clubDelLibro7));
        esperto15.setClubs(Arrays.asList(clubDelLibro9));
        esperto17.setClubs(Arrays.asList(clubDelLibro10));
        esperto20.setClubs(Arrays.asList(clubDelLibro8));

        espertoDAO.save(esperto);
        espertoDAO.save(esperto1);
        espertoDAO.save(esperto3);
        espertoDAO.save(esperto5);
        espertoDAO.save(esperto8);
        espertoDAO.save(esperto11);
        espertoDAO.save(esperto13);
        espertoDAO.save(esperto15);
        espertoDAO.save(esperto17);
        espertoDAO.save(esperto20);

//-----------------Associo dei generi agli esperti----------------------------------------------------------------------

        esperto.setGeneri(Arrays.asList(fantasy,fantascienza));
        esperto1.setGeneri(Arrays.asList(politico,storico));
        esperto2.setGeneri(Arrays.asList(romantico, avventura));
        esperto3.setGeneri(Arrays.asList(tecnologia, scientifico));
        esperto4.setGeneri(Arrays.asList(noir, thriller, horror));
        esperto5.setGeneri(Arrays.asList(fiabefavole, fantasy, ragazzi));
        esperto6.setGeneri(Arrays.asList(storico, scientifico, saggio));
        esperto7.setGeneri(Arrays.asList(narrativa, romanzo, azione));
        esperto8.setGeneri(Arrays.asList(azione, avventura, fantascienza));
        esperto9.setGeneri(Arrays.asList(storico, scientifico, tecnologia));
        esperto10.setGeneri(Arrays.asList(space,scientifico));
        esperto11.setGeneri(Arrays.asList(giallo, thriller, narrativa));
        esperto12.setGeneri(Arrays.asList(romantico, romanzo, ragazzi));
        esperto13.setGeneri(Arrays.asList(psicologico, scientifico));
        esperto14.setGeneri(Arrays.asList(fantascienza, comico, saggio));
        esperto15.setGeneri(Arrays.asList(noir, thriller));
        esperto16.setGeneri(Arrays.asList(ragazzi, politico, horror));
        esperto17.setGeneri(Arrays.asList(horror, narrativa, romanzo));
        esperto18.setGeneri(Arrays.asList(formazione, biografico));
        esperto19.setGeneri(Arrays.asList(fantascienza, fantasy, romantico));
        esperto20.setGeneri(Arrays.asList(romantico, romanzo));

        espertoDAO.save(esperto);
        espertoDAO.save(esperto1);
        espertoDAO.save(esperto2);
        espertoDAO.save(esperto3);
        espertoDAO.save(esperto4);
        espertoDAO.save(esperto5);
        espertoDAO.save(esperto6);
        espertoDAO.save(esperto7);
        espertoDAO.save(esperto8);
        espertoDAO.save(esperto9);
        espertoDAO.save(esperto10);
        espertoDAO.save(esperto11);
        espertoDAO.save(esperto12);
        espertoDAO.save(esperto13);
        espertoDAO.save(esperto14);
        espertoDAO.save(esperto15);
        espertoDAO.save(esperto16);
        espertoDAO.save(esperto17);
        espertoDAO.save(esperto18);
        espertoDAO.save(esperto19);
        espertoDAO.save(esperto20);

//-----------------Associo dei lettori agli eventi----------------------------------------------------------------------

        lettore.setEventi(Arrays.asList(evento, evento2));
        lettore1.setEventi(Arrays.asList(evento5, evento6));
        lettore2.setEventi(Arrays.asList(evento13));

        lettoreDAO.save(lettore);
        lettoreDAO.save(lettore1);
        lettoreDAO.save(lettore2);

//-----------------Associo dei generi ai lettori------------------------------------------------------------------------

        lettore.setGeneri(Arrays.asList(noir, politico));
        lettore1.setGeneri(Arrays.asList(ragazzi, avventura));
        lettore2.setGeneri(Arrays.asList(storico, saggio));
        lettore3.setGeneri(Arrays.asList(space, scientifico));
        lettore4.setGeneri(Arrays.asList(noir, politico));
        lettore5.setGeneri(Arrays.asList(ragazzi, avventura));
        lettore6.setGeneri(Arrays.asList(storico, saggio));
        lettore7.setGeneri(Arrays.asList(space, scientifico));
        lettore8.setGeneri(Arrays.asList(noir, politico));
        lettore9.setGeneri(Arrays.asList(ragazzi, avventura));
        lettore10.setGeneri(Arrays.asList(storico, saggio));

        lettoreDAO.save(lettore);
        lettoreDAO.save(lettore1);
        lettoreDAO.save(lettore2);
        lettoreDAO.save(lettore3);
        lettoreDAO.save(lettore4);
        lettoreDAO.save(lettore5);
        lettoreDAO.save(lettore6);
        lettoreDAO.save(lettore7);
        lettoreDAO.save(lettore8);
        lettoreDAO.save(lettore9);
        lettoreDAO.save(lettore10);


//----------------Associo dei ticket ai lettori-------------------------------------------------------------------------

        lettore.setTickets(Arrays.asList(ticket));
        lettore2.setTickets(Arrays.asList(ticket1));
        lettore3.setTickets(Arrays.asList(ticket2));
        lettore4.setTickets(Arrays.asList(ticket3));
        lettore5.setTickets(Arrays.asList(ticket4));
        lettore6.setTickets(Arrays.asList(ticket5));
        lettore9.setTickets(Arrays.asList(ticket6));
        lettore10.setTickets(Arrays.asList(ticket7));
        lettore12.setTickets(Arrays.asList(ticket8));

        lettoreDAO.save(lettore);
        lettoreDAO.save(lettore1);
        lettoreDAO.save(lettore2);
        lettoreDAO.save(lettore3);

       //----------------Associo dei ticket ai lettori-------------------------------------------------------------------------

        libro2.setRecensioni(Arrays.asList(recensione3));
        libro4.setRecensioni(Arrays.asList(recensione2));
        libro1.setRecensioni(Arrays.asList(recensione1));

        libroDAO.save(libro2);
        libroDAO.save(libro4);
        libroDAO.save(libro1);

        //----------------Associo delle recensioni a dei libri-------------------------------------------------------------------------

        recensione1.setCommenti(Arrays.asList(commento1));


        recensioneDAO.save(recensione1);

        //----------------Associo dei commenti alle recensioni-------------------------------------------------------------------------

        commento1.setRisposte(Arrays.asList(risposta1, risposta2, risposta3));

        commentoDAO.save(commento1);

        //----------------Associo delle risposte al commento-------------------------------------------------------------------------


        chatBLettore.setRisposta2(Arrays.asList(rispostaLettore1,rispostaLettore2,
               rispostaLettore3,rispostaLettore4));

        chatBotDAO.save(chatBLettore);

        chatBEsperto.setRisposta2(Arrays.asList(rispostaEsperto1,rispostaEsperto2,rispostaEsperto4,rispostaEsperto5));


        //----------------Associo domande alle Domande GestioneUtente-------------------------------------------------------------------------


        rispostaLettore1.setDomande(Arrays.asList(domandaUtente1,domandaUtente2
                ,domandaUtente3));

        rispostaEsperto1.setDomande(Arrays.asList(domandaEUtente1,domandaEUtente2,
                domandaEUtente3));

        //----------------Associo delle risposte alla domandaGestioneUtenteE----------------------------------------------
        rispostaLettore2.setDomande(Arrays.asList(domandaClubL1,domandaClubL2,
                domandaClubL3,domandaClubL4,domandaClubL5,domandaClubL6));


        rispostaEsperto2.setDomande(Arrays.asList(domandaEClubL1,domandaEClubL2,domandaEClubL3,domandaEClubL4,
                domandaEClubL5,domandaEClubL6,domandaEClubL7));

        //----------------Associo delle risposte alla domandaGestioneClubLibro-----------------------------------------------

        rispostaLettore3.setDomande(Arrays.asList(domandaPre1,domandaPre2,
                domandaPre3,domandaPre4,domandaPre5));

        //----------------Associo delle risposte alla domandaPrenotazioni-------------------------------------

        rispostaLettore4.setDomande(Arrays.asList(domandaBlog1,domandaBlog2,
                domandaBlog3));

        rispostaEsperto4.setDomande(Arrays.asList(domandaEBlog1,domandaEBlog2,domandaBlog3));

        //----------------Associo delle risposte alla domandaBlog-------------------------------------

        rispostaLettore5.setDomande(Arrays.asList(domandaPrl1,domandaPrl2,domandaPrl3));

        rispostaEsperto5.setDomande(Arrays.asList(domandaPrl1,domandaPrl2,domandaPrl3));

        rispostaDAO.save(rispostaLettore1);
        rispostaDAO.save(rispostaLettore2);
        rispostaDAO.save(rispostaLettore3);
        rispostaDAO.save(rispostaLettore4);
        rispostaDAO.save(rispostaLettore5);

        rispostaDAO.save(rispostaEsperto1);
        rispostaDAO.save(rispostaEsperto2);
        //domandaDAO.save(domandaEsperto3);
        rispostaDAO.save(rispostaEsperto4);
        rispostaDAO.save(rispostaEsperto5);


        questionario1.setDomande(Arrays.asList(risposta1Q1,risposta2Q1,
                risposta3Q1,risposta4Q1,risposta5Q1));

        questionario2.setDomande(Arrays.asList(risposta1Q2,risposta2Q2,
                risposta3Q2,risposta4Q2,risposta5Q2));

        questionario3.setDomande(Arrays.asList(risposta1Q3,risposta2Q3,
                risposta3Q3,risposta4Q3,risposta5Q3));

        questionario4.setDomande(Arrays.asList(risposta1Q4,risposta2Q4,
                risposta3Q4,risposta4Q4,risposta5Q4));

        questionario5.setDomande(Arrays.asList(risposta1Q5,risposta2Q5,
                risposta3Q5,risposta4Q5,risposta5Q5));


        chatBLettore.setRisposta1(Arrays.asList(questionario1,questionario2,
                questionario3,questionario4,questionario5));

        chatBotDAO.save(chatBLettore);

        rispostaDAO.save(questionario1);
        rispostaDAO.save(questionario2);
        rispostaDAO.save(questionario3);
        rispostaDAO.save(questionario4);
        rispostaDAO.save(questionario5);



        out.info("*************************** INIZIALIZZAZIONE DI BIBLIONET TERMINATA ***************************");

    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(BiblionetApplication.class, args);

        init(configurableApplicationContext);
    }

    public static String getCopertinaClubFromUrl(String filePath) {
        try{
            byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
            return Base64.getEncoder().encodeToString(fileContent);
        }
        catch (IOException ex){
            ex.printStackTrace();
            return "noimage";
        }

    }
}
