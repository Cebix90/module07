package pl.cebix;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryDAOTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

//    @Test
//    public void testAddAuthor() {
//        LibraryDAO libraryDAO = new LibraryDAO();
//        Author author = new Author();
//        author.setId(1L);
//        author.setName("Test Author");
//        author.setAge(30);
//        author.setFavouriteGenre("Fantasy");
//
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        libraryDAO.addAuthor(author);
//
//        verify(sessionFactory, times(1)).openSession();
//        verify(session, times(1)).close();
//    }

    @Test
    public void testAddAuthor() {
        LibraryDAO libraryDAO = new LibraryDAO();
        Author author = new Author();
        author.setName("Test Author");
        author.setAge(30);
        author.setFavouriteGenre("Fantasy");

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);

        libraryDAO.addAuthor(author);

        verify(session).merge(author);
        verify(transaction).commit();
    }
}
