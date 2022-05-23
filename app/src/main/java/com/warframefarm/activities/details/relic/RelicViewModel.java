package com.warframefarm.activities.details.relic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.Mission;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicRewardComplete;

import java.util.List;

public class RelicViewModel extends AndroidViewModel {

    private final RelicRepository repository;

    public RelicViewModel(@NonNull Application application) {
        super(application);
        repository = new RelicRepository(application);
    }

    public void setRelic(String id) {
        repository.setRelic(id);
    }

    public void setMode(int mode) {
        repository.setMode(mode);
    }

    public LiveData<Integer> getMode() {
        return repository.getMode();
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void setFilter(int filterPos) {
        repository.setFilter(filterPos);
    }

    public LiveData<RelicComplete> getRelic() {
        return repository.getRelic();
    }

    public LiveData<List<RelicRewardComplete>> getRewards() {
        return repository.getRewards();
    }

    public LiveData<List<Mission>> getMissions() {
        return repository.getMissions();
    }
}
