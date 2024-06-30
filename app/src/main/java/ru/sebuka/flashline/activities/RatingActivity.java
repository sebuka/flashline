package ru.sebuka.flashline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sebuka.flashline.R;
import ru.sebuka.flashline.adapters.RatingAdapter;
import ru.sebuka.flashline.models.RatingResponse;
import ru.sebuka.flashline.models.User;
import ru.sebuka.flashline.utils.ApiService;
import ru.sebuka.flashline.utils.RetrofitClient;

public class RatingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        recyclerView = findViewById(R.id.user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        loadRating();
    }

    private void loadRating() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getRating().enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> topUsers = response.body().getTopGoogleIds();
                    adapter = new RatingAdapter(RatingActivity.this, topUsers, user -> {
                       Intent intent = new Intent(RatingActivity.this, ProfileViewActivity.class);
                        intent.putExtra("GOOGLE_ID", user.getGoogleId());
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(RatingActivity.this, "Ошибка загрузки рейтинга", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {
                Toast.makeText(RatingActivity.this, "Ошибка загрузки рейтинга", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
