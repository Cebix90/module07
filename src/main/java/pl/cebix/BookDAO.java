package pl.cebix;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BookDAO {
    private final SessionFactory sessionFactory;

    public BookDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Book findBookByTitle(String bookTitle) {
        Book book = null;
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Book> bookQuery = cb.createQuery(Book.class);
            Root<Book> root = bookQuery.from(Book.class);
            bookQuery.select(root).where(cb.equal(root.get("title"), bookTitle));
            book = session.createQuery(bookQuery).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Book with title " + bookTitle + " was not found.");
        }

        return book;
    }
}
