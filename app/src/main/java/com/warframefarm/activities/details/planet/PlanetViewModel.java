package com.warframefarm.activities.details.planet;

import static com.warframefarm.database.WarframeFarmDatabase.TYPE_ARCHWING;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_EMPYREAN;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_NORMAL;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.Mission;
import com.warframefarm.database.Planet;
import com.warframefarm.repositories.PlanetRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class PlanetViewModel extends AndroidViewModel {

    private final PlanetRepository planetRepository;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<Planet> planet = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> typeList = new MutableLiveData<>(new ArrayList<>());

    private String search = "";
    private final MutableLiveData<Boolean> relicFilter = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> typeFilter = new MutableLiveData<>(-1);

    private final MutableLiveData<List<Mission>> missions = new MutableLiveData<>(new ArrayList<>());

    public PlanetViewModel(@NonNull Application application) {
        super(application);
        planetRepository = new PlanetRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setPlanet(String name) {
        backgroundThread.execute(() -> {
            Planet planet = planetRepository.getPlanet(name);
            mainThread.execute(() -> {
                this.planet.setValue(planet);
                setTypeList();
                updateMissions();
            });
        });
    }

    public LiveData<Planet> getPlanet() {
        return planet;
    }

    public void setSearch(String search) {
        this.search = search;
        updateMissions();
    }

    public void setTypeList() {
        backgroundThread.execute(() -> {
            Planet planet = this.planet.getValue();
            List<Integer> types = new ArrayList<>();
            if (planet == null)
                Collections.addAll(types, TYPE_NORMAL, TYPE_ARCHWING, TYPE_EMPYREAN);
            else types = planetRepository.getMissionTypes(planet.getName());

            List<Integer> finalTypes = types;
            mainThread.execute(() -> typeList.setValue(finalTypes));
        });
    }

    public LiveData<List<Integer>> getTypeList() {
        return typeList;
    }

    public void switchRelicFilter() {
        relicFilter.setValue(!getRelicFilterValue());
        updateMissions();
    }

    public LiveData<Boolean> getRelicFilter() {
        return relicFilter;
    }

    private boolean getRelicFilterValue() {
        if (relicFilter.getValue() == null)
            return false;
        else return relicFilter.getValue();
    }

    public void setTypeFilter(int type) {
        int currentType = getTypeFilterValue();
        if (currentType != type)
            typeFilter.setValue(type);
        else
            typeFilter.setValue(-1);
        updateMissions();
    }

    public LiveData<Integer> getTypeFilter() {
        return typeFilter;
    }

    private int getTypeFilterValue() {
        if (typeFilter.getValue() == null)
            return -1;
        else return typeFilter.getValue();
    }

    public LiveData<List<Mission>> getMissions() {
        return missions;
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            Planet planet = this.planet.getValue();
            if (planet == null)
                return;

            List<Mission> missionList =
                    planetRepository.getMissions(planet.getName(), getRelicFilterValue(), getTypeFilterValue(), search);
            mainThread.execute(() -> missions.setValue(missionList));
        });
    }
}
