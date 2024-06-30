package ru.sebuka.flashline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.Callback;
import ru.sebuka.flashline.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String SERVER_URL = "https://remarkable-creativity-production.up.railway.app/profiles/authenticate"; // Замените на ваш IP адрес

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            showSignInDialog();
        } else {
            authenticateWithServer();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            } catch (ApiException e) {
                Toast.makeText(this, "Ошибка входа в гугл аккаунт", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) throws ApiException {
        GoogleSignInAccount account = completedTask.getResult(ApiException.class);
        if (account != null) {
            Toast.makeText(this, "Выполнен вход: " + account.getEmail(), Toast.LENGTH_SHORT).show();
            authenticateWithServer();
        } else {
            showSignInDialog();
        }
    }

    private void authenticateWithServer() {
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(Task<GoogleSignInAccount> task) {
                if (task.isSuccessful()) {
                    GoogleSignInAccount account = task.getResult();
                    String idToken = account.getIdToken();
                    sendTokenToServer(idToken);
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка входа в гугл аккаунт в фоновом режиме", Toast.LENGTH_SHORT).show();
                    signIn();
                }
            }
        });
    }

    private void sendTokenToServer(String idToken) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"idToken\":\"" + idToken + "\"}";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка аутентификации на сервер", Toast.LENGTH_SHORT).show());
                finish();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        initializeUI();
                    });
                } else {
                    Log.e("MainActivity", "Failed to authenticate with server: " + response.message());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка аутентификации на сервер", Toast.LENGTH_SHORT).show());
                    finish();
                }
            }
        });
    }

    private void showSignInDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Требуется вход")
                .setMessage("Вам необходимо войти в свою учетную запись Google, чтобы продолжить. Хотите войти сейчас?")
                .setPositiveButton("Войти", (dialog, which) -> signIn())
                .setNegativeButton("Отмена", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void initializeUI() {

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button playPuzzleButton = findViewById(R.id.play_puzzle_button);
        Button playCompetitiveButton = findViewById(R.id.play_competitive_button);
        Button statsButton = findViewById(R.id.stats_button);
        Button rulesButton = findViewById(R.id.rules_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        playPuzzleButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LevelSelectionActivity.class);
            startActivity(intent);
        });
        playCompetitiveButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompetitiveActivity.class);
            startActivity(intent);
        });

        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        rulesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RulesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
