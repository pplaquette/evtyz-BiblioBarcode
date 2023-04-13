package com.evanzheng.bibliobarcode;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

// A class that manages conversions between list<author> and string. Used so we can insert and get books from SQL database.
@SuppressWarnings("WeakerAccess")
public class AuthorConverter {
    //converts author list to string for SQL database insertion, by separating the author's names by \t
    @TypeConverter
    public String authorListToText(List<Author> authors) {
        int numAuthors = authors.size();
        String authorsRaw = "";
        for (int i = 0; i < numAuthors; i++) {
            authorsRaw = authorsRaw.concat(authors.get(i).first);
            if (!authors.get(i).middle.equals("")) {
                authorsRaw = authorsRaw.concat(" ").concat(authors.get(i).middle);
            }
            if (!authors.get(i).last.equals("")) {
                authorsRaw = authorsRaw.concat(" ").concat(authors.get(i).last);
            }
            authorsRaw = authorsRaw.concat("\t");
        }
        return authorsRaw;
    }

    //Converts string back to author list
    @TypeConverter
    public List<Author> authorTextToList(String authorsRaw) {
        List<Author> authors = new ArrayList<>();
        String[] authorListRaw = authorsRaw.split("\t");
        for (String s : authorListRaw) {
            authors.add(new Author(s));
        }
        return authors;
    }
}
