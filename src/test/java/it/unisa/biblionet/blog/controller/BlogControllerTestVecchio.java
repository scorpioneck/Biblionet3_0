package it.unisa.biblionet.blog.controller;

/*@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTestVecchio {

    @MockBean
    private BlogService blogService;

    @Autowired
    private MockMvc mockMvc;


    @ParameterizedTest
    @MethodSource({"provideBlog"})
    public void visualizzaRecensioni(final List<Recensione> listaRecensioni, Esperto esperto1) throws Exception {
        when(blogService.visualizzaRecensioni()).thenReturn(listaRecensioni);

        // Simula la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.get("/blog") // GET perché è una visualizzazione
                        .sessionAttr("loggedUser", esperto1)) // Simula l'utente loggato
                .andExpect(status().isOk()) // Verifica che la richiesta abbia successo
                .andExpect(model().attribute("loggedUser", esperto1)) // Controlla l'attributo dell'utente loggato
                .andExpect(model().attribute("recensioni", listaRecensioni)) // Controlla la lista delle recensioni nel modello
                .andExpect(view().name("blog/visualizza-recensioni")); // Verifica che la vista restituita sia corretta
    }

   @ParameterizedTest
   @MethodSource({"provideBlog"})
    void visualizzaRecensione(final List<Recensione> listaRecensioni,final Esperto esperto1) throws Exception {
      // Estrai la recensione dal mock
       Recensione recensione = listaRecensioni.get(0);

       // Configura i mock per il servizio
       when(blogService.trovaRecensioneById(recensione.getId())).thenReturn(recensione);
       when(blogService.visualizzaRisposte()).thenReturn(new ArrayList<>()); // Lista vuota di CommentoRisposta

       // Simula la richiesta HTTP
       this.mockMvc.perform(MockMvcRequestBuilders.get("/blog/" + recensione.getId() + "/visualizzaRecensione")
                       .sessionAttr("loggedUser", esperto1)) // Simula l'utente loggato
               .andExpect(status().isOk()) // Verifica che la richiesta abbia successo
               .andExpect(model().attributeExists("commentoForm")) // Verifica l'esistenza del commentoForm nel modello
               .andExpect(model().attributeExists("recensione")) // Verifica l'esistenza della recensione nel modello
               .andExpect(model().attribute("recensione", recensione)) // Controlla che la recensione sia corretta
               .andExpect(model().attributeExists("commento")) // Verifica l'esistenza del commento
               .andExpect(model().attributeExists("commentoRisposta")) // Verifica l'esistenza del commentoRisposta
               .andExpect(model().attributeExists("commentoRispostaList")) // Verifica l'esistenza della lista di commenti risposta
               .andExpect(model().attribute("commentoRispostaList", new ArrayList<>())) // Controlla che la lista sia vuota
               .andExpect(view().name("blog/visualizza-singola-rec")); // Verifica che la vista restituita sia corretta
    }

    @ParameterizedTest
    @MethodSource({"provideEsperti"})
    void inizializzacreaRecensione(UtenteRegistrato esperto) throws Exception {

        // Simula una lista vuota di libri
        List<Libro> libriVuoti = new ArrayList<>();
        when(blogService.findAllLibri()).thenReturn(libriVuoti);

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.get("/blog/inizializzaCreaR")
                        .sessionAttr("loggedUser", esperto))
                .andExpect(status().isOk()) // Verifica che la richiesta abbia successo
                .andExpect(model().attributeExists("recensione")) // Verifica che il modello contenga l'attributo "recensione"
                .andExpect(model().attributeExists("listaLibri")) // Verifica che il modello contenga l'attributo "listaLibri"
                .andExpect(view().name("blog/inserimento-recensione")); // Verifica la vista restituita

        // Verifica che il mock sia stato chiamato
        verify(blogService).findAllLibri();
    }

    @Test
    void testInizializzacreaRecensione_Unauthorized() throws Exception {
        // Simula un utente non loggato
        this.mockMvc.perform(MockMvcRequestBuilders.get("/blog/inizializzaCreaR"))
                .andExpect(status().isUnauthorized()); // Verifica che lo stato sia UNAUTHORIZED
    }

    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testCreaRecensione_Success(UtenteRegistrato esperto) throws Exception {
        // Mock del libro trovato
        int idLibro = 1;
        Libro libro = new Libro("Titolo Libro", "Autore", "ISBN12345", LocalDateTime.now(), "Editore", "Descrizione");
        when(blogService.findLibroById(idLibro)).thenReturn(libro);

        // Mock della recensione
        RecensioneForm recensioneForm = new RecensioneForm();
        recensioneForm.setTitolo("Titolo Recensione");
        recensioneForm.setDescrizione("Descrizione Recensione");

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/creaRecensione")
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(idLibro))
                        .flashAttr("recensioneForm", recensioneForm))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog")); // Verifica l'URL del redirect

        // Verifica che il libro sia stato associato e la recensione creata
        verify(blogService).findLibroById(idLibro);
        verify(blogService).creaRecensione(argThat(recensione ->
                recensione.getTitolo().equals(recensioneForm.getTitolo()) &&
                        recensione.getDescrizione().equals(recensioneForm.getDescrizione()) &&
                        recensione.getEsperto().equals(esperto) &&
                        recensione.getLibro().equals(libro)
        ));
    }

    @Test
    void testCreaRecensione_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/creaRecensione")
                        .param("idLibro", "1"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }


    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testInizializzaModificaRecensione_Success(UtenteRegistrato esperto) throws Exception {
        // Mock della lista di libri
        List<Libro> libri = Arrays.asList(
                new Libro("Titolo 1", "Autore 1", "ISBN1", LocalDateTime.now(), "Editore 1", "Descrizione 1"),
                new Libro("Titolo 2", "Autore 2", "ISBN2", LocalDateTime.now(), "Editore 2", "Descrizione 2")
        );
        when(blogService.findAllLibri()).thenReturn(libri);

        // Mock del form della recensione
        RecensioneForm recensioneForm = new RecensioneForm();
        recensioneForm.setTitolo("Titolo Modifica");
        recensioneForm.setDescrizione("Descrizione Modifica");

        // ID della recensione da modificare
        int idRecensione = 1;

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.get("/blog/{id}/inizializzaModificaR", idRecensione)
                        .sessionAttr("loggedUser", esperto)
                        .flashAttr("recensioneForm", recensioneForm))
                .andExpect(status().isOk()) // Verifica che la richiesta abbia successo
                .andExpect(model().attributeExists("recensione")) // Verifica che il modello contenga "recensione"
                .andExpect(model().attribute("recensione", recensioneForm)) // Verifica il contenuto di "recensione"
                .andExpect(model().attribute("id", idRecensione)) // Verifica l'attributo "id"
                .andExpect(model().attribute("listaLibri", libri)) // Verifica l'attributo "listaLibri"
                .andExpect(view().name("blog/modifica-recensione")); // Verifica la vista restituita

        // Verifica che il servizio sia stato chiamato
        verify(blogService).findAllLibri();
    }

    @Test
    void testInizializzaModificaRecensione_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.get("/blog/{id}/inizializzaModificaR", 1))
                .andExpect(status().isUnauthorized()); // Verifica che lo stato sia UNAUTHORIZED
    }


    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testGestisciRecensione_Success(UtenteRegistrato esperto) throws Exception {
        // ID della recensione e del libro
        int idRecensione = 1;
        int idLibro = 2;

        // Mock della recensione trovata
        Recensione recensione = new Recensione();
        recensione.setId(idRecensione);
        recensione.setTitolo("Titolo Originale");
        recensione.setDescrizione("Descrizione Originale");

        // Mock del libro trovato
        Libro libro = new Libro("Titolo Libro", "Autore", "ISBN12345", LocalDateTime.now(), "Editore", "Descrizione");

        // Mock del form della recensione
        RecensioneForm recensioneForm = new RecensioneForm();
        recensioneForm.setTitolo("Titolo Aggiornato");
        recensioneForm.setDescrizione("Descrizione Aggiornata");

        // Configura i mock
        when(blogService.trovaRecensioneById(idRecensione)).thenReturn(recensione);
        when(blogService.findLibroById(idLibro)).thenReturn(libro);

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaRecensione", idRecensione)
                        .sessionAttr("loggedUser", esperto)
                        .param("idLibro", String.valueOf(idLibro))
                        .flashAttr("recensioneForm", recensioneForm))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog")); // Verifica l'URL del redirect

        // Verifica che la recensione sia stata aggiornata
        verify(blogService).trovaRecensioneById(idRecensione);
        verify(blogService).findLibroById(idLibro);
        verify(blogService).modificaRecensione(argThat(updatedRecensione ->
                updatedRecensione.getTitolo().equals(recensioneForm.getTitolo()) &&
                        updatedRecensione.getDescrizione().equals(recensioneForm.getDescrizione()) &&
                        updatedRecensione.getLibro().equals(libro)
        ));
    }

    @Test
    void testGestisciRecensione_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaRecensione", 1)
                        .param("idLibro", "2"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }


   /* @ParameterizedTest
    @MethodSource("provideUtenti")
    void testGestisciCommento_Success(UtenteRegistrato utente) throws Exception {
        // ID della recensione e del commento
        int idRecensione = 1;
        int idCommento = 2;

        // Mock della recensione trovata
        Recensione recensione = new Recensione();
        recensione.setId(idRecensione);
        recensione.setTitolo("Titolo Recensione");
        recensione.setDescrizione("Descrizione Recensione");

        // Mock del form per la modifica del commento
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setTitle("ProvaStringa");
        commentoForm.setDescription("Stringa1000");

        Commento commento = new Commento();
        commento.setId(idCommento);
        commento.setRecensione(recensione);
        commento.setTitle(commentoForm.getTitle());
        commento.setDescription(commentoForm.getDescription());
        commento.setIdUtente(utente.getEmail());

        // Configura i mock
        when(blogService.trovaRecensione(idRecensione)).thenReturn(recensione);
        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.of(commento));

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommento", idRecensione)
                        .sessionAttr("loggedUser", utente)
                        .param("commentoId", String.valueOf(idCommento)) // Passa il commentoId
                        .param("title", commentoForm.getTitle()) // Passa il titolo aggiornato
                        .param("description", commentoForm.getDescription())) // Passa la descrizione aggiornata
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect
    }


    @Test
    void testGestisciCommento_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciCommento", 1)
                        .param("title", "Titolo Commento")
                        .param("description", "Descrizione Commento"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }


    @ParameterizedTest
    @MethodSource("provideUtenti")
    void testGestisciCommentoRisposta_Success(UtenteRegistrato utente) throws Exception {
        // ID della recensione e del commento padre
        int idRecensione = 1;
        int commentoPadreId = 2;

        // Mock della recensione trovata
        Recensione recensione = new Recensione();
        recensione.setId(idRecensione);
        recensione.setTitolo("Titolo Recensione");
        recensione.setDescrizione("Descrizione Recensione");

        // Mock del commento padre trovato
        Commento commentoPadre = new Commento();
        commentoPadre.setId(commentoPadreId);
        commentoPadre.setTitle("Titolo Commento Padre");
        commentoPadre.setDescription("Descrizione Commento Padre");

        // Mock del form della risposta
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setTitle("Titolo Risposta");
        commentoForm.setDescription("Descrizione Risposta");

        // Configura i mock
        when(blogService.trovaRecensione(idRecensione)).thenReturn(recensione);
        when(blogService.trovaCommentoById(commentoPadreId)).thenReturn(Optional.of(commentoPadre));

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciCommentoRisposta", idRecensione)
                        .sessionAttr("loggedUser", utente)
                        .param("commentoPadreId", String.valueOf(commentoPadreId))
                        .flashAttr("commentoForm", commentoForm))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che la risposta sia stata creata
        verify(blogService).rispostaCommento(argThat(risposta ->
                risposta.getTitle().equals(commentoForm.getTitle()) &&
                        risposta.getDescription().equals(commentoForm.getDescription()) &&
                        risposta.getIdUtente().equals(utente.getEmail()) &&
                        risposta.getCommentoPadre().equals(commentoPadre)
        ));
    }

    @Test
    void testGestisciCommentoRisposta_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/gestisciCommentoRisposta", 1)
                        .param("commentoPadreId", "2")
                        .param("title", "Titolo Risposta")
                        .param("description", "Descrizione Risposta"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }

    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testCancellaRecensione_Success(UtenteRegistrato esperto) throws Exception {
        // ID della recensione
        int idRecensione = 1;

        // Mock della recensione trovata
        Recensione recensione = new Recensione();
        recensione.setId(idRecensione);
        recensione.setTitolo("Titolo Recensione");
        recensione.setDescrizione("Descrizione Recensione");

        // Configura i mock
        when(blogService.trovaRecensione(idRecensione)).thenReturn(recensione);

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaRecensione", idRecensione)
                        .sessionAttr("loggedUser", esperto))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog")); // Verifica l'URL del redirect

        // Verifica che la recensione sia stata eliminata
        verify(blogService).trovaRecensione(idRecensione);
        verify(blogService).eliminaRecensione(recensione);
    }

    @Test
    void testCancellaRecensione_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaRecensione", 1))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }

    @Test
    void testCancellaRecensione_NotFound() throws Exception {
        // ID della recensione
        int idRecensione = 1;

        // Configura il mock per simulare una recensione non trovata
        when(blogService.trovaRecensione(idRecensione)).thenReturn(null);

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaRecensione", idRecensione)
                        .sessionAttr("loggedUser", new Esperto()))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog")); // Verifica l'URL del redirect

        // Verifica che il metodo di eliminazione non venga chiamato
        verify(blogService, never()).eliminaRecensione(any(Recensione.class));
    }


    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testCancellaCommenti_Success(UtenteRegistrato esperto) throws Exception {
        int idRecensione = 1;
        int idCommento = 2;

        Commento commento = new Commento();
        commento.setId(idCommento);
        commento.setTitle("Titolo Commento");
        commento.setDescription("Descrizione Commento");
        commento.setIdUtente(esperto.getEmail());

        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.of(commento));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommento", idRecensione)
                        .sessionAttr("loggedUser", esperto)
                        .param("idCommento", String.valueOf(idCommento)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione"));

        verify(blogService).trovaCommentoById(idCommento);
        verify(blogService).eliminaCommento(commento);
    }

    @Test
    void testCancellaCommenti_Unauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommento", 1)
                        .param("idCommento", "2"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource({"provideEsperti"})
    void testCancellaCommenti_CommentoNonTrovato(UtenteRegistrato esperto) throws Exception {
        int idRecensione = 1;
        int idCommento = 2;

        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.empty());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommento", idRecensione)
                        .sessionAttr("loggedUser", esperto)
                .param("idCommento", String.valueOf(idCommento)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione"));

        verify(blogService, never()).eliminaCommento(any(Commento.class));
    }

    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testCancellaCommentiRisposta_Success(UtenteRegistrato esperto) throws Exception {
        // ID della recensione e della risposta al commento
        int idRecensione = 1;
        int idCommentoRisposta = 2;

        // Mock della risposta trovata
        CommentoRisposta risposta = new CommentoRisposta();
        risposta.setId(idCommentoRisposta);
        risposta.setTitle("Titolo Risposta");
        risposta.setDescription("Descrizione Risposta");
        risposta.setIdUtente(esperto.getEmail());

        // Configura i mock
        when(blogService.trovaCommentoRisposta(idCommentoRisposta)).thenReturn(Optional.of(risposta));

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommentoRisposta", idRecensione)
                        .sessionAttr("loggedUser", esperto)
                        .param("idCommentoRisposta", String.valueOf(idCommentoRisposta)))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che la risposta sia stata eliminata
        verify(blogService).trovaCommentoRisposta(idCommentoRisposta);
        verify(blogService).eliminaCommentoRisposta(risposta);
    }

    @Test
    void testCancellaCommentiRisposta_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommentoRisposta", 1)
                        .param("idCommentoRisposta", "2"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }

    @ParameterizedTest
    @MethodSource("provideEsperti")
    void testCancellaCommentiRisposta_NotFound(UtenteRegistrato esperto) throws Exception {
        // ID della recensione e della risposta al commento
        int idRecensione = 1;
        int idCommentoRisposta = 2;

        // Configura il mock per simulare una risposta non trovata
        when(blogService.trovaCommentoRisposta(idCommentoRisposta)).thenReturn(Optional.empty());

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/cancellaCommentoRisposta", idRecensione)
                        .sessionAttr("loggedUser", esperto)
                .param("idCommentoRisposta", String.valueOf(idCommentoRisposta)))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che il metodo di eliminazione non venga chiamato
        verify(blogService, never()).eliminaCommentoRisposta(any(CommentoRisposta.class));
    }


    @ParameterizedTest
    @MethodSource({"provideEsperti"})
    void testModificaCommento_Success(UtenteRegistrato utente) throws Exception {
        // ID della recensione e del commento
        int idRecensione = 1;
        int idCommento = 2;

        // Mock della recensione trovata
        Recensione recensione = new Recensione();
        recensione.setId(idRecensione);
        recensione.setTitolo("Titolo Recensione");
        recensione.setDescrizione("Descrizione Recensione");

        // Mock del commento trovato
        Commento commento = new Commento();
        commento.setId(idCommento);
        commento.setTitle("Titolo Commento");
        commento.setDescription("Descrizione Commento");
        commento.setIdUtente(utente.getEmail());

        // Mock del form per la modifica del commento
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setTitle("Nuovo Titolo");
        commentoForm.setDescription("Nuova Descrizione");

        // Configura i mock
        when(blogService.trovaRecensione(idRecensione)).thenReturn(recensione);
        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.of(commento));

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommento", idRecensione)
                        .sessionAttr("loggedUser", utente)
                        .param("commentoId", String.valueOf(idCommento)) // Passa il commentoId
                        .param("title", commentoForm.getTitle()) // Passa il titolo aggiornato
                        .param("description", commentoForm.getDescription())) // Passa la descrizione aggiornata
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che il commento sia stato modificato
        verify(blogService).trovaCommentoById(idCommento);
        verify(blogService).modificaCommento(argThat(com ->
                com.getTitle().equals(commentoForm.getTitle()) &&
                        com.getDescription().equals(commentoForm.getDescription())
        ));
    }

    @Test
    void testModificaCommento_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommento", 1)
                        .param("commentoId", "2")
                        .param("title", "Titolo Commento")
                        .param("description", "Descrizione Commento"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }

    @Test
    void testModificaCommento_CommentoNotFound() throws Exception {
        // ID della recensione e del commento
        int idRecensione = 1;
        int idCommento = 2;

        // Configura il mock per simulare che il commento non venga trovato
        when(blogService.trovaCommentoById(idCommento)).thenReturn(Optional.empty());

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommento", idRecensione)
                        .sessionAttr("loggedUser", new Esperto())
                        .param("commentoId", String.valueOf(idCommento))
                        .param("title", "Nuovo Titolo")
                        .param("description", "Nuova Descrizione"))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che il metodo di modifica non venga chiamato
        verify(blogService, never()).modificaCommento(any(Commento.class));
    }

    @ParameterizedTest
    @MethodSource("provideUtenti")
    void testModificaCommentoRisposta_Success(UtenteRegistrato utente) throws Exception {
        // ID della recensione e della risposta al commento
        int idRecensione = 1;
        int idCommentoRisposta = 2;

        // Mock della recensione trovata
        Recensione recensione = new Recensione();
        recensione.setId(idRecensione);
        recensione.setTitolo("Titolo Recensione");
        recensione.setDescrizione("Descrizione Recensione");


        // Mock della risposta trovata
        CommentoRisposta risposta = new CommentoRisposta();
        risposta.setId(idCommentoRisposta);
        risposta.setTitle("Titolo Risposta");
        risposta.setDescription("Descrizione Risposta");
        risposta.setIdUtente(utente.getEmail());

        // Mock del form per la modifica della risposta
        CommentoForm commentoForm = new CommentoForm();
        commentoForm.setTitle("Nuovo Titolo");
        commentoForm.setDescription("Nuova Descrizione");

        // Configura i mock
        when(blogService.trovaRecensione(idRecensione)).thenReturn(recensione);
        when(blogService.trovaCommentoRisposta(idCommentoRisposta)).thenReturn(Optional.of(risposta));

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommentoRisposta", idRecensione)
                        .sessionAttr("loggedUser", utente)
                        .param("rispostaId", String.valueOf(idCommentoRisposta))
                        .param("title", commentoForm.getTitle()) // Passa il titolo aggiornato
                        .param("description", commentoForm.getDescription())) // Passa la descrizione aggiornata
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che la risposta sia stata modificata
        verify(blogService).trovaCommentoRisposta(idCommentoRisposta);
        verify(blogService).modificaCommentoRisposta(argThat(com ->
                com.getTitle().equals(commentoForm.getTitle()) &&
                        com.getDescription().equals(commentoForm.getDescription())
        ));
    }

    @Test
    void testModificaCommentoRisposta_Unauthorized() throws Exception {
        // Simula una richiesta senza utente loggato
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommentoRisposta", 1)
                        .param("rispostaId", "2")
                        .param("title", "Titolo Risposta")
                        .param("description", "Descrizione Risposta"))
                .andExpect(status().isUnauthorized()); // Verifica lo stato UNAUTHORIZED
    }

    @Test
    void testModificaCommentoRisposta_RispostaNotFound() throws Exception {
        // ID della recensione e della risposta al commento
        int idRecensione = 1;
        int idCommentoRisposta = 2;

        // Configura il mock per simulare che la risposta non venga trovata
        when(blogService.trovaCommentoRisposta(idCommentoRisposta)).thenReturn(Optional.empty());

        // Esegui la richiesta HTTP
        this.mockMvc.perform(MockMvcRequestBuilders.post("/blog/{id}/modificaCommentoRisposta", idRecensione)
                        .sessionAttr("loggedUser", new Esperto())
                        .param("rispostaId", String.valueOf(idCommentoRisposta))
                        .param("title", "Nuovo Titolo")
                        .param("description", "Nuova Descrizione"))
                .andExpect(status().is3xxRedirection()) // Verifica il redirect
                .andExpect(redirectedUrl("/blog/" + idRecensione + "/visualizzaRecensione")); // Verifica l'URL del redirect

        // Verifica che il metodo di modifica non venga chiamato
        verify(blogService, never()).modificaCommentoRisposta(any(CommentoRisposta.class));
    }*/




    /*private static Stream<Arguments> provideBlog() {
        Biblioteca biblioteca = new Biblioteca("fmorlicchio@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Scafati",
                "Via Galileo Galilei 34",
                "0813223238",
                "Biblioteca Francesco Morlicchio");

        Esperto esperto1 = new Esperto(
                "ciromaiorino@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Via Roma 2",
                "2345678901",
                "ciromaiorino",
                "Ciro",
                "Maiorino",
                biblioteca
        );

        Recensione recensione1 = new Recensione();
        recensione1.setId(1);
        recensione1.setTitolo("Recensione 1");
        recensione1.setDescrizione("Testo della recensione 1");
        recensione1.setLibro(new Libro("Titolo Libro 1", "Autore Libro 1",
                "ISBN12345", LocalDateTime.now(), "Editore 1", "Descrizione Libro 1"));
        recensione1.setEsperto(esperto1);

        Recensione recensione2 = new Recensione();
        recensione2.setId(2);
        recensione2.setTitolo("Recensione 2");
        recensione2.setDescrizione("Testo della recensione 2");
        recensione2.setLibro(new Libro("Titolo Libro 2", "Autore Libro 2",
                "ISBN67890", LocalDateTime.now(), "Editore 2", "Descrizione Libro 2"));
        recensione2.setEsperto(esperto1);

        List<Recensione> listaRecensioni = Arrays.asList(recensione1, recensione2);

        return Stream.of(Arguments.of(listaRecensioni, esperto1));
    }

    private static Stream<Arguments> provideEsperti() {
        Biblioteca biblioteca = new Biblioteca("fmorlicchio@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Scafati",
                "Via Galileo Galilei 34",
                "0813223238",
                "Biblioteca Francesco Morlicchio");

        Esperto esperto = new Esperto(
                "ciromaiorino@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Via Roma 2",
                "2345678901",
                "ciromaiorino",
                "Ciro",
                "Maiorino",
                biblioteca
        );

        return Stream.of(Arguments.of(esperto));
    }

    private static Stream<Arguments> provideUtenti() {
        Biblioteca biblioteca = new Biblioteca("fmorlicchio@gmail.com",
                "BibliotecaPassword",
                "Salerno",
                "Scafati",
                "Via Galileo Galilei 34",
                "0813223238",
                "Biblioteca Francesco Morlicchio");

        Esperto esperto = new Esperto(
                "esperto@gmail.com",
                "EspertoPassword",
                "Salerno",
                "Pagani",
                "Via Roma 2",
                "2345678901",
                "esperto",
                "Ciro",
                "Maiorino",
                biblioteca
        );

        Lettore lettore = new Lettore(
                "lettore@gmail.com",
                "LettorePassword",
                "Salerno",
                "Nocera",
                "Via Dante 5",
                "3456789012",
                "lettore",
                "Mario",
                "Rossi"
        );

        return Stream.of(Arguments.of(esperto), Arguments.of(lettore));
    }

}*/