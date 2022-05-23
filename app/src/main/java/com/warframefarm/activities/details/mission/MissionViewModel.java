package com.warframefarm.activities.details.mission;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.BountyRewardComplete;
import com.warframefarm.database.CacheRewardComplete;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.database.MissionWithRewardTypes;

import java.util.List;

public class MissionViewModel extends AndroidViewModel {

    private final MissionRepository repository;

    public MissionViewModel(@NonNull Application application) {
        super(application);
        repository = new MissionRepository(application);
    }

    public void setMission(String name) {
        repository.setMission(name);
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

    public void switchFilter() {
        repository.switchFilter();
    }

    public LiveData<Boolean> getFilter() {
        return repository.getFilter();
    }

    public LiveData<MissionWithRewardTypes> getMission() {
        return repository.getMission();
    }

    public LiveData<List<MissionRewardComplete>> getMissionRewards() {
        return repository.getMissionRewards();
    }

    public LiveData<List<BountyRewardComplete>> getBountyRewards() {
        return repository.getBountyRewards();
    }

    public LiveData<List<CacheRewardComplete>> getCacheRewards() {
        return repository.getCacheRewards();
    }
}
