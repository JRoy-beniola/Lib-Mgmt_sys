package services;

import db.DatabaseManager;
import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonService {

    // View all available genres
    public void viewGenres() {
        String query = "SELECT name FROM genres";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Available Genres:");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View available books by genre using Book model
    public List<Book> getBooksByGenre(String genreName) {
        String query = """
            SELECT b.id, b.title, b.author, b.isbn, b.genre_id
            FROM books b
            JOIN genres g ON b.genre_id = g.id
            JOIN book_copies bc ON b.id = bc.book_id
            WHERE g.name = ? AND bc.available = TRUE
        """;

        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, genreName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getInt("genre_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Display books retrieved by getBooksByGenre
    public void viewBooksByGenre(String genreName) {
        List<Book> books = getBooksByGenre(genreName);
        System.out.printf("Available Books in Genre '%s':%n", genreName);
        for (Book book : books) {
            System.out.printf("- %s by %s (ISBN: %s)%n", book.getTitle(), book.getAuthor(), book.getIsbn());
        }
    }
}
