package com.warframefarm.activities.startup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.warframefarm.repositories.StartUpRepository;

public class StartUpViewModel extends AndroidViewModel {

    private final StartUpRepository repository;

    public StartUpViewModel(@NonNull Application application) {
        super(application);
        repository = new StartUpRepository(application);
    }

    public LiveData<Boolean> isLoading() {
        return repository.isLoading();
    }

    public LiveData<Integer> getLoadingTextRes() {
        return repository.getLoadingTextRes();
    }
}
