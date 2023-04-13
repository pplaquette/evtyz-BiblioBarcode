package com.evanzheng.bibliobarcode

import androidx.room.TypeConverter

// A class that manages conversions between list<author> and string. Used so we can insert and get books from SQL database.
class AuthorConverter {
    //converts author list to string for SQL database insertion, by separating the author's names by \t
    @TypeConverter
    fun authorListToText(authors: List<Author>): String {
        val numAuthors = authors.size
        var authorsRaw = ""
        for (i in 0 until numAuthors) {
            authorsRaw = authorsRaw + authors[i].first
            if (authors[i].middle != "") {
                authorsRaw = authorsRaw + " " + authors[i].middle
            }
            if (authors[i].last != "") {
                authorsRaw = authorsRaw + " " + authors[i].last
            }
            authorsRaw = authorsRaw + "\t"
        }
        return authorsRaw
    }

    //Converts string back to author list
    @TypeConverter
    fun authorTextToList(authorsRaw: String): List<Author> {
        val authors: MutableList<Author> = ArrayList()
        val authorListRaw =
            authorsRaw.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (s in authorListRaw) {
            authors.add(Author(s))
        }
        return authors
    }
}