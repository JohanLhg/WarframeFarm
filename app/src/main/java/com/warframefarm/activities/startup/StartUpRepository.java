package com.warframefarm.activities.startup;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.CommunicationHandler;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.List;

public class StartUpRepository implements CommunicationHandler {

    private final WarframeFarmDatabase database;
    private final FirestoreHelper firestoreHelper;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);
    private final List<Integer> actions = new ArrayList<>();

    public final static int UPDATE = 0;

    public StartUpRepository(Application application) {
        database = WarframeFarmDatabase.getInstance(application.getApplicationContext());

        firestoreHelper = new FirestoreHelper(application, this);

        if (FirestoreHelper.isConnectedToInternet(application.getApplicationContext()))
            firestoreHelper.checkForUpdates();
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    @Override
    public void startAction(int actionID) {
        actions.add(actionID);
        System.out.println("Actions in progress: " + actions);
    }

    @Override
    public void finishAction(int actionID) {
        actions.remove((Integer) actionID);
        if (actions.isEmpty())
            loading.setValue(false);
        System.out.println("Actions in progress: " + actions);
    }
}
