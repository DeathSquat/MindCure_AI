package com.eyantra.mind_cure_ai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eyantra.mind_cure_ai.R;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {
    private final int[] avatarResources = {
        R.drawable.avatar1,  // Bunny Hoodie
        R.drawable.avatar2,  // Simple Boy
        R.drawable.avatar3,  // Girl with Green Background
        R.drawable.avatar4,  // Girl with Glasses
        R.drawable.avatar5,  // Girl with Cat Hoodie
        R.drawable.avatar6   // Doctor
    };

    private int selectedPosition = -1;
    private OnAvatarSelectedListener listener;

    public interface OnAvatarSelectedListener {
        void onAvatarSelected(int position, int resourceId);
    }

    public void setOnAvatarSelectedListener(OnAvatarSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        holder.avatarImage.setImageResource(avatarResources[position]);
        holder.selectionIndicator.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onAvatarSelected(position, avatarResources[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarResources.length;
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        View selectionIndicator;

        AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);
        }
    }
} 