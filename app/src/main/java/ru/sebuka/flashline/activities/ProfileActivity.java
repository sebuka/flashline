package ru.sebuka.flashline.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import ru.sebuka.flashline.R;
import ru.sebuka.flashline.adapters.FriendsListAdapter;
import ru.sebuka.flashline.managers.ProfileManager;
import ru.sebuka.flashline.models.UpdateQuery;
import ru.sebuka.flashline.models.User;
import ru.sebuka.flashline.utils.ProfileLoadCallback;

public class ProfileActivity extends AppCompatActivity {

    private Button backButton, addFriendButton, saveButton;

    private ImageView imageView;
    private EditText nameEditText, descriptionEditText;
    private TextView ratingTextView, mmrTextView;
    private RecyclerView friendsRecyclerView;
    private FriendsListAdapter friendsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        backButton = findViewById(R.id.back_button);
        addFriendButton = findViewById(R.id.add_friend_button);
        saveButton = findViewById(R.id.save_button);
        nameEditText = findViewById(R.id.name_edittext);
        imageView = findViewById(R.id.profile_picture);
        descriptionEditText = findViewById(R.id.description_edittext);
        ratingTextView = findViewById(R.id.rating_text);
        mmrTextView = findViewById(R.id.mmr_text);
        friendsRecyclerView = findViewById(R.id.friends_list);

        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsListAdapter = new FriendsListAdapter(this);
        friendsRecyclerView.setAdapter(friendsListAdapter);


        loadProfile();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });

    }

    private void loadProfile() {
        String googleId = GoogleSignIn.getLastSignedInAccount(this).getId();
        ProfileManager.loadUser(this, googleId, new ProfileLoadCallback() {
            @Override
            public void onProfileLoaded(User user) {
                nameEditText.setText(user.getName());
                descriptionEditText.setText(user.getDesc());
                ratingTextView.setText("Рейтинг: " + user.getRating());
                mmrTextView.setText("MMR: " + user.getMmr());
                friendsListAdapter.setFriendsList(user.getFriendlist());
                Glide.with(ProfileActivity.this).load(user.getImg()).into(imageView);
            }

            @Override
            public void onProfileLoadFailed(Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveProfile() {
        String token = getToken();
        UpdateQuery updateQuery = new UpdateQuery();
        updateQuery.setName(nameEditText.getText().toString());
        updateQuery.setDesc(descriptionEditText.getText().toString());
        ProfileManager.saveUser(this, token, updateQuery);
    }

    private void showAddFriendDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_friend, null);

        EditText editTextFriendCode = dialogView.findViewById(R.id.editTextFriendCode);
        Button buttonAddFriend = dialogView.findViewById(R.id.buttonAddFriend);
        TextView textViewFriendCode = dialogView.findViewById(R.id.textViewFriendCode);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String myFriendCode = account.getId();
            textViewFriendCode.setText(myFriendCode);
            textViewFriendCode.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Friend Code", myFriendCode);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Код друга скопирован", Toast.LENGTH_SHORT).show();
            });
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        buttonAddFriend.setOnClickListener(v -> {
            String friendGoogleId = editTextFriendCode.getText().toString();
            ProfileManager.addFriend(this, getToken(), friendGoogleId);
            friendsListAdapter.notifyDataSetChanged();
        });

        dialog.show();
    }


    private String getToken() {
        String token = GoogleSignIn.getLastSignedInAccount(this).getIdToken();
        Log.d("Token", token);
        return token;
    }
}
