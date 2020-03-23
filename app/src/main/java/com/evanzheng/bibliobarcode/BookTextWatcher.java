package com.evanzheng.bibliobarcode;

import android.text.Editable;
import android.text.TextWatcher;

// A custom implementation of a TextWatcher with a key (so we can keep track of what information it's editing)

public abstract class BookTextWatcher implements TextWatcher {

    String key;

    BookTextWatcher(String key) {
        this.key = key;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    @Override
    public void afterTextChanged(Editable s) {
    }
}
