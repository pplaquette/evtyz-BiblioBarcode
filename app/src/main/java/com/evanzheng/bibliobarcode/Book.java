package com.evanzheng.bibliobarcode;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@SuppressWarnings("WeakerAccess")
@Entity(tableName = "books")
@TypeConverters(AuthorConverter.class)
public class Book {

    @PrimaryKey
    @NonNull
    public String isbn;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "authors")
    public List<Author> authors;

    @ColumnInfo(name = "publisher")
    public String publisher;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "city")
    public String city;

    @ColumnInfo(name = "state")
    public String state;

    @ColumnInfo(name = "description")
    public String description;


    public Book(@NotNull String isbn, String title, List<Author> authors, String publisher, String year, String city, String state, String description) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.year = year;
        this.city = city;
        this.state = state;
        this.description = description;
    }

    //Constructor in Book
    @Ignore
    Book(JSONObject info, @NotNull String isbn) {
        this.isbn = isbn;

        try {
            this.title = info.getString("title");
        } catch (JSONException e) {
            this.title = null;
        }

        this.authors = new ArrayList<>();
        try {
            JSONArray rawAuthors = info.getJSONArray("authors");
            int numAuthors = rawAuthors.length();
            for (int i = 0; i < numAuthors; i++) {
                String name = rawAuthors.getString(i);
                authors.add(new Author(name));
            }
            Collections.sort(authors);
        } catch (JSONException e) {
            this.authors = null;
        }

        try {
            this.publisher = info.getString("publisher");
        } catch (JSONException e) {
            this.publisher = null;
        }

        try {
            this.year = info.getString("publishedDate");
            this.year = this.year.substring(0, Math.min(this.year.length(), 4));
        } catch (JSONException e) {
            this.year = null;
        }

        try {
            this.description = info.getString("description");
        } catch (JSONException e) {
            this.description = null;
        }

        this.city = null;
        this.state = null;
    }

    //Converts hashmap to author list and string
    void authorMapToList(Map<Integer, Author> authorMap) {
        this.authors = new ArrayList<>();
        for (int i : authorMap.keySet()) {
            this.authors.add(authorMap.get(i));
        }
        Collections.sort(authors);
    }
}
