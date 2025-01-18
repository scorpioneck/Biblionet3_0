package it.unisa.biblionet.utils.recensioneInterceptor;

import it.unisa.biblionet.model.entity.blog.Recensione;
import lombok.NoArgsConstructor;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class RecensioneChangeInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;


    private static RecensioneChangeInterceptor instance;

    private List<onRecensioneChangeConsumer> reactors = new LinkedList<>();
    /**
     * Aggiunge un Consumer alla lista dei Consumer.
     * @param reactor Il Consumer da iscrivere all'Intercettore
     */

    public void subscribe(final onRecensioneChangeConsumer reactor) {
        reactors.add(reactor);
    }

    public  static RecensioneChangeInterceptor getIstance(){
        if(RecensioneChangeInterceptor.instance == null){
            return  RecensioneChangeInterceptor.instance = new RecensioneChangeInterceptor();
        }
        return RecensioneChangeInterceptor.instance;
    }

    /**
     * @param entity
     * @param id
     * @param currentState
     * @param previousState
     * @param propertyNames
     * @param types
     * @return
     * @throws CallbackException
     */
    @Override
    public boolean onFlushDirty(final Object entity,
                                final Object id,
                                final Object[] currentState,
                                final Object[] previousState,
                                final String[] propertyNames,
                                final Type[] types)  {

        if(entity.getClass() != Recensione.class){
            return super.onFlushDirty(entity,
                    id,
                    currentState,
                    previousState,
                    propertyNames,
                    types);
        }

        var recensione = (Recensione) entity;
        this.reactors.forEach(x -> x.accept(recensione));
        return true;
    }
}
