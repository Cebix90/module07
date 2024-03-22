package pl.cebix;

public class Main {
    public static void main(String[] args) {
        LibraryDAO dao = new LibraryDAO();

        Author author = new Author();
        author.setName("Mateusz");
        author.setAge(33);
        author.setFavouriteGenre("Fantasy");
    }
}