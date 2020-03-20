package com.evanzheng.bibliobarcode;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BookActivity extends AppCompatActivity {

    //Initialize Volley Request Queue
    private RequestQueue requestQueue;

    //Initialize book
    private Book book;

    //Initialize views and inflater
    private Toolbar toolbar;
    private LayoutInflater layoutInflater;
    private ViewGroup viewGroup;
    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //Set up views
        toolbar = findViewById(R.id.toolbar);
        setTitle("Edit Info:");
        setSupportActionBar(toolbar);
        viewGroup = findViewById(R.id.listFields);
        searchButton = findViewById(R.id.searchLocation);
        searchButton.setVisibility(View.INVISIBLE);

        //Set up layout inflater
        layoutInflater = getLayoutInflater();

        // Set up request queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Intent intent = getIntent();
        String code = intent.getStringExtra("barcode");
        loadBook(code);
    }

    //Loads a book based on the code
    protected void loadBook(String isbn) {
        //First API call
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:".concat(isbn);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                //Gets the first item searched (book that corresponds to ISBN) and then gets the url of the selfLink
                String specificUrl = response.getJSONArray("items").getJSONObject(0).getString("selfLink");

                //Second API call
                JsonObjectRequest specificRequest = new JsonObjectRequest(Request.Method.GET, specificUrl, null, response1 -> {
                    try {
                        //Gets info from API
                        JSONObject info = response1.getJSONObject("volumeInfo");
                        //Generates a book based on the info
                        book = new Book(info, isbn);

                        processBook();

                    } catch (JSONException e2) {
                        Log.e("specific book", "Json error");
                    }
                }, error1 -> Log.e("specific book", "List error"));
                requestQueue.add(specificRequest);

            } catch (JSONException e) {
                Log.e("book", "Json error", e);
            }
        }, error -> Log.e("book", "List error", error));
        requestQueue.add(request);
    }

    protected void processBook() {
        showState();
        showCity();
        showYear();
        showPublisher();
        showAuthors();
        showTitle();
        searchButton.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void showTitle() {
        @SuppressLint("InflateParams") View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText("Title:");
        EditText titleEdit = fieldLayout.findViewById(R.id.fieldEdit);
        titleEdit.setText(book.title);
        titleEdit.setHint("Title");
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                book.title = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewGroup.addView(fieldLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showAuthors() {

    }

    private void showPublisher() {
        View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText("Publisher:");
        EditText titleEdit = fieldLayout.findViewById(R.id.fieldEdit);
        titleEdit.setText(book.publisher);
        titleEdit.setHint("Publisher");
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                book.publisher = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewGroup.addView(fieldLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showYear() {
        View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText("Year of Publishing:");
        EditText titleEdit = fieldLayout.findViewById(R.id.fieldEdit);
        titleEdit.setText(book.year);
        titleEdit.setHint("Year");
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                book.year = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewGroup.addView(fieldLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showCity() {
        View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText("City of Publishing: ");
        EditText titleEdit = fieldLayout.findViewById(R.id.fieldEdit);
        titleEdit.setText(book.city);
        titleEdit.setHint("City");
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                book.state = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewGroup.addView(fieldLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showState() {
        View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText("State:");
        EditText titleEdit = fieldLayout.findViewById(R.id.fieldEdit);
        titleEdit.setText(book.state);
        titleEdit.setHint("State");
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                book.state = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        viewGroup.addView(fieldLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    public void searchLocation(View view) {
        if (book.publisher == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword = book.publisher.concat(" publisher location");
        intent.putExtra(SearchManager.QUERY, keyword);
        startActivity(intent);
    }
}
