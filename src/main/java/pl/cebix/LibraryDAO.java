package pl.cebix;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class LibraryDAO {
    private final SessionFactory sessionFactory = AuthorSessionFactory.getAuthorSessionFactory();

    public void addAuthor(Author author) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.merge(author);
        transaction.commit();
        session.close();
    }

    public void addBookToAuthor(String authorName, Book book) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Author author = getAuthorByName(authorName);

        if(author != null) {
            book.setAuthor(author);
            session.merge(book);
        } else {
            System.out.println("Author with name " + authorName + " is not exist.");
        }

        transaction.commit();
        session.close();
    }

    private Author getAuthorByName(String authorName) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Author> authorQuery = cb.createQuery(Author.class);
        Root<Author> root = authorQuery.from(Author.class);
        authorQuery.select(root).where(cb.equal(root.get("name"), authorName));

        return session.createQuery(authorQuery).getSingleResult();
    }

    public List<Book> getBooksOfAuthor(String authorName) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> booksQuery = cb.createQuery(Book.class);
        Root<Book> root = booksQuery.from(Book.class);

        booksQuery.select(root).where(cb.equal(root.get("author").get("name"), authorName));

        List<Book> booksOfAuthor = session.createQuery(booksQuery).getResultList();
        session.close();

        return booksOfAuthor;
    }

    public List<Author> getAllAuthors() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Author> authorsQuery = cb.createQuery(Author.class);
        Root<Author> root = authorsQuery.from(Author.class);
        authorsQuery.select(root);

        List<Author> authors = session.createQuery(authorsQuery).getResultList();
        session.close();

        return authors;
    }

    public List<Book> getAllBooks() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> booksQuery = cb.createQuery(Book.class);
        Root<Book> root = booksQuery.from(Book.class);
        booksQuery.select(root);

        List<Book> books = session.createQuery(booksQuery).getResultList();
        session.close();

        return books;
    }
//    public getAllBooksAndAuthors();

    public void deleteBook(String title) {

    }

    public void deleteAuthor(String authorName) {

    }

}
