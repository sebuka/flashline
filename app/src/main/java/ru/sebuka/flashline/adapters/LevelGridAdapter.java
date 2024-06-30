package ru.sebuka.flashline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.sebuka.flashline.R;

public class LevelGridAdapter extends RecyclerView.Adapter<LevelGridAdapter.LevelViewHolder> {

    private Context context;
    private List<Integer> levels;
    private OnLevelClickListener listener;

    public LevelGridAdapter(Context context, List<Integer> levels, OnLevelClickListener listener) {
        this.context = context;
        this.levels = levels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level_button, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        int level = levels.get(position);
        holder.button.setText(String.valueOf(level));
        holder.button.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLevelClick(level);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.level_button);
        }
    }

    public interface OnLevelClickListener {
        void onLevelClick(int level);
    }
}
