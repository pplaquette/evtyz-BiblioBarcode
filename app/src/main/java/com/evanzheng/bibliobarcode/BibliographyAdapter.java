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

import java.util.List;

public class BibliographyAdapter extends RecyclerView.Adapter<BibliographyAdapter.BibliographyViewHolder> {
    static class BibliographyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout containerView;
        TextView citationView;

        BibliographyViewHolder(View view) {
            super(view);
            this.containerView = view.findViewById(R.id.citation_row);
            this.citationView = view.findViewById(R.id.citation);

            this.containerView.setOnClickListener(v -> {
                Context context = v.getContext();
                Book book = (Book) containerView.getTag();
                Intent intent = new Intent(context, BookActivity.class);
                intent.putExtra("isbn", book.isbn);

                context.startActivity(intent);
            });

        }
    }

    private List<Book> books = MainActivity.database.bookDao().loadBookSources();

    @NonNull
    @Override
    public BibliographyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.citation, parent, false);
        return new BibliographyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BibliographyViewHolder holder, int position) {
        Book current = books.get(position);
        holder.containerView.setTag(current);
        holder.citationView.setText(current.title);
        //TODO
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    void deleteItem(int position) {
        Book current = books.get(position);
        MainActivity.database.bookDao().deleteBook(current.isbn);
        reload();
    }

    private void reload() {
        books = MainActivity.database.bookDao().loadBookSources();
        notifyDataSetChanged();
    }

}
