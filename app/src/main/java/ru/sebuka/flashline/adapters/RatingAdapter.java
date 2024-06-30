package ru.sebuka.flashline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ru.sebuka.flashline.R;
import ru.sebuka.flashline.managers.ProfileManager;
import ru.sebuka.flashline.models.User;
import ru.sebuka.flashline.utils.ProfileLoadCallback;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private List<String> userIdList;
    private Context context;
    private OnProfileClickListener profileClickListener;

    public RatingAdapter(Context context, List<String> userIdList, OnProfileClickListener profileClickListener) {
        this.context = context;
        this.userIdList = userIdList;
        this.profileClickListener = profileClickListener;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rating_profile, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        ProfileManager.loadUser(context, userIdList.get(position), new ProfileLoadCallback() {
            @Override
            public void onProfileLoaded(User user) {
                holder.Name.setText(user.getName());
                holder.Rating.setText("Рейтинг: " + user.getRating());
                holder.MRR.setText("MMR: " + user.getMmr());

                Glide.with(context).load(user.getImg()).into(holder.profilePicture);

                holder.profileButton.setOnClickListener(v -> {
                    if (profileClickListener != null) {
                        profileClickListener.onProfileClick(user);
                    }
                });
            }

            @Override
            public void onProfileLoadFailed(Throwable t) {
                holder.itemView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userIdList.size();
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView Name;
        TextView Rating;
        TextView MRR;
        Button profileButton;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            Name = itemView.findViewById(R.id.tvName);
            Rating = itemView.findViewById(R.id.tvRating);
            MRR = itemView.findViewById(R.id.tvMMR);
            profileButton = itemView.findViewById(R.id.btnMatch);
        }
    }

    public interface OnProfileClickListener {
        void onProfileClick(User user);
    }
}
