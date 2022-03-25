package com.warframefarm.activities.list.relics;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.RelicComplete;

import java.util.List;

public class RelicsViewModel extends AndroidViewModel {

    private final RelicsRepository repository;

    public RelicsViewModel(Application application) {
        super(application);
        repository = new RelicsRepository(application);
    }

    public LiveData<List<String>> getComponentIDs() {
        return repository.getComponentIDs();
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void setOrder(int position) {
        repository.setOrder(position);
    }

    public LiveData<String> getFilter() {
        return repository.getFilter();
    }

    public void setFilter(String filter) {
        repository.setFilter(filter);
    }

    public LiveData<LiveData<List<RelicComplete>>> getRelics() {
        return repository.getRelics();
    }
}