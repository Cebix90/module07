package pl.cebix;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
    @InjectMocks
    private LibraryDAO libraryDAO;

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
    public void testAddAuthorWithIncorrectName() {
        Author authorWithEmptyName = new Author("", 30, "Thriller");
        Session session = mock(Session.class);
        Transaction transaction = mock(Transaction.class);
        String expectedMessageIfAuthorIsEmpty = "Author's name cannot be null or empty.";

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        libraryDAO.addAuthor(authorWithEmptyName);

        assertTrue(outContent.toString().contains(expectedMessageIfAuthorIsEmpty));

        System.setOut(originalOut);
    }
}
