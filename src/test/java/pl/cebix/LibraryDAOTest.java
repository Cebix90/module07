package pl.cebix;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryDAOTest {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private BookDAO bookDAO;
    @Mock
    private AuthorDAO authorDAO;
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
        public void withValidData() {
            Author author = mock(Author.class);
            when(author.getName()).thenReturn("John Doe");

            Book book = new Book("Title", "Genre", 200);

            Session session = mock(Session.class);
            Transaction transaction = mock(Transaction.class);

            when(sessionFactory.openSession()).thenReturn(session);
            when(session.beginTransaction()).thenReturn(transaction);
            when(authorDAO.findAuthorByName("John Doe")).thenReturn(author);

            libraryDAO.addBookToAuthor(author.getName(), book);

            verify(session).merge(book);
            verify(transaction).commit();
        }


//        private void testValidateBookThrowsExceptionAndReturnAMessage(Author invalidBook, String expectedMessage) {
//            Session session = mock(Session.class);
//            Transaction transaction = mock(Transaction.class);
//
//            when(sessionFactory.openSession()).thenReturn(session);
//            when(session.beginTransaction()).thenReturn(transaction);
//
//            PrintStream originalOut = System.out;
//            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//            System.setOut(new PrintStream(outContent));
//
//            libraryDAO.addAuthor(invalidAuthor);
//
//            assertTrue(outContent.toString().contains(expectedMessage));
//
//            System.setOut(originalOut);
//        }
    }
}
