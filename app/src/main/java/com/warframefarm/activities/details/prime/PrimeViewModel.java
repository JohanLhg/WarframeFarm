package com.warframefarm.activities.details.prime;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.PartComplete;
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

    public LiveData<List<PartComplete>> getParts() {
        return repository.getParts();
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return repository.getRelics();
    }

    public LiveData<List<MissionComplete>> getMissions() {
        return repository.getMissions();
    }

    public void updateResults() {
        repository.updateResults();
    }

    public void updateParts() {
        repository.updateParts();
    }

    public void switchPrimeOwned() {
        repository.switchPrimeOwned();
    }

    public void switchPartOwned(PartComplete part) {
        repository.switchPartOwned(part);
    }
}
