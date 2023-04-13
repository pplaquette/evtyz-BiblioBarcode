package com.evanzheng.bibliobarcode

//This is an author object
class Author : Comparable<Author> {
    //Authors have three fields: first, middle, and last names
    @JvmField
    var first: String
    @JvmField
    var middle: String
    @JvmField
    var last: String

    //If we're creating an empty author, they can all be empty strings
    constructor() {
        first = ""
        middle = ""
        last = ""
    }

    //If we're creating an author based on a full name, we must parse it into first, middle, and last names
    constructor(name: String) {
        middle = ""
        last = ""

        //This is the way that we parse it: first name, then last name, then middle name optionally.
        val nameOrder = name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        first = nameOrder[0]
        val numNames = nameOrder.size
        if (numNames == 2) {
            middle = ""
            last = nameOrder[1]
        } else {
            middle = ""
            for (i in 1 until numNames - 1) {
                middle = middle + nameOrder[i]
                if (i != numNames - 2) {
                    middle = "$middle "
                }
            }
            last = nameOrder[numNames - 1]
            first = first.trim { it <= ' ' }
            middle = middle.trim { it <= ' ' }
            last = last.trim { it <= ' ' }
        }
    }

    //Returns the full name of the author
    fun fullName(): String {
        var name = first
        if (middle != "") {
            name = name + " " + middle[0].toString() + ". "
        } else {
            name = "$name "
        }
        name = name + last
        return name
    }

    //Returns the name of the author "First Middle Last" in format "Last, First M.".
    fun formattedName(): String {
        if (last == "") {
            return first
        } else if (middle == "") {
            return last + ", " + first
        } else {
            return last +" , " + first + " " + middle[0].toString() + "."
        }
    }

    //Returns the name of the author "First Middle Last" in format "Last, F.M."
    fun formattedInitializedName(): String {
        if (last == "") {
            return first
        } else if (middle == "") {
            return last +", " + first[0].toString() + "."
        } else {
            return last  +", " + first[0].toString() + "." + middle[0].toString() + "."
            return last  +", " + first[0].toString() + "." + middle[0].toString() + "."
        }
    }

    //Does the author have no name?
    val isNotEmpty: Boolean
        get() = first != "" || middle != "" || last != ""

    //Implements a comparable interface, compares last names by alphabetical order
    override fun compareTo(author: Author): Int {
        return last.compareTo(author.last)
    }
}