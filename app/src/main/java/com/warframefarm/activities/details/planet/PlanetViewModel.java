package com.warframefarm.activities.details.planet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.Planet;

import java.util.List;

public class PlanetViewModel extends AndroidViewModel {

    private final PlanetRepository repository;

    public PlanetViewModel(@NonNull Application application) {
        super(application);
        repository = new PlanetRepository(application);
    }

    public void setPlanet(String name) {
        repository.setPlanet(name);
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void switchFilter() {
        repository.switchFilter();
    }

    public LiveData<Boolean> getFilter() {
        return repository.getFilter();
    }

    public LiveData<Planet> getPlanet() {
        return repository.getPlanet();
    }

    public LiveData<List<MissionComplete>> getMissions() {
        return repository.getMissions();
    }
}
