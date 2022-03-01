package com.warframefarm.activities.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.PartComplete;

import java.util.List;

public class InventoryViewModel extends AndroidViewModel {

    private final InventoryRepository repository;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = InventoryRepository.getInstance(application);
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

    public void modifyPart(PartComplete part) {
        repository.modifyPart(part);
    }

    public void clearChanges() {
        repository.clearChanges();
    }

    public void applyChanges() {
        repository.applyChanges();
    }

    public LiveData<List<PartComplete>> getPartsAfterChanges() {
        return repository.getPartsAfterChanges();
    }

    public LiveData<LiveData<List<PartComplete>>> getParts() {
        return repository.getParts();
    }
}
