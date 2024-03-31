package pl.cebix;

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

import static org.junit.jupiter.api.Assertions.assertTrue;
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
        public void testAddAuthorWithValidData() {
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
        public void testAddAuthorIfAuthorIsNull() {
            String expectedMessageIfAuthorIsNull = "Author cannot be null.";
            testAddAuthorThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorIsNull);
        }

        @Test
        public void testAddAuthorWithEmptyName() {
            Author authorWithEmptyName = new Author("", 30, "Thriller");
            String expectedMessageIfAuthorsNameIsEmpty = "Author's name cannot be null or empty.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithEmptyName, expectedMessageIfAuthorsNameIsEmpty);
        }

        @Test
        public void testAddAuthorWithNullAsName() {
            Author authorWithNullAsName = new Author(null, 30, "Thriller");
            String expectedMessageIfAuthorsNameIsNull = "Author's name cannot be null or empty.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithNullAsName, expectedMessageIfAuthorsNameIsNull);
        }

        @Test
        public void testAddAuthorWithAgeAsNull() {
            Author authorWithIncorrectAge = new Author("John Doe", null, "Thriller");
            String expectedMessageIfAuthorsAgeIsNull = "Author's age must be a positive number less than 120.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsNull);
        }

        @Test
        public void testAddAuthorWithMinusAge() {
            Author authorWithIncorrectAge = new Author("John Doe", -1, "Thriller");
            String expectedMessageIfAuthorsAgeIsNegativeNumber = "Author's age must be a positive number less than 120.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsNegativeNumber);
        }

        @Test
        public void testAddAuthorWithAgeAboveTheLimit() {
            Author authorWithIncorrectAge = new Author("John Doe", 121, "Thriller");
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's age must be a positive number less than 120.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsAboveTheLimit);
        }

        private void testAddAuthorThrowsExceptionAndReturnAMessage(Author invalidAuthor, String expectedMessage) {
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
        public void testWithValidData() {
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
        public void testWhenAuthorIsNotExist() {
            String authorName = "John Doe";
            Book book = new Book("Title", "Fantasy", 250);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            doReturn(null).when(libraryDAO).findAuthorByName(authorName);

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            libraryDAO.addBookToAuthor(authorName, book);

            assertTrue(outContent.toString().contains("Author with name " + authorName + " is not exist."));

            System.setOut(originalOut);
        }

        @Test
        public void testIfBookIsNull() {
            String expectedMessageIfBookIsNull = "Book cannot be null.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(null, expectedMessageIfBookIsNull);
        }

        @Test
        public void testIfBooksTitleIsNull() {
            Book book = new Book(null, "Fantasy", 250);
            String expectedMessageIfBooksTitleIsNull = "Book's title cannot be null or empty.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsNull);
        }

        @Test
        public void testIfBooksTitleIsEmpty() {
            Book book = new Book("", "Fantasy", 250);
            String expectedMessageIfBooksTitleIsEmpty = "Book's title cannot be null or empty.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void testIfBooksGenreIsNull() {
            Book book = new Book("Title", null, 250);
            String expectedMessageIfBooksTitleIsEmpty = "Book's genre cannot be null or empty.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void testIfBooksGenreIsEmpty() {
            Book book = new Book("Title", "", 250);
            String expectedMessageIfBooksTitleIsEmpty = "Book's genre cannot be null or empty.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void testIfBooksNumberOfPagesIsNull() {
            Book book = new Book("Title", "Fantasy", null);
            String expectedMessageIfBooksTitleIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void testIfBooksNumberOfPagesIsBelowTheLimit() {
            Book book = new Book("Title", "Fantasy", 0);
            String expectedMessageIfBooksTitleIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        @Test
        public void testIfBooksNumberOfPagesIsAboveTheLimit() {
            Book book = new Book("Title", "Fantasy", 3001);
            String expectedMessageIfBooksTitleIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testAddBookToAuthorThrowsExceptionAndReturnAMessage(book, expectedMessageIfBooksTitleIsEmpty);
        }

        private void testAddBookToAuthorThrowsExceptionAndReturnAMessage(Book invalidBook, String expectedMessage) {
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
        public void testIfNewTitleHasValidData() {
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
        public void testIfNewTitleIsNull() {
            String expectedMessageIfBooksTitleIsNull = "Book's title cannot be null or empty.";

            testUpdateBookTitleWhenThrowsExceptionAndReturnAMessage(null, expectedMessageIfBooksTitleIsNull);
        }

        @Test
        public void testIfNewTitleIsEmpty() {
            String expectedMessageIfBooksTitleIsEmpty = "Book's title cannot be null or empty.";

            testUpdateBookTitleWhenThrowsExceptionAndReturnAMessage("", expectedMessageIfBooksTitleIsEmpty);
        }

        private void testUpdateBookTitleWhenThrowsExceptionAndReturnAMessage(String newTitle, String expectedMessage) {
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
        public void testIfNewGenreHasValidData() {
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
        public void testIfNewGenreIsNull() {
            String expectedMessageIfBooksGenreIsNull = "Book's genre cannot be null or empty.";

            testUpdateBookGenreWhenThrowsExceptionAndReturnAMessage(null, expectedMessageIfBooksGenreIsNull);
        }

        @Test
        public void testIfNewGenreIsEmpty() {
            String expectedMessageIfBooksGenreIsEmpty = "Book's genre cannot be null or empty.";

            testUpdateBookGenreWhenThrowsExceptionAndReturnAMessage("", expectedMessageIfBooksGenreIsEmpty);
        }

        private void testUpdateBookGenreWhenThrowsExceptionAndReturnAMessage(String newGenre, String expectedMessage) {
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
        public void testWithValidData() {
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
        public void testIfNewNumberOfPagesIsNull() {
            String expectedMessageIfBooksGenreIsNull = "Book's number of pages must be a positive number between 1 and 3000.";

            testUpdateBookNumberOfPagesWhenThrowsExceptionAndReturnAMessage(null, expectedMessageIfBooksGenreIsNull);
        }

        @Test
        public void testIfNewNumberOfPagesIsBelowTheLimit() {
            String expectedMessageIfBooksGenreIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testUpdateBookNumberOfPagesWhenThrowsExceptionAndReturnAMessage(0, expectedMessageIfBooksGenreIsEmpty);
        }

        @Test
        public void testIfNewNumberOfPagesIsAboveTheLimit() {
            String expectedMessageIfBooksGenreIsEmpty = "Book's number of pages must be a positive number between 1 and 3000.";

            testUpdateBookNumberOfPagesWhenThrowsExceptionAndReturnAMessage(3001, expectedMessageIfBooksGenreIsEmpty);
        }

        private void testUpdateBookNumberOfPagesWhenThrowsExceptionAndReturnAMessage(Integer newNumberOfPages, String expectedMessage) {
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
    class TestUpdateAuthor {
        @Test
        public void testIfNewAuthorHasValidData() {
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
        public void testIfNewAuthorIsNull() {
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
}
