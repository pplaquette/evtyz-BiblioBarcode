package com.evanzheng.bibliobarcode

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class BibliographyActivity : AppCompatActivity() {
    //Set up constants
    //Manages citation style
    private var style: String? = null

    //Maps style to appropriate button
    private var styleButtons: MutableMap<String?, Int>? = null

    //Manages the recycler view
    private var adapter: BibliographyAdapter? = null

    //For copying and pasting
    private var clipboard: ClipboardManager? = null

    //To remember styles from previous session, and to remember if a tutorial has been run
    private var sharedPref: SharedPreferences? = null

    //Export buttons initialization
    private var exportButton: FloatingActionButton? = null
    private var copyButton: FloatingActionButton? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bibliography)

        //Set up buttons
        exportButton = findViewById(R.id.export_button)
        copyButton = findViewById(R.id.copy_button)


        //Load up previous style, or set to MLA if none found
        sharedPref = getPreferences(MODE_PRIVATE)
        style = sharedPref?.getString("style", "MLA")

        //Make a hashmap between styles and buttons
        styleButtons = HashMap(5)
        (styleButtons as HashMap<String?, Int>)["Chicago"] = R.id.Chicago
        (styleButtons as HashMap<String?, Int>)["Harvard"] = R.id.Harvard
//        (styleButtons as HashMap<String?, Int>)["MLA"]. = R.id.MLA //PPL
//         (styleButtons as HashMap<String?, Int>)["APA"] = R.id.APA

        //Set up clipboard
        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        //Set up header
        Objects.requireNonNull(supportActionBar)?.title = "Bibliography"
        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
        )
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        //Set up recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        adapter = style?.let { BibliographyAdapter(it, clipboard!!) }

        //Set up recycler view swipe listener
        //This is for swipe-to-delete
        val itemTouchHelper = ItemTouchHelper(object : SwipeDeleteListener(adapter) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //int position = viewHolder.getAdapterPosition(); PPL
                val position = viewHolder.bindingAdapterPosition
                deleteItem(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        //Link everything together
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        setButtons(style)


        //Check to see if a tutorial needs to be run
        val ranBefore = sharedPref?.getBoolean("biblio", false)
        if (!ranBefore!!) {
            runTutorial()
        }
    }

    //Runs the tutorial
    private fun runTutorial() {
        val tutorialConfig = ShowcaseConfig()
        tutorialConfig.delay = 500
        val placeholder = findViewById<LinearLayout>(R.id.placeholder)
        val tutorial = MaterialShowcaseSequence(this, "smthdif5")
        tutorial.setConfig(tutorialConfig)
        tutorial.addSequenceItem(
            placeholder,
            "Edit a citation by tapping, copy by long-pressing, and delete by swiping",
            "OKAY"
        )
        tutorial.addSequenceItem(
            copyButton,
            "Copy your bibliography to your clipboard using this button",
            "OKAY"
        )
        tutorial.addSequenceItem(
            exportButton,
            "Save your bibliography to an HTML file using this button",
            "OKAY"
        )
        tutorial.start()

        //Tutorial will never be run again
        sharedPref!!.edit().putBoolean("biblio", true).apply()
    }

    //Four methods below are linked to onClick in layout files
    fun setMLA(view: View?) {
        setButtons("MLA")
    }

    fun setAPA(view: View?) {
        setButtons("APA")
    }

    fun setChicago(view: View?) {
        setButtons("Chicago")
    }

    fun setHarvard(view: View?) {
        setButtons("Harvard")
    }

    //When a button is pressed
    private fun setButtons(style: String?) {
        //Change the style to correspond with button
        val oldStyle = this.style
        this.style = style
        //Save style
        sharedPref!!.edit().putString("style", style).apply()

        //Send the style to the adapter to reload the citations
        adapter!!.style = this.style.toString()
        adapter!!.reload()

        //Defocus the old button
        processButton(oldStyle, false)

        //Focus the new button
        processButton(style, true)
    }

    //Manages bg color and text color changes for each button
    private fun processButton(style: String?, focus: Boolean) {
        val id = styleButtons!![style] ?: return
        val targetButton = findViewById<TextView>(id)
        val textC: Int
        val bgID: Int

        //Change colours based on whether it's supposed to be focusing/defocusing
        if (focus) {
            textC = getColor(R.color.colorWhite) //PPL
            bgID = R.drawable.pill_activated
        } else {
            textC = getColor(R.color.greyText) //PPL
            bgID = R.drawable.pill_deactivated
        }
        assert(targetButton != null)
        targetButton!!.setTextColor(textC)
        targetButton.setBackgroundResource(bgID)
    }

    // Prompts the user if they are sure they want to delete an entry
    private fun deleteItem(position: Int) {
        AlertDialog.Builder(
            this@BibliographyActivity,
            R.style.Theme_MaterialComponents_Light_Dialog_Alert
        )
            .setTitle("Delete Book")
            .setMessage("Are you sure you want to delete this entry?") // Deletes entry from adapter if they say yes
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                adapter!!.deleteItem(
                    position
                )
            } //Otherwise, reset the recycler view to before they swiped
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface?, which: Int -> adapter!!.reload() }
            .setIcon(android.R.drawable.ic_delete)
            .show()
    }

    // Copies citations to a clipboard
    fun copyToClipboard(view: View?) {

        //Plain text citation
        var rawBibliography = ""

        //HTML citation
        var formattedBibliography = ""

        //Builds each bibliography (we use adapter.books because it is alphabetically sorted)
        for (book in adapter!!.books!!) {
            if (book != null) {
                rawBibliography = """
                        $rawBibliography${book.citation}
                        
                        """.trimIndent()
            }
            if (book != null) {
                formattedBibliography = formattedBibliography + book.rawFormatCitation + "<br>"
            }
        }

        //Clip it. The plaintext is a backup for the html text in case they are pasting where HTML is not supported.
        val clip = ClipData.newHtmlText("Bibliography", rawBibliography, formattedBibliography)
        clipboard!!.setPrimaryClip(clip)
        Toast.makeText(
            this,
            "Copied! Remember to italicize your titles in your document!",
            Toast.LENGTH_LONG
        ).show()
    }

    //Exports citations to a clipboard
    fun export(view: View?) {
        //Builds bibliography
        var rawBibliography = ""
        for (book in adapter!!.books!!) {
            if (book != null) {
                rawBibliography = rawBibliography + book.rawFormatCitation + "<br>"
            }
        }
        //Converts to HTML
        val span = HtmlCompat.fromHtml(rawBibliography, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val htmlBibliography =
            HtmlCompat.toHtml(span, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)

        //Code below is modified from Piyush Malaviya's saveHtmlFile() at https://stackoverflow.com/questions/31553402/save-string-as-html-file-android
        val path = Objects.requireNonNull(getExternalFilesDir(null))?.path
        if (path != null) {
            Log.e("path: ", path)
        }
        val fileName = DateFormat.format("dd_MM_yyyy_hh:mm:ss", System.currentTimeMillis())
            .toString() + "_Bibliography.html"
        val file = File(path, fileName)
        try {
            val out = FileOutputStream(file)
            val data = htmlBibliography.toByteArray()
            out.write(data)
            out.close()
            Toast.makeText(this, "Bibliography saved at $path/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}