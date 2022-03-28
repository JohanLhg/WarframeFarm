package com.warframefarm.activities.details.planet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public LiveData<Planet> getPlanet() {
        return repository.getPlanet();
    }

    public LiveData<List<Integer>> getTypeList() {
        return repository.getTypeList();
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void switchRelicFilter() {
        repository.switchRelicFilter();
    }

    public LiveData<Boolean> getRelicFilter() {
        return repository.getRelicFilter();
    }

    public void setTypeFilter(int type) {
        repository.setTypeFilter(type);
    }

    public MutableLiveData<Integer> getTypeFilter() {
        return repository.getTypeFilter();
    }

    public LiveData<List<MissionComplete>> getMissions() {
        return repository.getMissions();
    }
}
