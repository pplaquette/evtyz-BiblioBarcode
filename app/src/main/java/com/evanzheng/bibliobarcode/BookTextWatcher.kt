package com.evanzheng.bibliobarcode

import android.text.Editable
import android.text.TextWatcher

// A custom implementation of a TextWatcher with a key (so we can keep track of what information it's editing)
internal abstract class BookTextWatcher(val key: String) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}