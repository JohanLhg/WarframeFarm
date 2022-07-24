package com.warframefarm.activities.details.mission;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.BountyRewardComplete;
import com.warframefarm.database.CacheRewardComplete;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.database.MissionWithRewardTypes;
import com.warframefarm.repositories.MissionRepository;
import com.warframefarm.repositories.MissionRewardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MissionViewModel extends AndroidViewModel {

    private final MissionRepository missionRepository;
    private final MissionRewardRepository missionRewardRepository;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<MissionWithRewardTypes> mission = new MutableLiveData<>();

    public static final int REWARDS = 0, BOUNTIES = 1, CACHES = 2;
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(REWARDS);

    private String search = "";
    private final MutableLiveData<Boolean> filter = new MutableLiveData<>(false);

    private final MutableLiveData<List<MissionRewardComplete>> missionRewards = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BountyRewardComplete>> bountyRewards = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CacheRewardComplete>> cacheRewards = new MutableLiveData<>(new ArrayList<>());

    public MissionViewModel(@NonNull Application application) {
        super(application);
        missionRepository = new MissionRepository(application);
        missionRewardRepository = new MissionRewardRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public void setMission(String name) {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes mission = missionRepository.getMission(name);
            int rewardType;
            if (mission.hasRewards())
                rewardType = REWARDS;
            else if (mission.hasBounties())
                rewardType = BOUNTIES;
            else if (mission.hasCaches())
                rewardType = CACHES;
            else rewardType = REWARDS;
            mainThread.execute(() -> {
                this.mission.setValue(mission);
                setMode(rewardType);
                updateRewards();
            });
        });
    }

    public LiveData<MissionWithRewardTypes> getMission() {
        return mission;
    }

    public void setMode(int mode) {
        if (getModeValue() != mode) {
            this.mode.setValue(mode);
            updateRewards();
        }
    }

    public LiveData<Integer> getMode() {
        return mode;
    }

    public int getModeValue() {
        if (mode.getValue() == null)
            return REWARDS;
        else return mode.getValue();
    }

    public void setSearch(String search) {
        this.search = search;
        updateRewards();
    }

    public void switchFilter() {
        filter.setValue(!getFilterValue());
        updateRewards();
    }

    public LiveData<Boolean> getFilter() {
        return filter;
    }

    private boolean getFilterValue() {
        if (filter.getValue() == null) return false;
        else return filter.getValue();
    }

    public LiveData<List<MissionRewardComplete>> getMissionRewards() {
        return missionRewards;
    }

    public LiveData<List<BountyRewardComplete>> getBountyRewards() {
        return bountyRewards;
    }

    public LiveData<List<CacheRewardComplete>> getCacheRewards() {
        return cacheRewards;
    }

    public void updateRewards() {
        switch (getModeValue()) {
            case BOUNTIES: updateBountyRewards(); break;
            case CACHES: updateCacheRewards(); break;
            default: updateMissionRewards(); break;
        }
    }

    public void updateMissionRewards() {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes mission = this.mission.getValue();
            if (mission == null)
                return;

            List<MissionRewardComplete> rewards =
                    missionRewardRepository.getMissionRewards(mission.getName(), getFilterValue(), search);
            mainThread.execute(() -> missionRewards.setValue(rewards));
        });
    }

    public void updateBountyRewards() {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes mission = this.mission.getValue();
            if (mission == null)
                return;

            List<BountyRewardComplete> rewardList =
                    missionRewardRepository.getBountyRewards(mission.getName(), getFilterValue(), search);
            mainThread.execute(() -> bountyRewards.setValue(rewardList));
        });
    }

    public void updateCacheRewards() {
        backgroundThread.execute(() -> {
            MissionWithRewardTypes mission = this.mission.getValue();
            if (mission == null)
                return;

            List<CacheRewardComplete> rewardList =
                    missionRewardRepository.getCacheRewards(mission.getName(), getFilterValue(), search);
            mainThread.execute(() -> cacheRewards.setValue(rewardList));
        });
    }
}
