package com.evanzheng.bibliobarcode;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class BibliographyAdapter extends RecyclerView.Adapter<BibliographyAdapter.BibliographyViewHolder> {

    String style;
    private List<Book> books = MainActivity.database.bookDao().loadBookSources();

    BibliographyAdapter(String style) {
        super();
        this.style = style;
    }

    //Creating a member of the list
    @NonNull
    @Override
    public BibliographyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.citation, parent, false);
        return new BibliographyViewHolder(view);
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
        //TODO Popup window confirmation

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

    static class BibliographyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout containerView;
        TextView citationView;

        BibliographyViewHolder(View view) {
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

        }
    }

}
