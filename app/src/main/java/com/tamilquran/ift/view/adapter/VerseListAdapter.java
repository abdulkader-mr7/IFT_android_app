package com.tamilquran.ift.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.R;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.utils.ArabicTextHelper;
import com.tamilquran.ift.utils.FontManager;

public class VerseListAdapter extends ListAdapter<VerseRow, VerseListAdapter.VerseViewHolder> {

    @Nullable
    private String highlightQuery;
    private int tamilFontSize = 16;
    private int arabicFontSize = 24;
    @Nullable
    private OnVerseInteractionListener interactionListener;

    public interface OnVerseInteractionListener {
        void onVerseClick(int position, VerseRow row);

        void onVerseLongClick(int position, VerseRow row);
    }

    public VerseListAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnVerseInteractionListener(@Nullable OnVerseInteractionListener listener) {
        this.interactionListener = listener;
    }

    /**
     * Updates the font sizes used when binding rows. Does not refresh on its
     * own: a following submitList() binds new rows with these sizes. For an
     * in-place size change call notifyItemRangeChanged() afterwards.
     */
    public void setFontSizes(int tamilFontSize, int arabicFontSize) {
        this.tamilFontSize = tamilFontSize;
        this.arabicFontSize = arabicFontSize;
    }

    public void setHighlightQuery(@Nullable String highlightQuery) {
        this.highlightQuery = highlightQuery;
    }

    @NonNull
    @Override
    public VerseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_sura_dtl, parent, false);
        return new VerseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerseViewHolder holder, int position) {
        VerseRow row = getItem(position);
        Context context = holder.itemView.getContext();
        Typeface arabicFace = FontManager.getArabicTypeface(context);
        Typeface tamilFace = FontManager.getTamilTypeface(context);

        int tamilSize = tamilFontSize;
        int arabicSize = arabicFontSize;

        if (row.bismillahRow) {
            holder.tamil.setTextColor(0xFF800080);
            holder.tamil.setGravity(android.view.Gravity.CENTER);
            holder.arabic.setTextColor(0xFF800080);
            holder.arabic.setGravity(android.view.Gravity.CENTER);
        } else {
            holder.tamil.setTextColor(0xFF000000);
            holder.tamil.setGravity(android.view.Gravity.START);
            holder.arabic.setTextColor(0xFF000000);
            holder.arabic.setGravity(android.view.Gravity.END);
        }

        if (row.tamilText != null && !row.tamilText.isEmpty()) {
            CharSequence tamilText = row.tamilText;
            if (highlightQuery != null && !highlightQuery.isEmpty()) {
                SpannableStringBuilder builder = new SpannableStringBuilder(row.tamilText);
                int index = 0;
                while ((index = row.tamilText.indexOf(highlightQuery, index)) >= 0) {
                    builder.setSpan(
                            new android.text.style.BackgroundColorSpan(0xFFFFFF00),
                            index,
                            index + highlightQuery.length(),
                            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    index += highlightQuery.length();
                }
                tamilText = builder;
            }
            holder.tamil.setText(tamilText);
            holder.tamil.setTypeface(tamilFace);
            holder.tamil.setTextSize(tamilSize);
        } else {
            holder.tamil.setText("");
            holder.tamil.setTextSize(1);
        }

        if (row.arabicText != null && !row.arabicText.isEmpty()) {
            holder.arabic.setText(ArabicTextHelper.buildStyledArabic(row.arabicText));
            holder.arabic.setTypeface(arabicFace);
            holder.arabic.setTextSize(arabicSize);
        } else {
            holder.arabic.setText("");
            holder.arabic.setTextSize(1);
        }

        holder.itemView.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onVerseClick(holder.getBindingAdapterPosition(), row);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onVerseLongClick(holder.getBindingAdapterPosition(), row);
                return true;
            }
            return false;
        });
    }

    static class VerseViewHolder extends RecyclerView.ViewHolder {
        final TextView tamil;
        final TextView arabic;
        VerseViewHolder(@NonNull View itemView) {
            super(itemView);
            tamil = itemView.findViewById(R.id.txtTitle);
            arabic = itemView.findViewById(R.id.txtArabic);
        }
    }

    private static final DiffUtil.ItemCallback<VerseRow> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<VerseRow>() {
                @Override
                public boolean areItemsTheSame(@NonNull VerseRow oldItem, @NonNull VerseRow newItem) {
                    return oldItem.sura == newItem.sura && oldItem.ayah == newItem.ayah;
                }

                @Override
                public boolean areContentsTheSame(@NonNull VerseRow oldItem, @NonNull VerseRow newItem) {
                    return oldItem.tamilText.equals(newItem.tamilText)
                            && oldItem.arabicText.equals(newItem.arabicText);
                }
            };
}
