package ru.sebuka.flashline.utils;

import ru.sebuka.flashline.models.User;

public interface ProfileLoadCallback {
    void onProfileLoaded(User user);
    void onProfileLoadFailed(Throwable t);
}
