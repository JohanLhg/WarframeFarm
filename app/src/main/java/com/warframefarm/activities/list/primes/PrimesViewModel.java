package com.warframefarm.activities.list.primes;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.PrimeComplete;

import java.util.List;

public class PrimesViewModel extends AndroidViewModel {

    private final PrimesRepository repository;

    public PrimesViewModel(Application application) {
        super(application);
        repository = new PrimesRepository(application);
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

    public LiveData<Boolean> getSelectionCheck() {
        return repository.getSelectionCheck();
    }

    public void setSelectionCheck(boolean check) {
        repository.setSelectionCheck(check);
    }

    public LiveData<Integer> getSelectionNb() {
        return repository.getSelectionNb();
    }

    public void setSelectionNb(int nb) {
        repository.setSelectionNb(nb);
    }

    public LiveData<LiveData<List<PrimeComplete>>> getPrimes() {
        return repository.getPrimes();
    }

    public List<String> getPrimeNames() {
        return repository.getPrimeNames();
    }

    public void updatePrimes() {
        repository.updatePrimes();
    }

    public void switchPrimeOwned(PrimeComplete prime) {
        repository.switchPrimeOwned(prime);
    }

    public void setSelectionOwned(List<String> primes) {
        repository.setSelectionOwned(primes);
    }
}