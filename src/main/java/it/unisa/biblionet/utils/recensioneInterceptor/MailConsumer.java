package it.unisa.biblionet.utils.recensioneInterceptor;

import it.unisa.biblionet.model.entity.blog.Recensione;


public class MailConsumer implements onRecensioneChangeConsumer{

    @Override
    public void accept(final Recensione recensione) {
        System.err.println(recensione.getTitolo());
    }
}
