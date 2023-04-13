package com.evanzheng.bibliobarcode;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class BibliographyAdapter extends RecyclerView.Adapter<BibliographyAdapter.BibliographyViewHolder> {

    // Style of citation
    String style;

    //All books in bibliography
    List<Book> books = MainActivity.database.bookDao().loadBookSources();

    //Clipboard manager for copying individual citations
    private final ClipboardManager clipboard;

    //Constructor method
    BibliographyAdapter(String style, ClipboardManager clipboard) {
        super();
        this.style = style;
        this.clipboard = clipboard;
    }

    //Creating a member of the list
    @NonNull
    @Override
    public BibliographyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.citation, parent, false);
        return new BibliographyViewHolder(view, clipboard);
    }

    //Binding it and linking it to a book
    @Override
    public void onBindViewHolder(@NonNull BibliographyViewHolder holder, int position) {
        Book current = books.get(position);
        holder.containerView.setTag(current);
        holder.citationView.setText(current.getCitation());
    }

    //Num items in list
    @Override
    public int getItemCount() {
        return books.size();
    }

    //Delete an item
    void deleteItem(int position) {
        Book current = books.get(position);
        MainActivity.database.bookDao().deleteBook(current.isbn);
        reload();


    }

    //Reload the list
    void reload() {
        books = MainActivity.database.bookDao().loadBookSources();
        for (Book book : books) {
            book.cite(style);
        }
        Collections.sort(books);
        notifyDataSetChanged();
    }

    //The actual viewholder for each entry in recyclerview
    static class BibliographyViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout containerView;
        final TextView citationView;

        BibliographyViewHolder(View view, ClipboardManager clipboard) {
            super(view);
            this.containerView = view.findViewById(R.id.citation_row);
            this.citationView = view.findViewById(R.id.citation);

            //Listen for touch to go to edit screen
            this.containerView.setOnClickListener(v -> {
                Context context = v.getContext();
                Book book = (Book) containerView.getTag();
                Intent intent = new Intent(context, BookActivity.class);
                intent.putExtra("isbn", book.isbn);

                context.startActivity(intent);
            });

            //Listen for long touch to copy text
            this.containerView.setOnLongClickListener(v -> {
                Book book = (Book) containerView.getTag();
                ClipData clip = ClipData.newHtmlText("Bibliography", book.citation, book.rawFormatCitation);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "Citation copied to clipboard!", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

}
