package com.eyantra.mind_cure_ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    private List<Lesson> lessons;

    public LessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.titleTextView.setText(lesson.getTitle());
        holder.descriptionTextView.setText(lesson.getDescription());
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        LessonViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.lessonTitle);
            descriptionTextView = itemView.findViewById(R.id.lessonDescription);
        }
    }
} 