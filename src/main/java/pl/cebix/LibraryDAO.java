package pl.cebix;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LibraryDAO {
    private final SessionFactory sessionFactory;

    public LibraryDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addAuthor(Author author) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            validateAuthor(author);

            session.merge(author);
            transaction.commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBookToAuthor(String authorName, Book book) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = findAuthorByName(authorName);

            if (author != null) {
                if (book != null) {
                    book.setAuthor(author);
                }

                validateBook(book);

                session.merge(book);
            } else {
                System.out.println("Author with name " + authorName + " is not exist.");
            }

            transaction.commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
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

        return session.createQuery(authorsQuery).getResultList();
    }

    public List<Book> getAllBooks() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> booksQuery = cb.createQuery(Book.class);
        Root<Book> root = booksQuery.from(Book.class);
        booksQuery.select(root);

        return session.createQuery(booksQuery).getResultList();
    }

    public List<Object> getAllBooksAndAuthors() {
        List<Object> booksAndAuthors = new ArrayList<>();

        List<Book> allBooks = getAllBooks();
        booksAndAuthors.add(allBooks);

        List<Author> allAuthors = getAllAuthors();
        booksAndAuthors.add(allAuthors);

        return booksAndAuthors;
    }

    public void updateBookTitle(String theTitle, String newTitle) {
        updateBookField(theTitle, book -> book.setTitle(newTitle), this::validateBookTitle, newTitle);
    }

    public void updateBookGenre(String theTitle, String newGenre) {
        updateBookField(theTitle, book -> book.setGenre(newGenre), this::validateBookGenre, newGenre);
    }

    public void updateBookNumberOfPages(String theTitle, Integer newNumberOfPages) {
        updateBookField(theTitle, book -> book.setNumberOfPages(newNumberOfPages), this::validateBookNumberOfPages, newNumberOfPages);
    }

    public void updateBookAuthor(String theTitle, Author newAuthor) {
        updateBookField(theTitle, book -> book.setAuthor(newAuthor), this::validateBookAuthor, newAuthor);
    }

    private <T> void updateBookField(String theTitle, Consumer<Book> fieldUpdater, Consumer<T> validator, T value) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Book book = findBookByTitle(theTitle);

            if (book != null) {
                fieldUpdater.accept(book);
                validator.accept(value);
                session.merge(book);
            }

            transaction.commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateAuthorName(String theAuthorName, String newAuthorName) {
        updateAuthorField(theAuthorName, author -> author.setName(newAuthorName), this::validateAuthorName, newAuthorName);
    }

    public void updateAuthorAge(String theAuthorName, Integer newAge) {
        updateAuthorField(theAuthorName, author -> author.setAge(newAge), this::validateAuthorAge, newAge);
    }

    public void updateAuthorFavouriteGenre(String theAuthorName, String newFavouriteGenre) {
        updateAuthorField(theAuthorName, author -> author.setFavouriteGenre(newFavouriteGenre), this::validateAuthorFavouriteGenre, newFavouriteGenre);
    }

    private <T> void updateAuthorField(String theAuthorName, Consumer<Author> fieldUpdater, Consumer<T> validator, T value) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = findAuthorByName(theAuthorName);

            if (author != null) {
                fieldUpdater.accept(author);
                validator.accept(value);
                session.merge(author);
            }

            transaction.commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteBook(String title) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Book book = findBookByTitle(title);

        if (book != null) {
            session.remove(book);
        }

        transaction.commit();
        session.close();
    }

    public void deleteAuthor(String authorName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Author author = findAuthorByName(authorName);

        if (author != null) {
            session.remove(author);
        }

        transaction.commit();
        session.close();
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

    private void validateAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        validateAuthorName(author.getName());
        validateAuthorAge(author.getAge());
        validateAuthorFavouriteGenre(author.getFavouriteGenre());
    }

    private void validateAuthorName(String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            throw new IllegalArgumentException("Author's name cannot be null or empty.");
        }
    }

    private void validateAuthorAge(Integer authorAge) {
        if (authorAge == null || authorAge < 0 || authorAge > 120) {
            throw new IllegalArgumentException("Author's age must be a positive number less than 120.");
        }
    }

    private void validateAuthorFavouriteGenre(String favouriteGenre){
        if(favouriteGenre == null) {
            throw new IllegalArgumentException("Author's favourite genre cannot be null");
        }
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        validateBookTitle(book.getTitle());
        validateBookGenre(book.getGenre());
        validateBookNumberOfPages(book.getNumberOfPages());
        validateBookAuthor(book.getAuthor());
    }

    private void validateBookTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Book's title cannot be null or empty.");
        }
    }

    private void validateBookGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            throw new IllegalArgumentException("Book's genre cannot be null or empty.");
        }
    }

    private void validateBookNumberOfPages(Integer numberOfPages) {
        if (numberOfPages == null || numberOfPages < 1 || numberOfPages > 3000) {
            throw new IllegalArgumentException("Book's number of pages must be a positive number between 1 and 3000.");
        }
    }

    private void validateBookAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Book's author cannot be null.");
        }
    }
}
