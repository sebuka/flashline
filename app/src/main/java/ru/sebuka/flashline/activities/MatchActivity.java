package ru.sebuka.flashline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sebuka.flashline.R;
import ru.sebuka.flashline.managers.ProfileManager;
import ru.sebuka.flashline.models.User;
import ru.sebuka.flashline.utils.ApiService;
import ru.sebuka.flashline.utils.ProfileLoadCallback;
import ru.sebuka.flashline.utils.RetrofitClient;

public class MatchActivity extends AppCompatActivity {

    private WebSocketClient webSocketClient;
    private LinearLayout player1;
    private LinearLayout player2;

    private TextView countdownText;
    private String matchId;
    private String idToken;
    private ApiService apiService;
    private static final int MATCH_GAME_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);
        countdownText = findViewById(R.id.countdown_text);
        apiService = RetrofitClient.getClient().create(ApiService.class);
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
        startMatchMaking();
    }

    private void startMatchMaking() {
        TextView text = new TextView(this);
        text.setText("Ожидание...");
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTextSize(36);
        player2.addView(text);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            idToken = account.getIdToken();
            loadUserProfile(account.getId(), player1);
            addToMatchMakingQueue();
            connectWebSocket();
        } else {
            Toast.makeText(MatchActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserProfile(String googleId, LinearLayout profile) {
        ProfileManager.loadUser(this, googleId, new ProfileLoadCallback() {
            @Override
            public void onProfileLoaded(User user) {
                View userProfileView = LayoutInflater.from(MatchActivity.this)
                        .inflate(R.layout.item_match_profile, profile, false);
                TextView name = userProfileView.findViewById(R.id.tvName);
                TextView rating = userProfileView.findViewById(R.id.tvRating);
                TextView MMR = userProfileView.findViewById(R.id.tvMMR);
                ImageView img = userProfileView.findViewById(R.id.profile_picture);
                name.setText(user.getName());
                rating.setText("Рейтинг: " + user.getRating());
                MMR.setText("MMR: " + user.getMmr());
                Glide.with(MatchActivity.this).load(user.getImg()).into(img);
                profile.addView(userProfileView);
            }

            @Override
            public void onProfileLoadFailed(Throwable t) {
                Toast.makeText(MatchActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://remarkable-creativity-production.up.railway.app?tokenId=" + idToken);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WebSocket", "Opened");
            }

            @Override
            public void onMessage(String message) {
                Log.i("WebSocket", "Message received: " + message);
                runOnUiThread(() -> {
                    handleWebSocketMessage(message);
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("WebSocket", "Closed " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.i("WebSocket", "Error " + ex.getMessage());
            }
        };

        webSocketClient.connect();
    }

    private void handleWebSocketMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.getString("type");

            switch (type) {
                case "matchFound":
                    matchId = jsonObject.getString("matchId");
                    String opponentId = jsonObject.getString("opponent");
                    player2.removeAllViews();
                    loadUserProfile(opponentId, player2);
                    confirmMatch();
                    break;

                case "matchActive":
                    Toast.makeText(this, "Матч подтвержден", Toast.LENGTH_SHORT).show();
                    startCountdown();
                    break;

                case "matchCancelled":
                    Toast.makeText(this, "Матч отменен", Toast.LENGTH_SHORT).show();
                    player2.removeAllViews();
                    TextView text = new TextView(this);
                    text.setText("Ожидание...");
                    text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    text.setTextSize(36);
                    player2.addView(text);
                    break;

                case "levelModel":
                    String levelModel = jsonObject.getString("model");
                    // Handle the level model, e.g., start the game
                    startGame(levelModel);
                    break;
                case "enemyScore":
                    Integer score = jsonObject.getInt("enemyScore");
                    enemyScore(score);
                    break;
                case "changeMmr":
                    Integer yourmmr = jsonObject.getInt("you");
                    Integer enemymmr = jsonObject.getInt("enemy");
                    displayMmr(yourmmr, enemymmr);
                    break;
                case "error":
                    Toast.makeText(this, "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayMmr(Integer yourmmr, Integer enemymmr) {
        TextView textView1 = new TextView(this);
        textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView1.setTextSize(30);
        textView1.setText("Изменение MMR: " + yourmmr);
        player1.addView(textView1);
        TextView textView2 = new TextView(this);
        textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView2.setTextSize(30);
        textView2.setText("Изменение MMR: " + enemymmr);
        player2.addView(textView2);
    }

    private void startCountdown() {
        countdownText.setVisibility(View.VISIBLE);
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdownText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                countdownText.setText("0");
                countdownText.setVisibility(View.GONE);
            }
        }.start();
    }

    private void startGame(String levelModelJson) {
        Intent intent = new Intent(this, MatchGameActivity.class);
        intent.putExtra("LEVEL_MODEL", levelModelJson); // Assuming you have levelModelJson
        startActivityForResult(intent, MATCH_GAME_REQUEST);

    }

    private void confirmMatch() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send("{ \"type\": \"confirmMatch\", \"idToken\": \"" + idToken + "\", \"matchId\": \"" + matchId + "\" }");
            Toast.makeText(this, "Матч подтвержден", Toast.LENGTH_SHORT).show();
        }
    }

    private void declineMatch() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send("{ \"type\": \"declineMatch\", \"idToken\": \"" + idToken + "\", \"matchId\": \"" + matchId + "\" }");
            Toast.makeText(this, "Матч отклонен", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendScore(int score) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send("{ \"type\": \"sendScore\", \"idToken\": \"" + idToken + "\", \"matchId\": \"" + matchId + "\", \"score\": \" " + score + "\" }");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveMatchMakingQueue();
        declineMatch();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    private void addToMatchMakingQueue() {
        apiService.joinQueue(idToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MatchActivity.this, "Добавлено в очередь матчмейкинга", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MatchActivity.this, "Не удалось добавить в очередь", Toast.LENGTH_SHORT).show();
                }
                Log.i("QueueJoin", response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MatchActivity.this, "Ошибка при добавлении в очередь", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void leaveMatchMakingQueue() {
        apiService.leaveQueue(idToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MatchActivity.this, "Удалено из очереди матчмейкинга", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MatchActivity.this, "Не удалось удалить из очереди", Toast.LENGTH_SHORT).show();
                }
                Log.i("QueueLeave", response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MatchActivity.this, "Ошибка при удалении из очереди", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MATCH_GAME_REQUEST ) {
            int score = data.getIntExtra("SCORE", 0);
            sendScore(score);
            TextView textView = new TextView(this);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(30);
            textView.setText("Ваш счёт: " + score);
            player1.addView(textView);
        }
    }

    private void enemyScore(int score) {
        TextView textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(30);
        textView.setText("Cчёт противника: " + score);
        player2.addView(textView);
    }
}
