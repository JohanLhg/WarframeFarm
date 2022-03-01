package com.warframefarm.activities.details.part;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.PartComplete;
import com.warframefarm.database.RelicComplete;

import java.util.List;

public class PartViewModel extends AndroidViewModel {

    private final PartRepository repository;

    public PartViewModel(@NonNull Application application) {
        super(application);
        repository = new PartRepository(application);
    }

    public void setPart(String id) {
        repository.setPart(id);
    }

    public LiveData<PartComplete> getPart() {
        return repository.getPart();
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
