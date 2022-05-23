package com.warframefarm.activities.details.prime;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Mission;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.RelicComplete;

import java.util.List;

public class PrimeViewModel extends AndroidViewModel {

    private final PrimeRepository repository;

    public PrimeViewModel(@NonNull Application application) {
        super(application);
        repository = new PrimeRepository(application);
    }

    public void setPrime(String prime) {
        repository.setPrime(prime);
    }

    public void setSearch(String search) {
        repository.setSearch(search);
    }

    public void setFilter(int filterPos) {
        repository.setFilter(filterPos);
    }

    public int getRelicFilterPos() {
        return repository.getRelicFilterPos();
    }

    public int getMissionFilterPos() {
        return repository.getMissionFilterPos();
    }

    public LiveData<PrimeComplete> getPrime() {
        return repository.getPrime();
    }

    public void setMode(int mode) {
        repository.setMode(mode);
    }

    public LiveData<Integer> getMode() {
        return repository.getMode();
    }

    public LiveData<List<ComponentComplete>> getComponents() {
        return repository.getComponents();
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return repository.getRelics();
    }

    public LiveData<List<Mission>> getMissions() {
        return repository.getMissions();
    }

    public void updateResults() {
        repository.updateResults();
    }

    public void updateComponents() {
        repository.updateComponents();
    }

    public void switchPrimeOwned() {
        repository.switchPrimeOwned();
    }

    public void switchComponentOwned(ComponentComplete component) {
        repository.switchComponentOwned(component);
    }
}
