package com.eyantra.mind_cure_ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.TipViewHolder> {
    private List<Tip> tips;

    public TipAdapter(List<Tip> tips) {
        this.tips = tips;
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        Tip tip = tips.get(position);
        holder.categoryTextView.setText(tip.getCategory());
        holder.tipTextView.setText(tip.getText());
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    static class TipViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView tipTextView;

        TipViewHolder(View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.tipCategory);
            tipTextView = itemView.findViewById(R.id.tipText);
        }
    }
} 