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
        public void testIfAuthorHasValidData() {
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
        public void testIfAuthorIsNull() {
            String expectedMessageIfAuthorIsNull = "Author cannot be null.";
            testAddAuthorThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorIsNull);
        }

        @Test
        public void testIfNameIsEmpty() {
            Author authorWithEmptyName = new Author("", 30, "Thriller");
            String expectedMessageIfAuthorsNameIsEmpty = "Author's name cannot be null or empty.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithEmptyName, expectedMessageIfAuthorsNameIsEmpty);
        }

        @Test
        public void testIfNameIsNull() {
            Author authorWithNullAsName = new Author(null, 30, "Thriller");
            String expectedMessageIfAuthorsNameIsNull = "Author's name cannot be null or empty.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithNullAsName, expectedMessageIfAuthorsNameIsNull);
        }

        @Test
        public void testIfAgeIsNull() {
            Author authorWithIncorrectAge = new Author("John Doe", null, "Thriller");
            String expectedMessageIfAuthorsAgeIsNull = "Author's age must be a positive number less than 120.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsNull);
        }

        @Test
        public void testIfAgeIsBelowTheLimit() {
            Author authorWithIncorrectAge = new Author("John Doe", -1, "Thriller");
            String expectedMessageIfAuthorsAgeIsNegativeNumber = "Author's age must be a positive number less than 120.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsNegativeNumber);
        }

        @Test
        public void testIfAgeAboveTheLimit() {
            Author authorWithIncorrectAge = new Author("John Doe", 121, "Thriller");
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's age must be a positive number less than 120.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsAboveTheLimit);
        }

        @Test
        public void testIfFavouriteGenreIsNull() {
            Author authorWithIncorrectAge = new Author("John Doe", 30, null);
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's favourite genre cannot be null or empty.";

            testAddAuthorThrowsExceptionAndReturnAMessage(authorWithIncorrectAge, expectedMessageIfAuthorsAgeIsAboveTheLimit);
        }

        @Test
        public void testIfFavouriteGenreIsEmpty() {
            Author authorWithIncorrectAge = new Author("John Doe", 30, "");
            String expectedMessageIfAuthorsAgeIsAboveTheLimit = "Author's favourite genre cannot be null or empty.";

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
        public void testWhenAuthorWasNotFound() {
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
        public void testWhenBookWasNotFound() {
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
        public void testWhenBookWasNotFound() {
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
        public void testWhenBookWasNotFound() {
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
    class TestUpdateBookAuthor {
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
        public void testWhenBookWasNotFound() {
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

    @Nested
    class TestUpdateAuthorName {
        @Test
        public void testIfNewAuthorNameHasValidData() {
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
        public void testWhenAuthorWasNotFound() {
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
        public void testIfNewNameIsNull() {
            String expectedMessageIfAuthorTitleIsNull = "Author's name cannot be null or empty.";

            testUpdateAuthorNameWhenThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorTitleIsNull);
        }

        @Test
        public void testIfNewNameIsEmpty() {
            String expectedMessageIfAuthorTitleIsEmpty = "Author's name cannot be null or empty.";

            testUpdateAuthorNameWhenThrowsExceptionAndReturnAMessage("", expectedMessageIfAuthorTitleIsEmpty);
        }

        private void testUpdateAuthorNameWhenThrowsExceptionAndReturnAMessage(String newName, String expectedMessage) {
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
        public void testIfNewAuthorAgeHasValidData() {
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
        public void testWhenAuthorWasNotFound() {
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
        public void testIfNewAgeIsNull() {
            String expectedMessageIfAuthorAgeIsNull = "Author's age must be a positive number less than 120.";

            testUpdateAuthorAgeWhenThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorAgeIsNull);
        }

        @Test
        public void testIfNewAgeIsBelowTheLimit() {
            String expectedMessageIfAuthorAgeIsBelowTheLimit = "Author's age must be a positive number less than 120.";

            testUpdateAuthorAgeWhenThrowsExceptionAndReturnAMessage(-1, expectedMessageIfAuthorAgeIsBelowTheLimit);
        }

        @Test
        public void testIfNewAgeIsAboveTheLimit() {
            String expectedMessageIfAuthorAgeIsAboveTheLimit = "Author's age must be a positive number less than 120.";

            testUpdateAuthorAgeWhenThrowsExceptionAndReturnAMessage(121, expectedMessageIfAuthorAgeIsAboveTheLimit);
        }

        private void testUpdateAuthorAgeWhenThrowsExceptionAndReturnAMessage(Integer newAge, String expectedMessage) {
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
        public void testIfNewAuthorNameHasValidData() {
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
        public void testWhenAuthorWasNotFound() {
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
        public void testIfNewFavouriteGenreIsNull() {
            String expectedMessageIfAuthorFavouriteGenreIsNull = "Author's favourite genre cannot be null or empty.";

            testUpdateAuthorFavouriteGenreWhenThrowsExceptionAndReturnAMessage(null, expectedMessageIfAuthorFavouriteGenreIsNull);
        }

        @Test
        public void testIfNewFavouriteGenreIsEmpty() {
            String expectedMessageIfAuthorFavouriteGenreIsEmpty = "Author's favourite genre cannot be null or empty.";

            testUpdateAuthorFavouriteGenreWhenThrowsExceptionAndReturnAMessage("", expectedMessageIfAuthorFavouriteGenreIsEmpty);
        }

        private void testUpdateAuthorFavouriteGenreWhenThrowsExceptionAndReturnAMessage(String newFavouriteGenre, String expectedMessage) {
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
        public void testWhenBookWasFound() {
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
        public void testWhenBookWasNotFound() {
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
        public void testWhenAuthorWasFound() {
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
        public void testWhenAuthorWasNotFound() {
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
        public void testWhenAuthorNameWasNull() {
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
