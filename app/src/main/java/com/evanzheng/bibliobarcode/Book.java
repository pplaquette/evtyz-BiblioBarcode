package com.evanzheng.bibliobarcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


class Book {

    // Fields in Book
    List<Author> authors;
    String year;
    String title;
    String publisher;
    String city;
    String state;
    String description;
    String isbn;

    //Constructor in Book
    Book(JSONObject info, String isbn) {
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
}
