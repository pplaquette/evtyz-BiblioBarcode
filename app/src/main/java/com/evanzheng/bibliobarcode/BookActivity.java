package com.evanzheng.bibliobarcode;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookActivity extends AppCompatActivity {

    //Initialize Volley Request Queue
    private RequestQueue requestQueue;

    //Initialize book
    private Book book;
    private Map<Integer, Author> authors;
    private int nextAuthorId;
    private boolean isNew;

    //Initialize views
    private LayoutInflater layoutInflater;
    private ViewGroup viewGroup;
    private Button searchButton;
    private ViewGroup fieldAuthorEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //Set up views
        FloatingActionButton saveButton = findViewById(R.id.addToBibliography);
        saveButton.setOnClickListener(v -> addToBibliography());

        //Initialize views and inflater
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        if (code == null) {
            String isbn = intent.getStringExtra("isbn");
            book = MainActivity.database.bookDao().loadBook(isbn);
            isNew = false;
            processAuthors();
            processBook();
        } else {
            isNew = true;
            loadBook(code);
        }
    }

    //Loads a book based on the code
    protected void loadBook(String isbn) {
        //First API call
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
                .concat(isbn);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                //Gets the first item searched (book that corresponds to ISBN) and then gets the url of the selfLink
                String specificUrl = response
                        .getJSONArray("items")
                        .getJSONObject(0).getString("selfLink");

                //Second API call
                JsonObjectRequest specificRequest = new JsonObjectRequest(Request.Method.GET, specificUrl, null, response1 -> {
                    try {
                        //Gets info from API
                        JSONObject info = response1
                                .getJSONObject("volumeInfo");
                        //Generates a book based on the info
                        book = new Book(info, isbn);
                        processAuthors();
                        processBook();
                    } catch (JSONException e2) {
                        Toast.makeText(this, "We couldn't find this book. Error Code 2A", Toast.LENGTH_LONG).show();
                        returnToCamera();
                    }
                }, error1 ->
                {
                    Toast.makeText(this, "We couldn't find this book. Error Code 2B", Toast.LENGTH_LONG).show();
                    returnToCamera();
                });

                requestQueue.add(specificRequest);

            } catch (JSONException e) {
                Toast.makeText(this, "We couldn't find this book. Error Code 1A", Toast.LENGTH_LONG).show();
                returnToCamera();
            }
        }, error ->
        {
            Toast.makeText(this, "We couldn't find this book. Error Code 1B", Toast.LENGTH_SHORT).show();
            returnToCamera();
        });

        requestQueue.add(request);
    }

    protected void processAuthors() {
        authors = new HashMap<>();
        if (book.authors != null) {
            for (int i = 0; i < book.authors.size(); i++) {
                authors.put(i, book.authors.get(i));
            }
        }
    }

    //Display the book's contents
    protected void processBook() {
        showState();
        showCity();
        showYear();
        showPublisher();
        showAuthors();
        showTitle();
        searchButton.setVisibility(View.VISIBLE);
    }

    //Shows the title fields
    private void showTitle() {
        @SuppressLint("InflateParams") View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText(R.string.titledesc);
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

    //Shows the author fields
    private void showAuthors() {
        @SuppressLint("InflateParams") View authorLayout = layoutInflater.inflate(R.layout.field_author, null);
        TextView description = authorLayout.findViewById(R.id.fieldDesc);
        description.setText(R.string.authordesc);
        fieldAuthorEdit = authorLayout.findViewById(R.id.fieldAuthorEdit);

        viewGroup.addView(authorLayout, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        nextAuthorId = book.authors.size();

        //Based on the number of authors:
        for (int i = 0; i < nextAuthorId; i++) {

            String first = book.authors.get(i).first;
            String middle = book.authors.get(i).middle;
            String last = book.authors.get(i).last;

            @SuppressLint("InflateParams") View authorAdd = layoutInflater.inflate(R.layout.author_add, null);
            EditText firstName = authorAdd.findViewById(R.id.add_first);
            firstName.setText(first);
            firstName.addTextChangedListener(new AuthorTextWatcher(i) {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Objects.requireNonNull(authors.get(id)).first = s.toString();
                }
            });

            EditText middleName = authorAdd.findViewById(R.id.add_middle);
            middleName.setText(middle);
            middleName.addTextChangedListener(new AuthorTextWatcher(i) {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Objects.requireNonNull(authors.get(id)).middle = s.toString();
                }
            });


            EditText lastName = authorAdd.findViewById(R.id.add_last);
            lastName.setText(last);
            lastName.addTextChangedListener(new AuthorTextWatcher(i) {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Objects.requireNonNull(authors.get(id)).last = s.toString();
                }
            });
            fieldAuthorEdit.addView(authorAdd, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            TextView deleteButton = authorAdd.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new AuthorDeleteListener(i) {
                @Override
                public void onClick(View v) {
                    authors.remove(id);
                    fieldAuthorEdit.removeView(authorAdd);
                }
            });
        }
    }

    //Show publisher field
    private void showPublisher() {
        @SuppressLint("InflateParams") View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText(R.string.publisherdesc);
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

    //Show year of publication field
    private void showYear() {
        @SuppressLint("InflateParams") View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText(R.string.yeardesc);
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

    //Show city of publication field
    private void showCity() {
        @SuppressLint("InflateParams") View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText(R.string.citydesc);
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

    //Show state of publication field
    private void showState() {
        @SuppressLint("InflateParams") View fieldLayout = layoutInflater.inflate(R.layout.field, null);
        TextView description = fieldLayout.findViewById(R.id.fieldDesc);
        description.setText(R.string.statedesc);
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

    //Search for location of publisher
    public void searchLocation(View view) {
        if (book.publisher == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword = book.publisher.concat(" publisher location");
        intent.putExtra(SearchManager.QUERY, keyword);
        startActivity(intent);
    }

    //Add an author
    public void addAuthor(View v) {
        @SuppressLint("InflateParams") View authorAdd = layoutInflater.inflate(R.layout.author_add, null);

        Author newAuthor = new Author();

        //Add to hashmap
        authors.put(nextAuthorId, newAuthor);

        EditText firstName = authorAdd.findViewById(R.id.add_first);
        firstName.addTextChangedListener(new AuthorTextWatcher(nextAuthorId) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Objects.requireNonNull(authors.get(id)).first = s.toString();
            }
        });

        EditText middleName = authorAdd.findViewById(R.id.add_middle);
        middleName.addTextChangedListener(new AuthorTextWatcher(nextAuthorId) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Objects.requireNonNull(authors.get(id)).middle = s.toString();
            }
        });


        EditText lastName = authorAdd.findViewById(R.id.add_last);
        lastName.addTextChangedListener(new AuthorTextWatcher(nextAuthorId) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Objects.requireNonNull(authors.get(id)).last = s.toString();
            }
        });

        fieldAuthorEdit.addView(authorAdd, fieldAuthorEdit.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView deleteButton = authorAdd.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new AuthorDeleteListener(nextAuthorId) {
            @Override
            public void onClick(View v) {
                authors.remove(id);
                fieldAuthorEdit.removeView(authorAdd);
            }
        });

        nextAuthorId++;

    }

    private void addToBibliography() {
        book.authorMapToList(authors);
        if (isNew) {
            MainActivity.database.bookDao().insertBook(book);
        } else {
            MainActivity.database.bookDao().updateBook(book);
        }
        Intent leaveIntent = new Intent(this, BibliographyActivity.class);
        startActivity(leaveIntent);
    }

    private void returnToCamera() {
        Intent returnIntent = new Intent(this, MainActivity.class);
        startActivity(returnIntent);
    }
}
