package com.warframefarm.activities.farm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.repositories.FarmRepository;

import java.util.List;

public class FarmDialogViewModel extends AndroidViewModel {

    private final FarmRepository repository;

    public FarmDialogViewModel(@NonNull Application application) {
        super(application);
        repository = FarmRepository.getInstance(application);
    }

    public void setSearch(String search) {
        repository.setDialogSearch(search);
    }

    public void setFilter(String filter) {
        repository.setDialogFilter(filter);
    }

    public void setOrder(String order) {
        repository.setDialogOrder(order);
    }

    public void addPrimes(List<String> primeNames, List<PrimeComplete> primes) {
        repository.addPrimes(primeNames, primes);
    }

    public void addComponents(List<String> componentNames, List<ComponentComplete> components) {
        repository.addComponents(componentNames, components);
    }

    public LiveData<List<PrimeComplete>> getRemainingPrimes() {
        return repository.getRemainingPrimes();
    }

    public LiveData<List<ComponentComplete>> getRemainingComponents() {
        return repository.getRemainingComponents();
    }

    public LiveData<String> getFilter() {
        return repository.getDialogFilter();
    }
}
