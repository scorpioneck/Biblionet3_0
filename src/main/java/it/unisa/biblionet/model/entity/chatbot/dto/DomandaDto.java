package it.unisa.biblionet.model.entity.chatbot.dto;

import it.unisa.biblionet.model.entity.chatbot.Risposta;
import it.unisa.biblionet.model.entity.chatbot.Categoria;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link Risposta}
 */
@Value
public class DomandaDto implements Serializable {

    int idDomanda;
    @Size(min = 1, max = 2000)

    String contenuto;

    Categoria categoria;

    int faq;

    List<DomandaRispostaDto> risposte;
}