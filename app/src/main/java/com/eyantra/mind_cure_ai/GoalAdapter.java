package com.eyantra.mind_cure_ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals;
    private OnGoalClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    public GoalAdapter(List<Goal> goals, OnGoalClickListener listener) {
        this.goals = goals;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void updateGoals(List<Goal> newGoals) {
        this.goals = newGoals;
        notifyDataSetChanged();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvDeadline;
        private CheckBox checkBox;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            checkBox = itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onGoalClick(goals.get(position));
                }
            });
        }

        public void bind(Goal goal) {
            tvTitle.setText(goal.getTitle());
            tvDescription.setText(goal.getDescription());
            tvDeadline.setText("Deadline: " + dateFormat.format(new Date(goal.getDeadline())));
            checkBox.setChecked(goal.isCompleted());
        }
    }
} 