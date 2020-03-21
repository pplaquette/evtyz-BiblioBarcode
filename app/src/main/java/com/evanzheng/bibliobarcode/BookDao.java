package com.evanzheng.bibliobarcode;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//A DAO that manages queries to our SQL database
@Dao
public interface BookDao {
    @Insert
    void insertBook(Book book);

    @Update
    void updateBook(Book book);

    @Query("DELETE FROM books WHERE isbn=:isbn")
    void deleteBook(String isbn);

    @Query("SELECT isbn FROM books")
    List<String> loadISBN();

    @Query("SELECT * FROM books")
    List<Book> loadBookSources();

    @Query("SELECT * FROM books WHERE isbn =:isbn")
    Book loadBook(String isbn);
}
