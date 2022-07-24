package com.warframefarm.activities.details.relic;

import static com.warframefarm.database.WarframeFarmDatabase.BEST_PLACES;
import static com.warframefarm.database.WarframeFarmDatabase.DROP_CHANCE;
import static com.warframefarm.database.WarframeFarmDatabase.MISSION_OBJECTIVE;
import static com.warframefarm.database.WarframeFarmDatabase.PLANET_NAME;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.Mission;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.repositories.RelicRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class RelicViewModel extends AndroidViewModel {

    private final RelicRepository relicRepository;

    private final Executor backgroundThread, mainThread;

    private final MutableLiveData<RelicComplete> relic = new MutableLiveData<>();

    public static final int REWARD = 0, MISSION = 1;
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(REWARD);
    private String search = "", filter = BEST_PLACES;
    private final List<String> filterValues = new ArrayList<>();

    private final MutableLiveData<List<RelicRewardComplete>> rewards = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Mission>> missions = new MutableLiveData<>(new ArrayList<>());

    public RelicViewModel(@NonNull Application application) {
        super(application);
        relicRepository = new RelicRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(filterValues,
                BEST_PLACES,
                DROP_CHANCE,
                MISSION_OBJECTIVE,
                PLANET_NAME
        );
    }

    public void setRelic(String id) {
        backgroundThread.execute(() -> {
            RelicComplete r = relicRepository.getRelic(id);
            mainThread.execute(() -> {
                relic.setValue(r);
                updateRewards();
            });
        });
    }

    public MutableLiveData<RelicComplete> getRelic() {
        return relic;
    }

    public void setMode(int newMode) {
        if (getModeValue() != newMode) {
            mode.setValue(newMode);
            if (newMode == MISSION)
                updateMissions();
        }
    }

    public MutableLiveData<Integer> getMode() {
        return mode;
    }

    private int getModeValue() {
        if (mode.getValue() == null)
            return REWARD;
        else return mode.getValue();
    }

    public void setSearch(String search) {
        this.search = search;
        if (getModeValue() == MISSION)
            updateMissions();
    }

    public void setFilter(int filterPos) {
        filter = filterValues.get(filterPos);
        if (getModeValue() == MISSION)
            updateMissions();
    }

    public MutableLiveData<List<RelicRewardComplete>> getRewards() {
        return rewards;
    }

    public MutableLiveData<List<Mission>> getMissions() {
        return missions;
    }

    public void updateRewards() {
        backgroundThread.execute(() -> {
            RelicComplete r = relic.getValue();
            if (r == null)
                return;

            List<RelicRewardComplete> rewardList = relicRepository.getRelicRewards(r.getId());

            mainThread.execute(() -> rewards.setValue(rewardList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            RelicComplete r = relic.getValue();
            if (r == null)
                return;

            List<Mission> missions = relicRepository.getMissions(r.getId(), filter, search);

            mainThread.execute(() -> this.missions.setValue(missions));
        });
    }
}
