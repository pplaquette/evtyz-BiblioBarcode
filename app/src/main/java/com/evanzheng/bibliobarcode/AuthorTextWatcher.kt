package com.evanzheng.bibliobarcode

import android.text.Editable
import android.text.TextWatcher

//A custom implementation of an TextWatcher with an id field.
internal abstract class AuthorTextWatcher(val id: Int) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}