package com.evanzheng.bibliobarcode;

class Author implements Comparable<Author> {
    String first;
    String middle;
    String last;

    Author() {
        this.first = "";
        this.middle = "";
        this.last = "";
    }

    Author(String name) {
        String[] nameOrder = name.split(" ");
        this.first = nameOrder[0];
        int numNames = nameOrder.length;
        if (numNames == 2) {
            this.middle = "";
            this.last = nameOrder[1];
        } else {
            this.middle = "";
            for (int i = 1; i < numNames - 1; i++) {
                this.middle = this.middle.concat(nameOrder[i]);
                if (i != numNames - 1) {
                    this.middle = this.middle.concat(" ");
                }
            }
            this.last = nameOrder[numNames - 1];
        }
    }

    @Override
    public int compareTo(Author author) {
        return this.last.compareTo(author.last);
    }
}
