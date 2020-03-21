package com.evanzheng.bibliobarcode;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BibliographyActivity extends AppCompatActivity {

    ItemTouchHelper itemTouchHelper;
    String style;
    Map<String, Integer> styleButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bibliography);

        style = "MLA";

        styleButtons = new HashMap<>();
        styleButtons.put("MLA", R.id.MLA);
        styleButtons.put("APA", R.id.APA);
        styleButtons.put("Chicago", R.id.Chicago);
        styleButtons.put("Harvard", R.id.Harvard);

        setButtons(style);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Bibliography");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        BibliographyAdapter adapter = new BibliographyAdapter();

        itemTouchHelper = new ItemTouchHelper(new swipeDelete(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void setMLA(View view) {
        setButtons("MLA");
    }

    public void setAPA(View view) {
        setButtons("APA");
    }

    public void setChicago(View view) {
        setButtons("Chicago");
    }

    public void setHarvard(View view) {
        setButtons("Harvard");
    }

    private void setButtons(String style) {
        String oldStyle = this.style;
        this.style = style;

        processButton(oldStyle, false);
        processButton(style, true);


    }

    private void processButton(String style, boolean focus) {
        Integer id = styleButtons.get(style);
        if (id == null) {
            return;
        }
        TextView targetButton = findViewById(id);
        int textC;
        int bgID;

        if (focus) {
            textC = getResources().getColor(R.color.colorAccent);
            bgID = R.drawable.pill_activated;
        } else {
            textC = getResources().getColor(R.color.darkText);
            bgID = R.drawable.pill_deactivated;
        }

        assert targetButton != null;
        targetButton.setTextColor(textC);
        targetButton.setBackgroundResource(bgID);
    }

}
