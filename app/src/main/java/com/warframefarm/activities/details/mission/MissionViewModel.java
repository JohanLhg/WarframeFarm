package com.warframefarm.activities.details.mission;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionRewardComplete;

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

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void switchFilter() {
        repository.switchFilter();
    }

    public LiveData<Boolean> getFilter() {
        return repository.getFilter();
    }

    public LiveData<MissionComplete> getMission() {
        return repository.getMission();
    }

    public LiveData<List<MissionRewardComplete>> getMissionRewards() {
        return repository.getRewards();
    }
}
