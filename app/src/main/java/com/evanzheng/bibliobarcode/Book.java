package com.evanzheng.bibliobarcode;

import android.text.Html;
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

    @ColumnInfo(name = "description")
    public String description;

    @Ignore
    public String citation;

    @Ignore
    public Spanned formattedCitation;

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

        try {
            description = info.getString("description");
        } catch (JSONException e) {
            description = "";
        }

        city = "";
        state = "";
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
        formattedCitation = HtmlCompat.fromHtml("", HtmlCompat.FROM_HTML_MODE_LEGACY);
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

                formattedCitation = HtmlCompat.fromHtml(authorCite
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(publisherCite)
                        .concat(yearCite), HtmlCompat.FROM_HTML_MODE_LEGACY);
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

                formattedCitation = HtmlCompat.fromHtml(authorCite
                        .concat(yearCite)
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(cityCite)
                        .concat(stateCite)
                        .concat(publisherCite), HtmlCompat.FROM_HTML_MODE_LEGACY);

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

                formattedCitation = HtmlCompat.fromHtml(authorCite
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(cityCite)
                        .concat(publisherCite)
                        .concat(yearCite), HtmlCompat.FROM_HTML_MODE_LEGACY);

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

                formattedCitation = HtmlCompat.fromHtml(authorCite
                        .concat(yearCite)
                        .concat("<i>")
                        .concat(titleCite)
                        .concat("</i>")
                        .concat(cityCite)
                        .concat(publisherCite), HtmlCompat.FROM_HTML_MODE_LEGACY);

                citation = authorCite
                        .concat(yearCite)
                        .concat(titleCite)
                        .concat(cityCite)
                        .concat(publisherCite);

                break;
            default:
                formattedCitation = Html.fromHtml(title.concat(" Citation error"));
                break;
        }
    }

    Spanned getCitation() {
        return formattedCitation;
    }

    @Override
    public int compareTo(Book book) {
        return citation.compareTo(book.citation);
    }
}
