package com.evanzheng.bibliobarcode;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BibliographyActivity extends AppCompatActivity {

    //Set up constants
    ItemTouchHelper itemTouchHelper;
    String style;
    Map<String, Integer> styleButtons;
    BibliographyAdapter adapter;
    ClipboardManager clipboard;


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

        //Set up clipboard
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        //Set up header
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bibliography");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        //Set up recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new BibliographyAdapter(style);

        //Set up recycler view touch listener
        itemTouchHelper = new ItemTouchHelper(new SwipeDeleteListener(adapter) {
            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteItem(position);
            }
        });
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

    void deleteItem(int position) {
        new AlertDialog.Builder(BibliographyActivity.this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> adapter.deleteItem(position))
                .setNegativeButton(android.R.string.no, (dialog, which) -> adapter.reload())
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    public void copyToClipboard(View view) {
        String rawBibliography = "";
        for (Book book : adapter.books) {
            rawBibliography = rawBibliography.concat(book.citation).concat("\n");
        }
        ClipData clip = ClipData.newPlainText("Bibliography", rawBibliography);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied! Remember to italicize your titles in your document!", Toast.LENGTH_LONG).show();
    }


    public void export(View view) {
        String rawBibliography = "";
        for (Book book : adapter.books) {
            rawBibliography = rawBibliography.concat(book.rawFormatCitation).concat("<br>");
        }
        Spanned span = HtmlCompat.fromHtml(rawBibliography, HtmlCompat.FROM_HTML_MODE_LEGACY);
        String htmlBibliography = HtmlCompat.toHtml(span, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);

        //Code below is modified from Piyush Malaviya's saveHtmlFile() at https://stackoverflow.com/questions/31553402/save-string-as-html-file-android
        String path = Objects.requireNonNull(this.getExternalFilesDir(null)).getPath();
        Log.e("path: ", path);

        String fileName = DateFormat.format("dd_MM_yyyy_hh:mm:ss", System.currentTimeMillis()).toString().concat("_Bibliography.html");
        File file = new File(path, fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] data = htmlBibliography.getBytes();
            out.write(data);
            out.close();
            Toast.makeText(this, "Bibliography saved at ".concat(path).concat("/").concat(fileName), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
