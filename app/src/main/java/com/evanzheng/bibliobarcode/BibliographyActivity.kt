package com.evanzheng.bibliobarcode;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class BibliographyActivity extends AppCompatActivity {

    //Set up constants

    //Manages citation style
    private String style;

    //Maps style to appropriate button
    private Map<String, Integer> styleButtons;

    //Manages the recycler view
    private BibliographyAdapter adapter;

    //For copying and pasting
    private ClipboardManager clipboard;

    //To remember styles from previous session, and to remember if a tutorial has been run
    private SharedPreferences sharedPref;

    //Export buttons initialization
    private FloatingActionButton exportButton;
    private FloatingActionButton copyButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bibliography);

        //Set up buttons
        exportButton = findViewById(R.id.export_button);
        copyButton = findViewById(R.id.copy_button);


        //Load up previous style, or set to MLA if none found
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        style = sharedPref.getString("style", "MLA");

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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Set up recycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new BibliographyAdapter(style, clipboard);

        //Set up recycler view swipe listener
        //This is for swipe-to-delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDeleteListener(adapter) {
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                //int position = viewHolder.getAdapterPosition(); PPL
                int position = viewHolder.getBindingAdapterPosition();
                deleteItem(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Link everything together
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setButtons(style);


        //Check to see if a tutorial needs to be run
        boolean ranBefore = sharedPref.getBoolean("biblio", false);

        if (!ranBefore) {
            runTutorial();
        }
    }

    //Runs the tutorial
    private void runTutorial() {
        ShowcaseConfig tutorialConfig = new ShowcaseConfig();
        tutorialConfig.setDelay(500);


        LinearLayout placeholder = findViewById(R.id.placeholder);
        MaterialShowcaseSequence tutorial = new MaterialShowcaseSequence(this, "smthdif5");

        tutorial.setConfig(tutorialConfig);
        tutorial.addSequenceItem(placeholder, "Edit a citation by tapping, copy by long-pressing, and delete by swiping", "OKAY");
        tutorial.addSequenceItem(copyButton, "Copy your bibliography to your clipboard using this button", "OKAY");
        tutorial.addSequenceItem(exportButton, "Save your bibliography to an HTML file using this button", "OKAY");
        tutorial.start();

        //Tutorial will never be run again
        sharedPref.edit().putBoolean("biblio", true).apply();
    }

    //Four methods below are linked to onClick in layout files
    @SuppressWarnings("unused")
    public void setMLA(View view) {
        setButtons("MLA");
    }

    @SuppressWarnings("unused")
    public void setAPA(View view) {
        setButtons("APA");
    }

    @SuppressWarnings("unused")
    public void setChicago(View view) {
        setButtons("Chicago");
    }

    @SuppressWarnings("unused")
    public void setHarvard(View view) {
        setButtons("Harvard");
    }

    //When a button is pressed
    private void setButtons(String style) {
        //Change the style to correspond with button
        String oldStyle = this.style;
        this.style = style;
        //Save style
        sharedPref.edit().putString("style", style).apply();

        //Send the style to the adapter to reload the citations
        adapter.style = this.style;
        adapter.reload();

        //Defocus the old button
        processButton(oldStyle, false);

        //Focus the new button
        processButton(style, true);
    }

    //Manages bg color and text color changes for each button
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
            textC = this.getColor(R.color.colorWhite);   //PPL
            bgID = R.drawable.pill_activated;
        } else {
            textC = this.getColor(R.color.greyText); //PPL
            bgID = R.drawable.pill_deactivated;
        }

        assert targetButton != null;
        targetButton.setTextColor(textC);
        targetButton.setBackgroundResource(bgID);
    }

    // Prompts the user if they are sure they want to delete an entry
    private void deleteItem(int position) {
        new AlertDialog.Builder(BibliographyActivity.this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this entry?")
                // Deletes entry from adapter if they say yes
                .setPositiveButton(android.R.string.yes, (dialog, which) -> adapter.deleteItem(position))
                //Otherwise, reset the recycler view to before they swiped
                .setNegativeButton(android.R.string.no, (dialog, which) -> adapter.reload())
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    // Copies citations to a clipboard
    @SuppressWarnings("unused")
    public void copyToClipboard(View view) {

        //Plain text citation
        String rawBibliography = "";

        //HTML citation
        String formattedBibliography = "";

        //Builds each bibliography (we use adapter.books because it is alphabetically sorted)
        for (Book book : adapter.books) {
            rawBibliography = rawBibliography.concat(book.citation).concat("\n");
            formattedBibliography = formattedBibliography.concat(book.rawFormatCitation).concat("<br>");
        }

        //Clip it. The plaintext is a backup for the html text in case they are pasting where HTML is not supported.
        ClipData clip = ClipData.newHtmlText("Bibliography", rawBibliography, formattedBibliography);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied! Remember to italicize your titles in your document!", Toast.LENGTH_LONG).show();
    }

    //Exports citations to a clipboard
    @SuppressWarnings("unused")
    public void export(View view) {
        //Builds bibliography
        String rawBibliography = "";
        for (Book book : adapter.books) {
            rawBibliography = rawBibliography.concat(book.rawFormatCitation).concat("<br>");
        }
        //Converts to HTML
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
