package com.evanzheng.bibliobarcode;


import android.view.View;

abstract class AuthorDeleteListener implements View.OnClickListener {
    int id;

    AuthorDeleteListener(int id) {
        this.id = id;
    }

}
