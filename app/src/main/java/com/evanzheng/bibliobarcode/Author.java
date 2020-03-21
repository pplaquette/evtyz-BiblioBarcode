package com.evanzheng.bibliobarcode;

class Author implements Comparable<Author> {
    String first;
    String middle;
    String last;

    Author() {
        first = "";
        middle = "";
        last = "";
    }

    //Parses a name into an author object
    Author(String name) {
        middle = "";
        last = "";

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

    //Implements a comparable interface, compares last names by alphabetical order
    @Override
    public int compareTo(Author author) {
        return last.compareTo(author.last);
    }
}
