package com.warframefarm.activities.startup;

import static com.warframefarm.data.FirestoreHelper.CHECK_FOR_NEW_USER_DATA;
import static com.warframefarm.data.FirestoreHelper.CHECK_FOR_OFFLINE_CHANGES;
import static com.warframefarm.data.FirestoreHelper.CHECK_FOR_UPDATES;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.CommunicationHandler;
import com.warframefarm.R;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartUpRepository implements CommunicationHandler {

    private final WarframeFarmDatabase database;
    private final FirestoreHelper firestoreHelper;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    private final List<Integer> queue = new ArrayList<>();
    private final MutableLiveData<Integer> loadingTextRes = new MutableLiveData<>(-1);

    public StartUpRepository(Application application) {
        database = WarframeFarmDatabase.getInstance(application.getApplicationContext());

        firestoreHelper = new FirestoreHelper(application, this);

        if (FirestoreHelper.isConnectedToInternet(application.getApplicationContext())) {
            Collections.addAll(queue, CHECK_FOR_UPDATES, CHECK_FOR_NEW_USER_DATA, CHECK_FOR_OFFLINE_CHANGES);
            startAction();
        }
        else loading.setValue(false);
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Integer> getLoadingTextRes() {
        return loadingTextRes;
    }

    public void startAction() {
        int action = queue.get(0);
        switch (action) {
            case CHECK_FOR_UPDATES:
                loadingTextRes.setValue(R.string.checking_updates);
                firestoreHelper.checkForUpdates();
                break;
            case CHECK_FOR_NEW_USER_DATA:
                loadingTextRes.setValue(R.string.updating_user);
                firestoreHelper.checkForNewUserData();
                break;
            case CHECK_FOR_OFFLINE_CHANGES:
                finishAction(CHECK_FOR_OFFLINE_CHANGES);
                break;
        }
    }

    @Override
    public void startAction(Integer actionID) {

    }

    @Override
    public void finishAction(Integer actionID) {
        loadingTextRes.setValue(-1);
        queue.remove(actionID);
        if (queue.isEmpty())
            loading.setValue(false);
        else startAction();
    }
}
