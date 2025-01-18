package it.unisa.biblionet.utils.recensioneInterceptor;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RecensioneChangeInterceptorRegistration implements HibernatePropertiesCustomizer {

    /**
     * @param hibernateProperties the JPA vendor properties to customize
     */

    @Override
    public void customize(final Map<String, Object> hibernateProperties) {
        RecensioneChangeInterceptor.getIstance().subscribe(new MailConsumer());
        hibernateProperties.put("hibernate.session_factory.interceptor",
                RecensioneChangeInterceptor.getIstance());
    }

}
