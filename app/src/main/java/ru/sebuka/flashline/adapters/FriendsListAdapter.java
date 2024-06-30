package ru.sebuka.flashline.adapters;

import android.annotation.SuppressLint;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.List;

import ru.sebuka.flashline.utils.ProfileLoadCallback;
import ru.sebuka.flashline.managers.ProfileManager;
import ru.sebuka.flashline.R;
import ru.sebuka.flashline.models.User;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder> {

    private List<String> friendsList;
    private Context context;
    public FriendsListAdapter(Context context) {
        this.context = context;
    }
    public void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String friendGoogleId = friendsList.get(position);
        ProfileManager.loadUser(null, friendGoogleId, new ProfileLoadCallback() {
            @Override
            public void onProfileLoaded(User friend) {
                holder.friendNameTextView.setText(friend.getName());
                holder.friendRatingTextView.setText("Рейтинг: " + friend.getMmr());
                holder.friendStatusTextView.setVisibility(View.GONE);
                holder.friendStatusTextView.setText("Онлайн");
                Glide.with(holder.itemView.getContext()).load(friend.getImg()).into(holder.friendProfilePictureImageView);
                holder.matchButton.setVisibility(View.GONE);
                holder.matchButton.setOnClickListener(v -> {
                });

                holder.deleteButton.setOnClickListener(v -> {
                    ProfileManager.deleteFriend(context, GoogleSignIn.getLastSignedInAccount(context).getIdToken(), friendsList.get(position));
                    friendsList.remove(position);
                    notifyItemRemoved(position);
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
        return friendsList != null ? friendsList.size() : 0;
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {

        TextView friendNameTextView;
        TextView friendRatingTextView;
        TextView friendStatusTextView;
        ImageView friendProfilePictureImageView;
        Button matchButton;
        Button deleteButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.tvFriendName);
            friendRatingTextView = itemView.findViewById(R.id.tvFriendRating);
            friendStatusTextView = itemView.findViewById(R.id.tvFriendStatus);
            friendProfilePictureImageView = itemView.findViewById(R.id.profile_picture);
            matchButton = itemView.findViewById(R.id.btnMatch);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
