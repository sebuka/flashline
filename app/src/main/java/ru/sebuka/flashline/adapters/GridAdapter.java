package ru.sebuka.flashline.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.sebuka.flashline.R;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    private Context mContext;
    private int mSize;
    private int mItemSize;

    public GridAdapter(Context context, int size, int itemSize) {
        mContext = context;
        mSize = size;
        mItemSize = itemSize;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setBackgroundResource(R.drawable.grid_item_background);
        ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        layoutParams.width = mItemSize;
        layoutParams.height = mItemSize;
        holder.imageView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mSize * mSize;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_item_image);
        }
    }
}
