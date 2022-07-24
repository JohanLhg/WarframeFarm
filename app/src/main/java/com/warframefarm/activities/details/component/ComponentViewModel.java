package com.warframefarm.activities.details.component;

import static com.warframefarm.database.WarframeFarmDatabase.BEST_PLACES;
import static com.warframefarm.database.WarframeFarmDatabase.DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Mission;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.repositories.ComponentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class ComponentViewModel extends AndroidViewModel {

    private final ComponentRepository componentRepository;

    private final Executor backgroundThread, mainThread;

    private LiveData<ComponentComplete> component = new MutableLiveData<>();

    public static final int RELIC = 0, MISSION = 1;
    private final List<String> filterValues = new ArrayList<>();
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(RELIC);
    private String search = "", filter = BEST_PLACES;

    private final MutableLiveData<LiveData<List<RelicComplete>>> relics = new MutableLiveData<>();
    private final MutableLiveData<List<Mission>> missions = new MutableLiveData<>(new ArrayList<>());

    public ComponentViewModel(@NonNull Application application) {
        super(application);
        componentRepository = new ComponentRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(filterValues,
                BEST_PLACES,
                DROP_CHANCE,
                MISSION_OBJECTIVE,
                PLANET_NAME
        );
    }

    public void setComponent(String id) {
        component = componentRepository.getComponent(id);
    }

    public LiveData<ComponentComplete> getComponent() {
        return component;
    }

    public void setMode(int newMode) {
        if (getModeValue() != newMode) {
            mode.setValue(newMode);
            updateResults();
        }
    }

    public LiveData<Integer> getMode() {
        return mode;
    }

    private int getModeValue() {
        if (mode.getValue() == null)
            return RELIC;
        else return mode.getValue();
    }

    public void setSearch(String search) {
        this.search = search;
        updateResults();
    }

    public void setFilter(int filterPos) {
        filter = filterValues.get(filterPos);
        if (getModeValue() == MISSION)
            updateMissions();
    }

    public LiveData<LiveData<List<RelicComplete>>> getRelics() {
        return relics;
    }

    public LiveData<List<Mission>> getMissions() {
        return missions;
    }

    public void updateResults() {
        int mode = getModeValue();
        if (mode == RELIC)
            updateRelics();
        else if (mode == MISSION)
            updateMissions();
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            ComponentComplete component = this.component.getValue();
            if (component == null)
                return;

            LiveData<List<RelicComplete>> relics = componentRepository.getRelics(component.getId(), search);
            mainThread.execute(() -> this.relics.setValue(relics));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            ComponentComplete component = this.component.getValue();
            if (component == null)
                return;

            List<Mission> missionList = componentRepository.getMissions(component.getId(), filter, search);
            mainThread.execute(() -> missions.setValue(missionList));
        });
    }

    public void switchOwned() {
        backgroundThread.execute(() -> {
            ComponentComplete component = this.component.getValue();
            if (component == null)
                return;

            componentRepository.setComponentOwned(component.getId(), component.getPrime(), !component.isOwned());
        });
    }
}
