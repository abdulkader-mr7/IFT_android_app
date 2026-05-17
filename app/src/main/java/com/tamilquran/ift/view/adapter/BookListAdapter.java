package com.tamilquran.ift.view.adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.R;
import com.tamilquran.ift.model.entity.BookItem;

import java.io.File;
import java.text.DecimalFormat;
public class BookListAdapter extends ListAdapter<BookItem, BookListAdapter.BookViewHolder> {

    public interface BookActionListener {
        void onDownloadClick(BookItem item, int position);

        void onDeleteClick(BookItem item, int position);
    }

    private final BookActionListener listener;
    private final boolean samarasam;
    private final int tamilFontSize;
    private final DecimalFormat sizeFormat = new DecimalFormat("0.00");

    public BookListAdapter(BookActionListener listener, boolean samarasam, int tamilFontSize) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.samarasam = samarasam;
        this.tamilFontSize = tamilFontSize;
    }

    public void setPdfExistsChecker(PdfExistsChecker checker) {
        this.pdfExistsChecker = checker;
    }

    public void setCoverFileResolver(CoverFileResolver resolver) {
        this.coverFileResolver = resolver;
    }

    private PdfExistsChecker pdfExistsChecker;
    private CoverFileResolver coverFileResolver;

    public interface PdfExistsChecker {
        boolean pdfExists(String filename);
    }

    public interface CoverFileResolver {
        File getCoverFile(String filename);
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.iftbooks_list_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookItem item = getItem(position);
        holder.title.setText(item.bookTitle);
        holder.author.setText(item.author);
        holder.size.setText(sizeFormat.format(item.sizeBytes / 1024.0) + " MB");
        holder.title.setTextSize(tamilFontSize);
        holder.author.setTextSize(tamilFontSize);
        holder.size.setTextSize(tamilFontSize);

        boolean pdfExists = pdfExistsChecker != null && pdfExistsChecker.pdfExists(item.bookFilename);
        if (pdfExists) {
            holder.download.setImageResource(R.mipmap.ic_open_in_browser_black_36dp);
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.download.setImageResource(R.mipmap.ic_file_download_black_36dp);
            holder.delete.setVisibility(View.INVISIBLE);
        }

        int placeholder = samarasam ? R.mipmap.samarasam : R.mipmap.iftbooks;
        holder.cover.setImageResource(placeholder);
        if (coverFileResolver != null) {
            File coverFile = coverFileResolver.getCoverFile(item.bookFilename);
            if (coverFile.exists()) {
                holder.cover.setImageBitmap(BitmapFactory.decodeFile(coverFile.getAbsolutePath()));
            }
        }

        holder.download.setOnClickListener(v -> listener.onDownloadClick(item, position));
        holder.delete.setOnClickListener(v -> listener.onDeleteClick(item, position));
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView author;
        final TextView size;
        final ImageView cover;
        final ImageButton download;
        final ImageButton delete;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookname);
            author = itemView.findViewById(R.id.author);
            size = itemView.findViewById(R.id.size);
            cover = itemView.findViewById(R.id.ImageView01);
            download = itemView.findViewById(R.id.downloadbutton);
            delete = itemView.findViewById(R.id.deletebutton);
        }
    }

    private static final DiffUtil.ItemCallback<BookItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BookItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookItem oldItem, @NonNull BookItem newItem) {
                    return oldItem.sno == newItem.sno;
                }

                @Override
                public boolean areContentsTheSame(@NonNull BookItem oldItem, @NonNull BookItem newItem) {
                    return oldItem.bookFilename.equals(newItem.bookFilename);
                }
            };
}
