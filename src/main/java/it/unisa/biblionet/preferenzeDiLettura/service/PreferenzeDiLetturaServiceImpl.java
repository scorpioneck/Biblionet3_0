package it.unisa.biblionet.preferenzeDiLettura.service;

import it.unisa.biblionet.model.dao.GenereDAO;
import it.unisa.biblionet.model.dao.utente.EspertoDAO;
import it.unisa.biblionet.model.dao.utente.LettoreDAO;
import it.unisa.biblionet.model.entity.Genere;
import it.unisa.biblionet.model.entity.utente.Esperto;
import it.unisa.biblionet.model.entity.utente.Lettore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
@RequiredArgsConstructor
public class PreferenzeDiLetturaServiceImpl implements
        PreferenzeDiLetturaService {

    /**
     * Si occupa delle funzioni CRUD per il genere.
     */
    private final GenereDAO genereDAO;

    /**
     * Si occupa delle funzioni CRUD per l'esperto.
     */
    private final EspertoDAO espertoDAO;

    /**
     * Si occupa delle funzioni CRUD per l'utente.
     */
    private final LettoreDAO lettoreDAO;

    /**
     * Implementa la funzionalità di restituire tutti i generi
     * presenti nel database.
     * @return la lista di tutti i generi presenti nel database
     */
    @Override
    public List<Genere> getAllGeneri() {
        return genereDAO.findAll();
    }

    /**
     * Implementa la funzionalità di restituire tutti i generi
     * data una lista di nomi di generi.
     * @param generi i generi da trovare
     * @return la lista di generi contenente solamente i generi effettivamente
     * presenti nel database
     */
    @Override
    public List<Genere> getGeneriByName(final String[] generi) {
        List<Genere> toReturn = new ArrayList<>();

        for (String g: generi) {
            Genere gen = genereDAO.findByName(g);
            if (gen != null) {
                toReturn.add(gen);
            }

        }

        return toReturn;
    }

    /**
     * Implementa la funzionalità di aggiungere una lista di generi
     * ad un esperto.
     * @param generi i generi da inserire
     * @param esperto l'esperto a cui inserirli
     */
    @Override
    public void addGeneriEsperto(final List<Genere> generi,
                                 final Esperto esperto) {
        esperto.setGeneri(generi);
        espertoDAO.save(esperto);
    }

    /**
     * Implementa la funzionalità di aggiungere una lista di generi
     * ad un lettore.
     * @param generi i generi da inserire
     * @param lettore il lettore a cui inserirli
     */
    @Override
    public void addGeneriLettore(final List<Genere> generi,
                                 final Lettore lettore) {
        lettore.setGeneri(generi);
        lettoreDAO.save(lettore);
    }


}
