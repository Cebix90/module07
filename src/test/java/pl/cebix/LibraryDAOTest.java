package pl.cebix;

import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryDAOTest {
    @Mock
    private SessionFactory sessionFactory;
    @Spy
    @InjectMocks
    private LibraryDAO libraryDAO;

    @Nested
    class TestAddAuthor {
        @Test
        public void checkIfAuthorHasBeenAddedSuccessfully() {
            Author author = new Author("John Doe", 30, "Thriller");
            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);
            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            libraryDAO.addAuthor(author);

            verify(session).merge(author);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenAuthorIsNull() {
            String expectedMessageIfAuthorIsNull = "Author cannot be null.";
            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorNameIsEmpty() {
            Author authorWithEmptyName = new Author("", 30, "Thriller");
            String expectedMessageIfAuthorsNameIsEmpty = "Author's name cannot be null or empty.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithEmptyName, expectedMessageIfAuthorsNameIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorNameIsNull() {
            Author authorWithNullAsName = new Author(null, 30, "Thriller");
            String expectedMessageIfAuthorsNameIsNull = "Author's name cannot be null or empty.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithNullAsName, expectedMessageIfAuthorsNameIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorAgeIsNull() {
            Author authorWithIncorrectAge = new Author("John Doe", null, "Thriller");
            String expectedMessageIfAuthorsAgeIsNull = "Author's age must be a positive number less than 120.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorAgeIsBelowTheLimit() {
            Author authorWithIncorrectAge = new Author("John Doe", -1, "Thriller");
            String expectedMessageIfAuthorsAgeIsNegativeNumber = "Author's age must be a positive number less than 120.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsNegativeNumber);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorAgeIsAboveTheLimit() {
            Author authorWithIncorrectAge = new Author("John Doe", 121, "Thriller");
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's age must be a positive number less than 120.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsAboveTheLimit);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorFavouriteGenreIsNull() {
            Author authorWithIncorrectAge = new Author("John Doe", 30, null);
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's favourite genre cannot be null or empty.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsAboveTheLimit);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorFavouriteGenreIsEmpty() {
            Author authorWithIncorrectAge = new Author("John Doe", 30, "");
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's favourite genre cannot be null or empty.";

            testIfInvalidAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsAboveTheLimit);
        }

        private void testIfInvalidAuthorThrowsExceptionAndReturnAMessage(Author invalidAuthor, String expectedMessage) {
            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.addAuthor(invalidAuthor);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestAddBookToAuthor {
        @Test
        public void checkIfBookHasBeenAddedToAuthorSuccessfully() {
            Author author = new Author("John Doe", 25, "Fantasy");
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            libraryDAO.addBookToAuthor(author.getName(), book);

            verify(session).merge(book);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenAuthorWasNotFound() {
            String authorName = "John Doe";
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Author with name " + authorName + " was not found.")).when(libraryDAO).findAuthorByName(authorName);

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.addBookToAuthor(authorName, book);

            assertTrue(outContent.toString().contains("Author with name " + authorName + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenBookIsNull() {
            String expectedMessageIfBookIsNull = "Book cannot be null.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(null, expectedMessageIfBookIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenBookTitleIsNull() {
            Book book = new Book(null, "Fantasy", 250);
            String expectedMessageIfBooksTitleIsNull = "Book's title cannot be null or empty.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenBookTitleIsEmpty() {
            Book book = new Book("", "Fantasy", 250);
            String expectedMessageIfBooksTitleIsEmpty = "Book's title cannot be null or empty.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenBookGenreIsNull() {
            Book book = new Book("Title", null, 250);
            String expectedMessageIfBooksTitleIsEmpty = "Book's genre cannot be null or empty.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenBookGenreIsEmpty() {
            Book book = new Book("Title", "", 250);
            String expectedMessageIfBooksTitleIsEmpty = "Book's genre cannot be null or empty.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenBookNumberOfPagesIsNull() {
            Book book = new Book("Title", "Fantasy", null);
            String expectedMessageIfBooksTitleIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenBookNumberOfPagesIsBelowTheLimit() {
            Book book = new Book("Title", "Fantasy", 0);
            String expectedMessageIfBooksTitleIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenBookNumberOfPagesIsAboveTheLimit() {
            Book book = new Book("Title", "Fantasy", 3001);
            String expectedMessageIfBooksTitleIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testIfInvalidBookThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        private void testIfInvalidBookThrowsExceptionAndReturnAMessage(Book invalidBook, String expectedMessage) {
            Author author = new Author("John Doe", 25, "Fantasy");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.addBookToAuthor(author.getName(), invalidBook);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateBookTitle {
        @Test
        public void checkIfTitleHasBeenUpdatedSuccessfully() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            libraryDAO.updateBookTitle(book.getTitle(), "New Title");

            verify(session).merge(book);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenBookWasNotFound() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Book with title " + book.getTitle() + " was not found.")).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookTitle(book.getTitle(), "New Title");

            assertTrue(outContent.toString().contains("Book with title " + book.getTitle() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewTitleIsNull() {
            String expectedMessageIfBooksTitleIsNull = "Book's title cannot be null or empty.";

            testIfInvalidNewTitleThrowsExceptionAndReturnAMessage(null, expectedMessageIfBooksTitleIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenNewTitleIsEmpty() {
            String expectedMessageIfBooksTitleIsEmpty = "Book's title cannot be null or empty.";

            testIfInvalidNewTitleThrowsExceptionAndReturnAMessage("", expectedMessageIfBooksTitleIsEmpty);
        }

        private void testIfInvalidNewTitleThrowsExceptionAndReturnAMessage(String newTitle, String expectedMessage) {
            Book book = new Book("Title", "Fantasy", 250);
            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookTitle(book.getTitle(), newTitle);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateBookGenre {
        @Test
        public void checkIfGenreHasBeenUpdatedSuccessfully() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            libraryDAO.updateBookGenre(book.getTitle(), "New Genre");

            verify(session).merge(book);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenBookWasNotFound() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Book with title " + book.getTitle() + " was not found.")).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookGenre(book.getTitle(), "New Genre");

            assertTrue(outContent.toString().contains("Book with title " + book.getTitle() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewGenreIsNull() {
            String expectedMessageIfBooksGenreIsNull = "Book's genre cannot be null or empty.";

            testIfInvalidNewGenreThrowsExceptionAndReturnAMessage(null, expectedMessageIfBooksGenreIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenNewGenreIsEmpty() {
            String expectedMessageIfBooksGenreIsEmpty = "Book's genre cannot be null or empty.";

            testIfInvalidNewGenreThrowsExceptionAndReturnAMessage("", expectedMessageIfBooksGenreIsEmpty);
        }

        private void testIfInvalidNewGenreThrowsExceptionAndReturnAMessage(String newGenre, String expectedMessage) {
            Book book = new Book("Title", "Fantasy", 250);
            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookGenre(book.getTitle(), newGenre);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateBookNumberOfPages {
        @Test
        public void checkIfNumberOfPagesHasBeenUpdatedSuccessfully() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            libraryDAO.updateBookNumberOfPages(book.getTitle(), 260);

            verify(session).merge(book);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenBookWasNotFound() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Book with title " + book.getTitle() + " was not found.")).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookNumberOfPages(book.getTitle(), 111);

            assertTrue(outContent.toString().contains("Book with title " + book.getTitle() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewNumberOfPagesIsNull() {
            String expectedMessageIfBooksGenreIsNull = "Book's number of pages must be a positive number between 1 and 3000.";

            testIfInvalidNewNumberOfPagesThrowsExceptionAndReturnAMessage(null, expectedMessageIfBooksGenreIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenNewNumberOfPagesIsBelowTheLimit() {
            String expectedMessageIfBooksGenreIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testIfInvalidNewNumberOfPagesThrowsExceptionAndReturnAMessage(0, expectedMessageIfBooksGenreIsEmpty);
        }

        @Test
        public void checkThrowingExceptionWhenNewNumberOfPagesIsAboveTheLimit() {
            String expectedMessageIfBooksGenreIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testIfInvalidNewNumberOfPagesThrowsExceptionAndReturnAMessage(3001, expectedMessageIfBooksGenreIsEmpty);
        }

        private void testIfInvalidNewNumberOfPagesThrowsExceptionAndReturnAMessage(Integer newNumberOfPages, String expectedMessage) {
            Book book = new Book("Title", "Fantasy", 250);
            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookNumberOfPages(book.getTitle(), newNumberOfPages);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateBookAuthor {
        @Test
        public void checkIfAuthorHasBeenUpdatedSuccessfully() {
            Author newAuthor = new Author("New Author", 30, "Genre");
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            libraryDAO.updateBookAuthor(book.getTitle(), newAuthor);

            verify(session).merge(book);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenBookWasNotFound() {
            Author newAuthor = mock(Author.class);
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Book with title " + book.getTitle() + " was not found.")).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookAuthor(book.getTitle(), newAuthor);

            assertTrue(outContent.toString().contains("Book with title " + book.getTitle() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewAuthorIsNull() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateBookAuthor(book.getTitle(), null);

            assertTrue(outContent.toString().contains("Book's author cannot be null."));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateAuthorName {
        @Test
        public void checkIfNameHasBeenUpdatedSuccessfully() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            libraryDAO.updateAuthorName(author.getName(), "New Name");

            verify(session).merge(author);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenAuthorWasNotFound() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Author with name " + author.getName() + " was not found.")).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateAuthorName(author.getName(), "New Name");

            assertTrue(outContent.toString().contains("Author with name " + author.getName() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewNameIsNull() {
            String expectedMessageIfAuthorTitleIsNull = "Author's name cannot be null or empty.";

            testIfInvalidNewNameThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorTitleIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenNewNameIsEmpty() {
            String expectedMessageIfAuthorTitleIsEmpty = "Author's name cannot be null or empty.";

            testIfInvalidNewNameThrowsExceptionAndReturnAMessage("", expectedMessageIfAuthorTitleIsEmpty);
        }

        private void testIfInvalidNewNameThrowsExceptionAndReturnAMessage(String newName, String expectedMessage) {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateAuthorName(author.getName(), newName);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateAuthorAge {
        @Test
        public void checkIfAgeHasBeenUpdatedSuccessfully() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            libraryDAO.updateAuthorAge(author.getName(), 55);

            verify(session).merge(author);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenAuthorWasNotFound() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Author with name " + author.getName() + " was not found.")).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateAuthorAge(author.getName(), 55);

            assertTrue(outContent.toString().contains("Author with name " + author.getName() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewAgeIsNull() {
            String expectedMessageIfAuthorAgeIsNull = "Author's age must be a positive number less than 120.";

            testIfInvalidNewAgeThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorAgeIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenNewAgeIsBelowTheLimit() {
            String expectedMessageIfAuthorAgeIsBelowTheLimit = "Author's age must be a positive number less than 120.";

            testIfInvalidNewAgeThrowsExceptionAndReturnAMessage(-1, expectedMessageIfAuthorAgeIsBelowTheLimit);
        }

        @Test
        public void checkThrowingExceptionWhenNewAgeIsAboveTheLimit() {
            String expectedMessageIfAuthorAgeIsAboveTheLimit = "Author's age must be a positive number less than 120.";

            testIfInvalidNewAgeThrowsExceptionAndReturnAMessage(121, expectedMessageIfAuthorAgeIsAboveTheLimit);
        }

        private void testIfInvalidNewAgeThrowsExceptionAndReturnAMessage(Integer newAge, String expectedMessage) {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateAuthorAge(author.getName(), newAge);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestUpdateAuthorFavouriteGenre {
        @Test
        public void checkIfFavouriteGenreHasBeenUpdatedSuccessfully() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            libraryDAO.updateAuthorFavouriteGenre(author.getName(), "New Favourite Genre");

            verify(session).merge(author);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenAuthorWasNotFound() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Author with name " + author.getName() + " was not found.")).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateAuthorFavouriteGenre(author.getName(), "New Favourite Genre");

            assertTrue(outContent.toString().contains("Author with name " + author.getName() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenNewFavouriteGenreIsNull() {
            String expectedMessageIfAuthorFavouriteGenreIsNull = "Author's favourite genre cannot be null or empty.";

            testIfInvalidNewFavouriteGenreThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorFavouriteGenreIsNull);
        }

        @Test
        public void checkThrowingExceptionWhenNewFavouriteGenreIsEmpty() {
            String expectedMessageIfAuthorFavouriteGenreIsEmpty = "Author's favourite genre cannot be null or empty.";

            testIfInvalidNewFavouriteGenreThrowsExceptionAndReturnAMessage("", expectedMessageIfAuthorFavouriteGenreIsEmpty);
        }

        private void testIfInvalidNewFavouriteGenreThrowsExceptionAndReturnAMessage(String newFavouriteGenre, String expectedMessage) {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.updateAuthorFavouriteGenre(author.getName(), newFavouriteGenre);

            assertTrue(outContent.toString().contains(expectedMessage));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestDeleteBook {
        @Test
        public void checkIfBookHasBeenDeletedSuccessfully() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(book).when(libraryDAO).findBookByTitle(book.getTitle());

            libraryDAO.deleteBook(book.getTitle());

            verify(session).remove(book);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenBookWasNotFound() {
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Book with title " + book.getTitle() + " was not found.")).when(libraryDAO).findBookByTitle(book.getTitle());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.deleteBook(book.getTitle());

            assertTrue(outContent.toString().contains("Book with title " + book.getTitle() + " was not found."));

            System.setOut(originalOut);
        }
    }

    @Nested
    class TestDeleteAuthor {
        @Test
        public void checkIfAuthorHasBeenDeletedSuccessfully() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(author).when(libraryDAO).findAuthorByName(author.getName());

            libraryDAO.deleteAuthor(author.getName());

            verify(session).remove(author);
            verify(transaction).commit();
        }

        @Test
        public void checkThrowingExceptionWhenAuthorWasNotFound() {
            Author author = new Author("Name", 44, "Favourite Genre");

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("Author with name " + author.getName() + " was not found.")).when(libraryDAO).findAuthorByName(author.getName());

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.deleteAuthor(author.getName());

            assertTrue(outContent.toString().contains("Author with name " + author.getName() + " was not found."));

            System.setOut(originalOut);
        }

        @Test
        public void checkThrowingExceptionWhenAuthorNameIsNull() {
            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doThrow(new NoResultException("The name of the author must be provided.")).when(libraryDAO).findAuthorByName(null);

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.deleteAuthor(null);

            assertTrue(outContent.toString().contains("The name of the author must be provided."));

            System.setOut(originalOut);
        }
    }
}
