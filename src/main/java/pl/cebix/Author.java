package pl.cebix;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Author {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private Integer age;
    private String favouriteGenre;
    @OneToMany(mappedBy = "author")
    List<Book> books;

    public Author() {
    }

    public Author(long id, String name, Integer age, String favouriteGenre) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.favouriteGenre = favouriteGenre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getFavouriteGenre() {
        return favouriteGenre;
    }

    public void setFavouriteGenre(String favouriteGenre) {
        this.favouriteGenre = favouriteGenre;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", favouriteGenre='" + favouriteGenre + '\'' +
                ", books=" + books +
                '}';
    }
}
