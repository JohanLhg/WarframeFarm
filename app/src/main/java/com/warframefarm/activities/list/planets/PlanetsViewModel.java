package com.warframefarm.activities.list.planets;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.Planet;

import java.util.List;

public class PlanetsViewModel extends AndroidViewModel {

    private final PlanetsRepository repository;

    public PlanetsViewModel(Application application) {
        super(application);
        repository = new PlanetsRepository(application);
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public LiveData<List<Planet>> getPlanets() {
        return repository.getPlanets();
    }
}