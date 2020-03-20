package com.evanzheng.bibliobarcode;

import android.text.Editable;
import android.text.TextWatcher;

//A custom implementation of an TextWatcher with an id field.

public abstract class AuthorTextWatcher implements TextWatcher {

    int id;

    AuthorTextWatcher(int id) {
        this.id = id;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    @Override
    public void afterTextChanged(Editable s) {
    }
}
