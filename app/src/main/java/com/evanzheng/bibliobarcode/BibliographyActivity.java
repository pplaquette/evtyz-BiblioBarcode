package com.evanzheng.bibliobarcode;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class BibliographyActivity extends AppCompatActivity {

    ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bibliography);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bibliography");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        BibliographyAdapter adapter = new BibliographyAdapter();

        itemTouchHelper = new ItemTouchHelper(new swipeDelete(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
