package ru.sebuka.flashline.utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.sebuka.flashline.models.UpdateQuery;
import ru.sebuka.flashline.models.UserResponse;
import ru.sebuka.flashline.models.RatingResponse; // Assuming you have a model for RatingResponse

public interface ApiService {
    @GET("/profiles/profile")
    Call<UserResponse> getProfile(@Query("googleId") String googleId);

    @POST("/profiles/updateProfile")
    Call<Void> updateProfile(@Body UpdateQuery user, @Query("idToken") String idToken);

    @POST("/profiles/addFriend")
    Call<Void> addFriend(@Query("tokenId") String tokenId, @Query("googleId") String googleId);

    @POST("/profiles/deleteFriend")
    Call<Void> deleteFriend(@Query("tokenId") String tokenId, @Query("googleId") String googleId);

    @GET("/profiles/getRating")
    Call<RatingResponse> getRating();

    @POST("/matches/joinQueue")
    Call<Void> joinQueue(@Query("tokenId") String tokenId);

    @POST("/matches/leaveQueue")
    Call<Void> leaveQueue(@Query("tokenId") String tokenId);
}
