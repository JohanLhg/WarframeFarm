package com.warframefarm.activities.farm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.Item;
import com.warframefarm.database.Mission;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.repositories.FarmRepository;

import java.util.List;

public class FarmViewModel extends AndroidViewModel {

    private final FarmRepository repository;

    public FarmViewModel(@NonNull Application application) {
        super(application);
        repository = FarmRepository.getInstance(application);
    }

    public LiveData<Integer> getMode() {
        return repository.getMode();
    }

    public LiveData<Boolean> getRelicFilter() {
        return repository.getRelicFilter();
    }

    public LiveData<Boolean> getMissionFilter() {
        return repository.getMissionFilter();
    }

    public LiveData<List<Item>> getItems() {
        return repository.getItems();
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return repository.getRelics();
    }

    public LiveData<List<Mission>> getMissions() {
        return repository.getMissions();
    }

    public void setMode(int mode) {
        repository.setMode(mode);
    }

    public void checkFilter() {
        repository.checkFilter();
    }

    public void clearItems() {
        repository.clearItems();
    }

    public void addAllNeededItems() {
        repository.addAllNeededItems();
    }

    public void removeItem(Item item) {
        repository.removeItem(item);
    }

    public void updateRelics() {
        repository.updateRelics();
    }

    public void updateMissions() {
        repository.updateMissions();
    }
}
