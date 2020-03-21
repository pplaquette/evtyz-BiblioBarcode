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

    //Set up constants
    ItemTouchHelper itemTouchHelper;
    String style;
    Map<String, Integer> styleButtons;
    BibliographyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bibliography);

        //Default style
        //TODO: Implement Shared Preferences
        style = "MLA";

        //Make a hashmap between styles and buttons
        styleButtons = new HashMap<>();
        styleButtons.put("MLA", R.id.MLA);
        styleButtons.put("APA", R.id.APA);
        styleButtons.put("Chicago", R.id.Chicago);
        styleButtons.put("Harvard", R.id.Harvard);


        //Set up header
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bibliography");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        //Set up recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new BibliographyAdapter(style);

        //Set up recycler view touch listener
        itemTouchHelper = new ItemTouchHelper(new swipeDelete(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Link everything together
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setButtons(style);
    }

    //Four methods below are linked to onClick in layout files
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

    //When a button is pressed
    private void setButtons(String style) {
        String oldStyle = this.style;
        this.style = style;
        adapter.style = this.style;
        adapter.reload();

        //Defocus the old button
        processButton(oldStyle, false);

        //Focus the new button
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

        //Change colours based on whether it's supposed to be focusing/defocusing
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

    //TODO Export function

}
