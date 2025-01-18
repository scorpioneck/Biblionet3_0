package it.unisa.biblionet.utils.eventchangeinterceptor;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Inserire Javadoc.
 */
@Component
public class EventChangeInterceptorRegistration
    implements HibernatePropertiesCustomizer {

    @Override
    public void customize(final Map<String, Object> hibernateProperties) {
        EventChangeInterceptor.getInstance().subscribe(new MailConsumer());
        hibernateProperties.put(
            "hibernate.session_factory.interceptor",
            EventChangeInterceptor.getInstance()
        );
    }

}
