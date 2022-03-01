package com.warframefarm.activities.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.warframefarm.database.Setting;

public class MainViewModel extends AndroidViewModel {

    private final MainRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = MainRepository.getInstance(application);
        repository.checkForUpdates();
    }

    public LiveData<FirebaseUser> getUser() {
        return repository.getUser();
    }

    public void updateUser() {
        repository.updateUser();
    }

    public void saveEmail(Context context, String email) {
        repository.saveEmail(context, email);
    }

    public void signOut() {
        repository.signOut();
    }

    public LiveData<Setting> getSettings() {
        return repository.getSettings();
    }

    public void saveLoadLimit(int limit) {
        repository.saveLoadLimit(limit);
    }

    public void setLimited(boolean limited) {
        repository.setLimited(limited);
    }

    public void syncFromLocal() {
        repository.syncFromLocal();
    }

    public void syncFromOnline() {
        repository.syncFromOnline();
    }

    public void resetUserData() {
        repository.resetUserData();
    }

    public void checkForUpdates() {
        repository.checkForUpdates();
    }
}
