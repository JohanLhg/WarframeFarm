package com.warframefarm.activities.details.component;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.RelicComplete;

import java.util.List;

public class ComponentViewModel extends AndroidViewModel {

    private final ComponentRepository repository;

    public ComponentViewModel(@NonNull Application application) {
        super(application);
        repository = new ComponentRepository(application);
    }

    public void setComponent(String id) {
        repository.setComponent(id);
    }

    public LiveData<ComponentComplete> getComponent() {
        return repository.getComponent();
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void setFilter(int filterPos) {
        repository.setFilter(filterPos);
    }

    public void updateResults() {
        repository.updateResults();
    }

    public LiveData<Integer> getMode() {
        return repository.getMode();
    }

    public void setMode(int mode) {
        repository.setMode(mode);
    }

    public LiveData<LiveData<List<RelicComplete>>> getRelics() {
        return repository.getRelics();
    }

    public LiveData<List<MissionComplete>> getMissions() {
        return repository.getMissions();
    }

    public void switchOwned() {
        repository.switchOwned();
    }
}
