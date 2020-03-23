package com.evanzheng.bibliobarcode;


//This is an author object

class Author implements Comparable<Author> {

    //Authors have three fields: first, middle, and last names
    String first;
    String middle;
    String last;

    //If we're creating an empty author, they can all be empty strings
    Author() {
        first = "";
        middle = "";
        last = "";
    }

    //If we're creating an author based on a full name, we must parse it into first, middle, and last names
    Author(String name) {
        middle = "";
        last = "";

        //This is the way that we parse it: first name, then last name, then middle name optionally.
        String[] nameOrder = name.split(" ");
        first = nameOrder[0];
        int numNames = nameOrder.length;
        if (numNames == 2) {
            middle = "";
            last = nameOrder[1];
        } else {
            middle = "";
            for (int i = 1; i < numNames - 1; i++) {
                middle = middle.concat(nameOrder[i]);
                if (i != numNames - 2) {
                    middle = middle.concat(" ");
                }
            }
            last = nameOrder[numNames - 1];
            first = first.trim();
            middle = middle.trim();
            last = last.trim();
        }
    }

    //Returns the full name of the author

    String fullName() {
        String name = first;
        if (!middle.equals("")) {
            name = name
                    .concat(" ")
                    .concat(String.valueOf(middle.charAt(0)))
                    .concat(". ");
        } else {
            name = name.concat(" ");
        }
        name = name.concat(last);
        return name;
    }

    //Returns the name of the author "First Middle Last" in format "Last, First M.".
    String formattedName() {
        if (last.equals("")) {
            return first;
        } else if (middle.equals("")) {
            return last
                    .concat(", ")
                    .concat(first);
        } else {
            return last
                    .concat(", ")
                    .concat(first)
                    .concat(" ")
                    .concat(String.valueOf(middle.charAt(0)))
                    .concat(".");
        }
    }

    //Returns the name of the author "First Middle Last" in format "Last, F.M."
    String formattedInitializedName() {
        if (last.equals("")) {
            return first;
        } else if (middle.equals("")) {
            return last
                    .concat(", ")
                    .concat(String.valueOf(first.charAt(0)))
                    .concat(".");
        } else {
            return last
                    .concat(", ")
                    .concat(String.valueOf(first.charAt(0)))
                    .concat(".")
                    .concat(String.valueOf(middle.charAt(0)))
                    .concat(".");
        }
    }

    //Does the author have no name?
    boolean isNotEmpty() {
        return !first.equals("") || !middle.equals("") || !last.equals("");
    }

    //Implements a comparable interface, compares last names by alphabetical order
    @Override
    public int compareTo(Author author) {
        return last.compareTo(author.last);
    }
}
