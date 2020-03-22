package com.evanzheng.bibliobarcode;

import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
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
public class Book implements Comparable<Book> {

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

    @Ignore
    public String citation;

    @Ignore
    public String rawFormatCitation;

    public Book(@NotNull String isbn, String title, List<Author> authors, String publisher, String year, String city, String state) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.year = year;
        this.city = city;
        this.state = state;
    }

    //Constructor in Book
    @Ignore
    Book(JSONObject info, @NotNull String isbn) {
        this.isbn = isbn;

        try {
            title = info.getString("title");
        } catch (JSONException e) {
            title = "";
        }

        authors = new ArrayList<>();
        try {
            JSONArray rawAuthors = info.getJSONArray("authors");
            int numAuthors = rawAuthors.length();
            for (int i = 0; i < numAuthors; i++) {
                String name = rawAuthors.getString(i);
                authors.add(new Author(name));
            }
            Collections.sort(authors);
        } catch (JSONException ignored) {
        }

        try {
            publisher = info.getString("publisher");
        } catch (JSONException e) {
            publisher = "";

        }

        try {
            year = info.getString("publishedDate");
            year = year.substring(0, Math.min(year.length(), 4));
        } catch (JSONException e) {
            year = "";
        }

        city = "";
        state = "";
    }

    void infoMapToList(Map<String, String> bookInfo) {
        title = bookInfo.get("title");
        publisher = bookInfo.get("publisher");
        year = bookInfo.get("year");
        city = bookInfo.get("city");
        state = bookInfo.get("state");
    }

    //Converts hashmap to author list and string
    void authorMapToList(Map<Integer, Author> authorMap) {
        authors = new ArrayList<>();
        for (int i : authorMap.keySet()) {
            authors.add(authorMap.get(i));
        }
        Collections.sort(authors);
    }


    void cite(String style) {
        rawFormatCitation = "";
        citation = "";
        String authorCite;
        String titleCite;
        String publisherCite;
        String yearCite;
        String cityCite;
        String stateCite;


        switch (style) {
            case "MLA":
                authorCite = "";
                if (authors.size() != 0) {
                    authorCite = authorCite.concat(authors.get(0).formattedName());
                    for (int i = 1; i < authors.size(); i++) {
                        if (i == authors.size() - 1) {
                            authorCite = authorCite.concat(", and ");
                        } else {
                            authorCite = authorCite.concat(", ");
                        }
                        authorCite = authorCite.concat(authors.get(i).fullName());
                    }
                    authorCite = authorCite.concat(". ");
                }

                titleCite = title.concat(". ");

                if (publisher.equals("")) {
                    publisherCite = "n.p., ";
                } else {
                    publisherCite = publisher.concat(", ");
                }

                if (year.equals("")) {
                    yearCite = "n.d.";
                } else {
                    yearCite = year.concat(".");
                }

                citation = authorCite
                        .concat(titleCite)
                        .concat(publisherCite)
                        .concat(yearCite);

                rawFormatCitation = authorCite
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(publisherCite)
                        .concat(yearCite);
                break;

            case "APA":
                authorCite = "";
                for (int i = 0; i < authors.size(); i++) {
                    authorCite = authorCite.concat(authors.get(i).formattedInitializedName());
                    if (i != authors.size() - 1) {
                        authorCite = authorCite.concat(", ");
                    }
                    if (i == authors.size() - 2) {
                        authorCite = authorCite.concat(" & ");
                    }
                }

                yearCite = " (";
                if (year.equals("")) {
                    yearCite = yearCite.concat("n.d.");
                } else {
                    yearCite = yearCite.concat(year);
                }
                yearCite = yearCite.concat("). ");

                titleCite = title.concat(". ");

                if (city.equals("") || state.equals("")) {
                    cityCite = "N.p.: ";
                    stateCite = "";
                } else {
                    cityCite = city.concat(", ");
                    stateCite = state.concat(": ");
                }

                if (publisher.equals("")) {
                    publisherCite = "n.p.";
                } else {
                    publisherCite = publisher.concat(".");
                }

                rawFormatCitation = authorCite
                        .concat(yearCite)
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(cityCite)
                        .concat(stateCite)
                        .concat(publisherCite);

                citation = authorCite
                        .concat(yearCite)
                        .concat(titleCite)
                        .concat(cityCite)
                        .concat(stateCite)
                        .concat(publisherCite);

                break;
            case "Chicago":
                authorCite = "";
                if (authors.size() != 0) {
                    authorCite = authorCite.concat(authors.get(0).formattedName());
                    for (int i = 1; i < authors.size(); i++) {
                        if (i == authors.size() - 1) {
                            authorCite = authorCite.concat(", and ");
                        } else {
                            authorCite = authorCite.concat(", ");
                        }
                        authorCite = authorCite.concat(authors.get(i).fullName());
                    }
                    authorCite = authorCite.concat(". ");
                }

                titleCite = title.concat(". ");

                if (city.equals("")) {
                    cityCite = "N.p.: ";
                } else {
                    cityCite = city.concat(": ");
                }

                if (publisher.equals("")) {
                    publisherCite = "n.p., ";
                } else {
                    publisherCite = publisher.concat(", ");
                }

                if (year.equals("")) {
                    yearCite = "n.d.";
                } else {
                    yearCite = year.concat(".");
                }

                rawFormatCitation = authorCite
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(cityCite)
                        .concat(publisherCite)
                        .concat(yearCite);

                citation = authorCite
                        .concat(titleCite)
                        .concat(cityCite)
                        .concat(publisherCite)
                        .concat(yearCite);

                break;
            case "Harvard":
                authorCite = "";
                for (int i = 0; i < authors.size(); i++) {
                    authorCite = authorCite.concat(authors.get(i).formattedInitializedName()).concat(", ");
                    if (i == authors.size() - 2) {
                        authorCite = authorCite.concat(" and ");
                    }
                }

                if (year.equals("")) {
                    yearCite = "n.d. ";
                } else {
                    yearCite = year.concat(". ");
                }

                titleCite = title.concat(". ");

                if (city.equals("")) {
                    cityCite = "N.p.: ";
                } else {
                    cityCite = city.concat(": ");
                }

                if (publisher.equals("")) {
                    publisherCite = "n.p.";
                } else {
                    publisherCite = publisher.concat(".");
                }

                rawFormatCitation = authorCite
                        .concat(yearCite)
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(cityCite)
                        .concat(publisherCite);

                citation = authorCite
                        .concat(yearCite)
                        .concat(titleCite)
                        .concat(cityCite)
                        .concat(publisherCite);

                break;
            default:
                rawFormatCitation = title.concat(" Citation error");
                break;
        }
    }

    Spanned getCitation() {
        return HtmlCompat.fromHtml(rawFormatCitation, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    @Override
    public int compareTo(Book book) {
        return citation.compareTo(book.citation);
    }
}
