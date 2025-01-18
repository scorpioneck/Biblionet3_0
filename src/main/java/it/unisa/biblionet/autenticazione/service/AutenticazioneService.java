package it.unisa.biblionet.autenticazione.service;


import it.unisa.biblionet.model.entity.ClubDelLibro;
import it.unisa.biblionet.model.entity.utente.Biblioteca;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.entity.utente.Lettore;
import it.unisa.biblionet.model.entity.utente.UtenteRegistrato;

import java.util.List;


public interface AutenticazioneService {
     /**
      * Firma del metodo che implementa la funzione di login.
      * @param email dell'utente da loggare.
      * @param password dell'utente da loggare.
      * @return dell'utente da loggato.
      */
     UtenteRegistrato login(String email, String password);

     /**
      * Firma del metodo che implementa l'identificazione di un lettore.
      * @param utente registrato che si trova in sessione.
      * @return true se l'utente è un lettore altrimenti false.
      */
     boolean isLettore(UtenteRegistrato utente);

     /**
      * Firma del metodo che implementa l'identificazione di un esperto.
      * @param utente registrato che si trova in sessione.
      * @return true se l'utente è un esperto altrimenti false.
      */
     boolean isEsperto(UtenteRegistrato utente);

     /**
      * Firma del metodo che implementa l'identificazione di una biblioteca.
      * @param utente registrato che si trova in sessione.
      * @return true se l'utente è una biblioteca altrimenti false.
      */
     boolean isBiblioteca(UtenteRegistrato utente);

     /**
      * Implementa la funzionalità di salvataggio delle modifiche
      * all'account biblioteca.
      * @param utente La biblioteca da aggiornare
      * @return la biblioteca aggiornata
      */
     Biblioteca aggiornaBiblioteca(Biblioteca utente);

     /**
      * Implementa la funzionalità di salvataggio delle modifiche
      * all'account esperto.
      * @param utente L'esperto da aggiornare
      * @return l'esperto aggiornato
      */
     Esperto aggiornaEsperto(Esperto utente);

     /**
      * Implementa la funzionalità di salvataggio delle modifiche
      * all'account lettore.
      * @param utente Lettore da aggiornare
      * @return il lettore aggiornato
      */
     Lettore aggiornaLettore(Lettore utente);


     /**
      * Implementa la funzionalità di trovare un esperto.
      * @param email La mail dell esperto
      * @return L'esperto se c'è, altrimenti null
      */
     Esperto findEspertoByEmail(String email);

     /**
      * Implementa la funzionalità di trovare un lettore.
      * @param email La mail dell lettore
      * @return Il lettore se c'è, altrimenti null
      */
     Lettore findLettoreByEmail(String email);

     /**
      * Implementa la funzionalità di trovare una biblioteca.
      * @param email La mail della biblioteca
      * @return La biblioteca se c'è, altrimenti null
      */
     Biblioteca findBibliotecaByEmail(String email);

     /**
      * Implementa la funzionalità di prendere una lista di club
      * del libro a cui un lettore partecipa.
      * @param lettore il lettore preso in esame
      * @return la lista dei club del libro a cui partecipa
      */
     List<ClubDelLibro> findAllByLettori(Lettore lettore);

     /**
      * Implementa la funzionalità di prendere una lista di club
      * del libro di cui un esperto è proprietario.
      * @param esperto l' esperto preso in esame
      * @return la lista dei club del libro a cui partecipa
      */
     List<ClubDelLibro> findAllByEsperto(Esperto esperto);
}
