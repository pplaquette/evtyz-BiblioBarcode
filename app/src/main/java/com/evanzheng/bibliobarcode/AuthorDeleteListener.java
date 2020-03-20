package com.evanzheng.bibliobarcode;

//A custom implementation of an OnClickListener with an id field.

import android.view.View;

abstract class AuthorDeleteListener implements View.OnClickListener {
    int id;

    AuthorDeleteListener(int id) {
        this.id = id;
    }

}
