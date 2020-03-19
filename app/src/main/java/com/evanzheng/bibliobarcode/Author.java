package com.evanzheng.bibliobarcode;

public class Author {
    public String first;
    public String middle;
    public String last;

    Author(String name) {
        String[] nameOrder = name.split(" ");
        this.first = nameOrder[0];
        int numNames = nameOrder.length;
        if (numNames == 2) {
            this.last = nameOrder[1];
        } else {
            this.middle = "";
            for (int i = 1; i < numNames - 1; i++) {
                this.middle.concat(nameOrder[i]);
                if (i != numNames - 1) {
                    this.middle.concat(" ");
                }
            }
            this.last = nameOrder[numNames - 1];
        }
    }
}
