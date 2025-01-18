package it.unisa.biblionet.model.entity.chatbot.dto;

import it.unisa.biblionet.model.entity.chatbot.Categoria;
import it.unisa.biblionet.model.entity.chatbot.DomandaRisposta;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link DomandaRisposta}
 */
@Value
public class DomandaRispostaDto implements Serializable {

    int idDomandaRisposta;

    @Size(min = 1, max = 2000)
    String contenuto;

    Categoria categoria;

    int domandaPadre;
}