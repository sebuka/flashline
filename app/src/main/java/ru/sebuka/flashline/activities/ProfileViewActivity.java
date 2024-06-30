package ru.sebuka.flashline.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ru.sebuka.flashline.R;
import ru.sebuka.flashline.adapters.FriendsListAdapter;
import ru.sebuka.flashline.managers.ProfileManager;
import ru.sebuka.flashline.models.User;
import ru.sebuka.flashline.utils.ProfileLoadCallback;

public class ProfileViewActivity extends AppCompatActivity {

    private Button backButton, addFriendButton, saveButton;

    private ImageView imageView;
    private TextView nameText, descriptionText;
    private TextView ratingTextView,mmrTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        backButton = findViewById(R.id.back_button);
        nameText = findViewById(R.id.name);
        imageView = findViewById(R.id.profile_picture);
        descriptionText = findViewById(R.id.description);
        ratingTextView = findViewById(R.id.rating_text);
        mmrTextView = findViewById(R.id.mmr_text);
        String googleId = getIntent().getStringExtra("GOOGLE_ID");

        loadProfile(googleId);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void loadProfile(String googleId) {
        ProfileManager.loadUser(this, googleId, new ProfileLoadCallback() {
            @Override
            public void onProfileLoaded(User user) {
                nameText.setText(user.getName());
                descriptionText.setText(user.getDesc());
                ratingTextView.setText("Рейтинг: " + user.getRating());
                mmrTextView.setText("MMR: " + user.getMmr());
                Glide.with(ProfileViewActivity.this).load(user.getImg()).into(imageView);
            }

            @Override
            public void onProfileLoadFailed(Throwable t) {
                Toast.makeText(ProfileViewActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
