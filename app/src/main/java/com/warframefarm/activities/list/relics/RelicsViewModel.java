package com.warframefarm.activities.list.relics;

import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.repositories.ComponentRepository;
import com.warframefarm.repositories.RelicRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class RelicsViewModel extends AndroidViewModel {

    private final RelicRepository relicRepository;
    private final ComponentRepository componentRepository;

    private final Executor backgroundThread, mainThread;

    private final LiveData<List<String>> componentIDs;
    private String search = "", order = RELIC_NEEDED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> orderValues = new ArrayList<>();

    private final MutableLiveData<LiveData<List<RelicComplete>>> relics = new MutableLiveData<>();

    public RelicsViewModel(Application application) {
        super(application);
        relicRepository = new RelicRepository(application);
        componentRepository = new ComponentRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        componentIDs = componentRepository.getComponentIDs();

        Collections.addAll(orderValues,
                RELIC_NEEDED,
                RELIC_VAULTED,
                RELIC_ERA
        );
    }

    public LiveData<List<String>> getComponentIDs() {
        return componentIDs;
    }

    public void setSearch(String search) {
        this.search = search;
        updateRelics();
    }

    public void setOrder(int position) {
        order = orderValues.get(position);
        updateRelics();
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            newFilter = "";
        filter.setValue(newFilter);
        updateRelics();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    public LiveData<LiveData<List<RelicComplete>>> getRelics() {
        return relics;
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            LiveData<List<RelicComplete>> relicList = relicRepository.getRelics(filter.getValue(), search, order);

            mainThread.execute(() -> relics.setValue(relicList));
        });
    }
}