package com.evanzheng.bibliobarcode;

import android.text.Editable;
import android.text.TextWatcher;

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
