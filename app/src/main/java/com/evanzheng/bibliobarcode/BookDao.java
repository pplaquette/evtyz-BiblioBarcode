package com.evanzheng.bibliobarcode;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//A DAO that manages queries to our SQL database
@Dao
public interface BookDao {

    //Inserting a book into the database
    @Insert
    void insertBook(Book book);

    //Updating an existing book in the database
    @Update
    void updateBook(Book book);

    //Deleting a book with a certain ISBN
    @Query("DELETE FROM books WHERE isbn=:isbn")
    void deleteBook(String isbn);

    //Getting list of all ISBNs currently in database
    @Query("SELECT isbn FROM books")
    List<String> loadISBN();

    //Getting list of all books currently in database
    @Query("SELECT * FROM books")
    List<Book> loadBookSources();

    //Selecting a book with a particular ISBN from database
    @Query("SELECT * FROM books WHERE isbn =:isbn")
    Book loadBook(String isbn);
}
