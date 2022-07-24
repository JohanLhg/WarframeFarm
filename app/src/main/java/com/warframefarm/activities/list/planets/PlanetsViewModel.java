package com.warframefarm.activities.list.planets;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.Planet;
import com.warframefarm.repositories.PlanetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PlanetsViewModel extends AndroidViewModel {

    private final PlanetRepository planetRepository;

    private final Executor backgroundThread, mainThread;

    public String search = "";

    private final MutableLiveData<List<Planet>> planets = new MutableLiveData<>(new ArrayList<>());

    public PlanetsViewModel(Application application) {
        super(application);
        planetRepository = new PlanetRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setSearch(String search) {
        this.search = search;
        updatePlanets();
    }

    public LiveData<List<Planet>> getPlanets() {
        return planets;
    }

    public void updatePlanets() {
        backgroundThread.execute(() -> {
            List<Planet> planetList = planetRepository.getPlanets(search);
            mainThread.execute(() -> planets.setValue(planetList));
        });
    }
}