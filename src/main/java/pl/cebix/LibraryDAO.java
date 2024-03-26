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
            book.setAuthor(author);

            if (author != null) {
                validateBook(book);

                book.setAuthor(author);

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

    public void updateBook(String actualTitle, String newTitle, String newGenre, Integer newNumberOfPages, Author newAuthor) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Book book = findBookByTitle(actualTitle);

            if (book != null) {
                if (newTitle != null) {
                    book.setTitle(newTitle);
                }
                if (newGenre != null) {
                    book.setGenre(newGenre);
                }
                if (newNumberOfPages != null) {
                    book.setNumberOfPages(newNumberOfPages);
                }
                if (newAuthor != null) {
                    book.setAuthor(newAuthor);
                }

                validateBook(book);

                session.merge(book);
                transaction.commit();
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateAuthor(String actualAuthorName, String newName, Integer newAge, String newFavouriteGenre) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Author author = findAuthorByName(actualAuthorName);

            if (author != null) {
                if (newName != null) {
                    author.setName(newName);
                }
                if (newAge != null) {
                    author.setAge(newAge);
                }
                if (newFavouriteGenre != null) {
                    author.setFavouriteGenre(newFavouriteGenre);
                }

                validateAuthor(author);

                session.merge(author);
                transaction.commit();
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteBook(String title) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Book book = findBookByTitle(title);
        session.remove(book);
        transaction.commit();
        session.close();
    }

    public void deleteAuthor(String authorName) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Author author = findAuthorByName(authorName);
        session.remove(author);
        transaction.commit();
        session.close();
    }

    private Author findAuthorByName(String authorName) {
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

    private Book findBookByTitle(String bookTitle) {
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
        if (author.getName() == null || author.getName().isEmpty()) {
            throw new IllegalArgumentException("Author's name cannot be null or empty.");
        }
        if (author.getAge() == null || author.getAge() < 0 || author.getAge() > 120) {
            throw new IllegalArgumentException("Author's age must be a positive number less than 120.");
        }
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Book's title cannot be null or empty.");
        }
        if (book.getGenre() == null || book.getGenre().isEmpty()) {
            throw new IllegalArgumentException("Book's genre cannot be null or empty.");
        }
        if (book.getNumberOfPages() == null || book.getNumberOfPages() < 1 || book.getNumberOfPages() > 3000) {
            throw new IllegalArgumentException("Book's number of pages must be a positive number between 1 and 3000.");
        }
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("Book's author cannot be null.");
        }
    }
}
