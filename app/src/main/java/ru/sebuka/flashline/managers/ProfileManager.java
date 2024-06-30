package ru.sebuka.flashline.managers;

import android.content.Context;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sebuka.flashline.utils.ApiService;
import ru.sebuka.flashline.utils.ProfileLoadCallback;
import ru.sebuka.flashline.utils.RetrofitClient;
import ru.sebuka.flashline.models.UpdateQuery;
import ru.sebuka.flashline.models.User;
import ru.sebuka.flashline.models.UserResponse;

public class ProfileManager {

    public static void loadUser(Context context, String googleId, ProfileLoadCallback callback) {
        RetrofitClient.getClient().create(ApiService.class).getProfile(googleId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    Toast.makeText(context, "Профиль загружен успешно", Toast.LENGTH_SHORT).show();
                    callback.onProfileLoaded(user);
                } else {
                    Toast.makeText(context, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                    callback.onProfileLoadFailed(new Exception("Ошибка загрузки профиля"));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(context, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                callback.onProfileLoadFailed(t);
            }
        });
    }

    public static void saveUser(Context context, String token, UpdateQuery updateQuery) {
        RetrofitClient.getClient().create(ApiService.class).updateProfile(updateQuery, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Профиль обновлён успешно", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void addFriend(Context context, String tokenId, String googleId) {
        RetrofitClient.getClient().create(ApiService.class).addFriend(tokenId, googleId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(context,response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Ошибка добавления друга", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void deleteFriend(Context context, String tokenId, String googleId) {
        RetrofitClient.getClient().create(ApiService.class).deleteFriend(tokenId, googleId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Ошибка удаления друга", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
