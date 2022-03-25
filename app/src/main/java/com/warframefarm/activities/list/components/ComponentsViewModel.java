package com.warframefarm.activities.list.components;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.ComponentComplete;

import java.util.List;

public class ComponentsViewModel extends AndroidViewModel {

    private final ComponentsRepository repository;

    public ComponentsViewModel(@NonNull Application application) {
        super(application);
        repository = ComponentsRepository.getInstance(application);
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void setOrder(int position) {
        repository.setOrder(position);
    }

    public void setFilter(String filter) {
        repository.setFilter(filter);
    }

    public LiveData<String> getFilter() {
        return repository.getFilter();
    }

    public void modifyComponent(ComponentComplete component) {
        repository.modifyComponent(component);
    }

    public void clearChanges() {
        repository.clearChanges();
    }

    public void applyChanges() {
        repository.applyChanges();
    }

    public LiveData<List<ComponentComplete>> getComponentsAfterChanges() {
        return repository.getComponentsAfterChanges();
    }

    public LiveData<LiveData<List<ComponentComplete>>> getComponents() {
        return repository.getComponents();
    }
}
