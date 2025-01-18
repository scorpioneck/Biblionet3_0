package it.unisa.biblionet.utils.mapper;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CategoriaGenereMapper {

    private final Map<String, String> rispostaGenereMap;

    public CategoriaGenereMapper() {
        rispostaGenereMap = new HashMap<>();

        // Associazione risposta → genere
        rispostaGenereMap.put("Viaggi in terre misteriose e lontane", "D'Avventura");
        rispostaGenereMap.put("Esplorazione dello spazio e mondi futuristici", "Fantascienza");
        rispostaGenereMap.put("Indagini e misteri da risolvere", "Giallo");
        rispostaGenereMap.put("Un’immersione nei sentimenti e nei legami umani", "Romantico");
        rispostaGenereMap.put("Conflitti intensi e azione adrenalinica", "Azione");

        rispostaGenereMap.put("Suspense e brividi", "Thriller");
        rispostaGenereMap.put("Stupore e meraviglia per mondi immaginari", "Fantasy");
        rispostaGenereMap.put("Divertimento e leggerezza", "Comico");
        rispostaGenereMap.put("Riflessione e ispirazione su temi profondi", "Narrativa");
        rispostaGenereMap.put("Nostalgia e romanticismo", "Romantico");

        rispostaGenereMap.put("Un’epoca passata, ricca di eventi storici", "Storico");
        rispostaGenereMap.put("Un mondo distopico o tecnologico", "Distopia");
        rispostaGenereMap.put("Una tranquilla cittadina con misteri da risolvere", "Giallo");
        rispostaGenereMap.put("Un’avventura magica o fiabesca", "Fiabe e favole");
        rispostaGenereMap.put("Una rappresentazione realistica della vita moderna", "Narrativa");

        rispostaGenereMap.put("Lotte di potere e conflitti sociali", "Politico");
        rispostaGenereMap.put("Scoperte scientifiche e innovazione tecnologica", "Scientifico");
        rispostaGenereMap.put("Emozioni forti e storie d’amore", "Romantico");
        rispostaGenereMap.put("Commedia e situazioni divertenti", "Comico");
        rispostaGenereMap.put("Racconti surreali e fantastici", "Fantasy");

        rispostaGenereMap.put("Un detective o un investigatore", "Giallo");
        rispostaGenereMap.put("Un eroe che combatte contro il male", "D'Avventura");
        rispostaGenereMap.put("Un visionario o scienziato che esplora nuovi orizzonti", "Fantascienza");
        rispostaGenereMap.put("Una persona comune che affronta grandi sfide emotive", "Narrativa");
        rispostaGenereMap.put("Un protagonista divertente e spensierato", "Comico");
    }

    /**
     * Ottiene il genere associato a una risposta.
     * @param contenutoRisposta Il contenuto della risposta.
     * @return Il genere corrispondente.
     */
    public String getGenereByRisposta(String contenutoRisposta) {
        return rispostaGenereMap.getOrDefault(contenutoRisposta, null);
    }
}

