package pl.cebix;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AuthorSessionFactory {
    public static SessionFactory getAuthorSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure("/hibernate.cfg.xml");

        return configuration.buildSessionFactory();
    }
}
