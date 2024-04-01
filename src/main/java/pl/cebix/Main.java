package pl.cebix;

import org.hibernate.SessionFactory;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = AuthorSessionFactory.getAuthorSessionFactory();
        LibraryDAO dao = new LibraryDAO(sessionFactory);

        Author author = new Author();
        author.setName("Mateusz");
        author.setAge(33);
        author.setFavouriteGenre("Fantasy");

        dao.addAuthor(author);
        Author author1 = new Author("Tolkien", 55, "Fantasy");
        dao.addAuthor(author1);
//        dao.addAuthor(new Author("Tolkien", 55, "Fantasy"));
//        dao.addAuthor(new Author("Tolkien", 55, "Fantasy"));
//        dao.addAuthor(new Author("Tolkien", 55, "Fantasy"));

        Author author2 = new Author();
        author2.setName("Rowling");
        author2.setAge(45);
        author2.setFavouriteGenre("Fantasy");

        dao.addAuthor(author);
        dao.addAuthor(author2);

        Book book1 = new Book();
        book1.setTitle("Eragon");
        book1.setGenre("Fantasy");
        book1.setNumberOfPages(320);
//        book1.setAuthor(author);

        Book book2 = new Book();
        book2.setTitle("Malowany Czlowiek");
        book2.setGenre("Fantasy");
        book2.setNumberOfPages(400);
//        book2.setAuthor(author);

        Book book3 = new Book();
        book3.setTitle("Harry Potter");
        book3.setGenre("Fantasy");
        book3.setNumberOfPages(350);
//        book3.setAuthor(author2);

        dao.addBookToAuthor("Mateusz", book1);
        dao.addBookToAuthor("Mateusz", book2);
        dao.addBookToAuthor("Rowling", book3);

        List<Book> listOfBooks = dao.getBooksOfAuthor("Mateusz");

        for(Book book : listOfBooks) {
            System.out.println(book);
        }

        System.out.println(dao.getAllAuthors());
        System.out.println();
        System.out.println(dao.getAllBooks());
        System.out.println();
        System.out.println(dao.getAllBooksAndAuthors());

        Author author3 = null;
        dao.addAuthor(author3);

        System.out.println(dao.getAllBooks());

        dao.updateBookTitle("Harry Potter", "Harry Potter 2");
        dao.updateBookGenre("Harry Potter 2", "Fantasy 2");
        dao.updateBookNumberOfPages("Harry Potter 2", 300);
        dao.updateBookAuthor("Harry Potter 2", author1);

        dao.updateAuthorAge("Rowling", 50);
        dao.updateAuthorFavouriteGenre("Rowling", "");
        dao.updateAuthorName("Rowling", "JK Rowling");

//        dao.deleteBook("Harry Potter");
//        dao.deleteAuthor("JK Rowling");
    }
}