package pl.cebix;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        LibraryDAO dao = new LibraryDAO();

        Author author = new Author();
        author.setName("Mateusz");
        author.setAge(33);
        author.setFavouriteGenre("Fantasy");

        dao.addAuthor(author);

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

        Book book2 = new Book();
        book2.setTitle("Malowany Czlowiek");
        book2.setGenre("Fantasy");
        book2.setNumberOfPages(400);

        Book book3 = new Book();
        book3.setTitle("Harry Potter");
        book3.setGenre("Fantasy");
        book3.setNumberOfPages(350);

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

        dao.deleteBook("Harry Potter");
        dao.deleteAuthor("Rowling");
    }
}