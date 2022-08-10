package com.warframefarm.repositories;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.warframefarm.AppExecutors;
import com.warframefarm.R;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.Setting;

import java.util.concurrent.Executor;

public class MainRepository {

    private static MainRepository instance;

    private final SettingsRepository settingsRepository;
    private final UserPrimeRepository userPrimeRepository;
    private final UserComponentRepository userComponentRepository;

    private final FirebaseAuth auth;
    private final FirestoreHelper firestoreHelper;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private final LiveData<Setting> settings;

    private MainRepository(Application application) {
        settingsRepository = new SettingsRepository(application);
        userPrimeRepository = new UserPrimeRepository(application);
        userComponentRepository = new UserComponentRepository(application);

        auth = FirebaseAuth.getInstance();
        firestoreHelper = FirestoreHelper.getInstance(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        updateUser();
        settings = settingsRepository.getSettings();
    }

    public static MainRepository getInstance(Application application) {
        if (instance == null)
            instance = new MainRepository(application);
        return instance;
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public void updateUser() {
        user.setValue(auth.getCurrentUser());
    }

    public void saveEmail(Context context, String email) {
        FirebaseUser user = this.user.getValue();
        if (user != null) {
            if (!user.getEmail().equals(email)) {
                ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.title_updating_email), context.getString(R.string.text_updating_email));
                backgroundThread.execute(() -> {
                    user.updateEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mainThread.execute(() -> {
                                updateUser();

                                progressDialog.dismiss();
                                Toast.makeText(context, R.string.email_updated, Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                });
            } else Toast.makeText(context, R.string.error_same_email, Toast.LENGTH_SHORT).show();
        }
    }

    public void signOut() {
        auth.signOut();
        resetUserData();
        updateUser();
    }

    public LiveData<Setting> getSettings() {
        return settings;
    }

    public void saveLoadLimit(int limit) {
        Setting settings = this.settings.getValue();
        if (settings != null && !settings.isLimited())
            return;
        if (limit <= 0) {
            setLimited(false);
            return;
        }
        else setLimited(true);

        backgroundThread.execute(() -> settingsRepository.setLoadLimit(limit));
    }

    public void setLimited(boolean limited) {
        backgroundThread.execute(() -> settingsRepository.setLimited(limited));
    }

    public void syncFromLocal() {
        firestoreHelper.syncFromLocal();
    }

    public void syncFromOnline() {
        firestoreHelper.syncFromOnline();
    }

    public void resetUserData() {
        backgroundThread.execute(() -> {
            userPrimeRepository.resetPrimes();
            userComponentRepository.resetComponents();
        });
    }
}
