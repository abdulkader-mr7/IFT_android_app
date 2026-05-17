package com.tamilquran.ift.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.R;
import com.tamilquran.ift.model.entity.SuraHeader;

public class SuraListAdapter extends ListAdapter<SuraHeader, SuraListAdapter.SuraViewHolder> {

    public interface OnSuraClickListener {
        void onSuraClick(SuraHeader header);
    }

    private final OnSuraClickListener listener;
    private final int tamilFontSize;

    public SuraListAdapter(OnSuraClickListener listener, int tamilFontSize) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.tamilFontSize = tamilFontSize;
    }

    @NonNull
    @Override
    public SuraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_suratitle, parent, false);
        return new SuraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuraViewHolder holder, int position) {
        SuraHeader header = getItem(position);
        holder.title.setText(header.suraNo + ". " + header.name);
        holder.detail.setText(header.suraType + ", வசனங்கள் : " + header.verseCount);
        holder.title.setTextSize(tamilFontSize);
        holder.detail.setTextSize(Math.max(10, tamilFontSize - 2));
        holder.itemView.setOnClickListener(v -> listener.onSuraClick(header));
    }

    static class SuraViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView detail;

        SuraViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            detail = itemView.findViewById(R.id.txtDtl);
        }
    }

    private static final DiffUtil.ItemCallback<SuraHeader> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SuraHeader>() {
                @Override
                public boolean areItemsTheSame(@NonNull SuraHeader oldItem, @NonNull SuraHeader newItem) {
                    return oldItem.suraNo == newItem.suraNo;
                }

                @Override
                public boolean areContentsTheSame(@NonNull SuraHeader oldItem, @NonNull SuraHeader newItem) {
                    return oldItem.name.equals(newItem.name)
                            && oldItem.suraType.equals(newItem.suraType)
                            && oldItem.verseCount == newItem.verseCount;
                }
            };
}
