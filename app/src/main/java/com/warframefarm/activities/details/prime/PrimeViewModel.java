package com.warframefarm.activities.details.prime;

import static com.warframefarm.database.WarframeFarmDatabase.BEST_PLACES;
import static com.warframefarm.database.WarframeFarmDatabase.COMPONENT_ID;
import static com.warframefarm.database.WarframeFarmDatabase.DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Mission;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.repositories.ComponentRepository;
import com.warframefarm.repositories.PrimeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class PrimeViewModel extends AndroidViewModel {

    private final PrimeRepository primeRepository;
    private final ComponentRepository componentRepository;

    private final Executor backgroundThread, mainThread;

    private LiveData<PrimeComplete> prime = new MutableLiveData<>();

    public static final int COMPONENT = 0, RELIC = 1, MISSION = 2;
    private final List<String> relicFilterValues = new ArrayList<>(), missionFilterValues = new ArrayList<>();
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(COMPONENT);
    private String search = "", relicFilter = COMPONENT_ID, missionFilter = BEST_PLACES;

    private LiveData<List<ComponentComplete>> components = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<RelicComplete>> relics = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Mission>> missions = new MutableLiveData<>(new ArrayList<>());

    public PrimeViewModel(@NonNull Application application) {
        super(application);
        primeRepository = new PrimeRepository(application);
        componentRepository = new ComponentRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(relicFilterValues,
                COMPONENT_ID,
                RELIC_ID
        );

        Collections.addAll(missionFilterValues,
                BEST_PLACES,
                DROP_CHANCE,
                MISSION_OBJECTIVE,
                PLANET_NAME
        );
    }

    public void setPrime(String name) {
        prime = primeRepository.getPrime(name);
        components = primeRepository.getComponents(name);
    }

    public LiveData<PrimeComplete> getPrime() {
        return prime;
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

    public int getModeValue() {
        if (mode.getValue() == null)
            return COMPONENT;
        else return mode.getValue();
    }

    public void setSearch(String search) {
        this.search = search;
        updateResults();
    }

    public void setFilter(int filterPos) {
        int m = getModeValue();
        if (m == RELIC)
            setRelicFilter(filterPos);
        else if (m == MISSION)
            setMissionFilter(filterPos);
    }

    public void setRelicFilter(int filterPos) {
        relicFilter = relicFilterValues.get(filterPos);
        updateRelics();
    }

    public int getRelicFilterPos() {
        int pos = relicFilterValues.indexOf(relicFilter);
        return pos == -1 ? 0 : pos;
    }

    public void setMissionFilter(int filterPos) {
        missionFilter = missionFilterValues.get(filterPos);
        updateMissions();
    }

    public int getMissionFilterPos() {
        int pos = missionFilterValues.indexOf(missionFilter);
        return pos == -1 ? 0 : pos;
    }

    public LiveData<List<ComponentComplete>> getComponents() {
        return components;
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return relics;
    }

    public LiveData<List<Mission>> getMissions() {
        return missions;
    }

    public void updateResults() {
        int m = getModeValue();
        if (m == RELIC)
            updateRelics();
        else if (m == MISSION)
            updateMissions();
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            PrimeComplete prime = this.prime.getValue();
            if (prime == null)
                return;

            List<RelicComplete> relicList = primeRepository.getRelics(prime.getName(), relicFilter, search);
            mainThread.execute(() -> relics.setValue(relicList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            PrimeComplete prime = this.prime.getValue();
            if (prime == null)
                return;

            List<Mission> missionList = primeRepository.getMissions(prime.getName(), missionFilter, search);
            mainThread.execute(() -> missions.setValue(missionList));
        });
    }

    public void switchPrimeOwned() {
        backgroundThread.execute(() -> {
            PrimeComplete prime = this.prime.getValue();
            if (prime == null)
                return;
            primeRepository.setPrimeOwned(prime.getName(), !prime.isOwned());
        });
    }

    public void switchComponentOwned(ComponentComplete component) {
        backgroundThread.execute(() -> componentRepository.setComponentOwned(component.getId(), component.getPrime(), !component.isOwned()));
    }
}
