package pl.cebix;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class AuthorDAO {
    private final SessionFactory sessionFactory;

    public AuthorDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Author findAuthorByName(String authorName) {
        Author author = null;
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Author> authorQuery = cb.createQuery(Author.class);
            Root<Author> root = authorQuery.from(Author.class);
            authorQuery.select(root).where(cb.equal(root.get("name"), authorName));
            author = session.createQuery(authorQuery).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Author with name " + authorName + " was not found.");
        }

        return author;
    }
}
