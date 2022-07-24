package com.warframefarm.activities.list.primes;

import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PRIME_OWNED;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.repositories.PrimeRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class PrimesViewModel extends AndroidViewModel {

    private final PrimeRepository primeRepository;

    private final Executor backgroundThread, mainThread;

    private String search = "", order = USER_PRIME_OWNED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> orderValues = new ArrayList<>();

    private final MutableLiveData<Boolean> selectionCheck = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> selectionNb = new MutableLiveData<>(0);

    private final MutableLiveData<LiveData<List<PrimeComplete>>> primes = new MutableLiveData<>();

    public PrimesViewModel(Application application) {
        super(application);
        primeRepository = new PrimeRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(orderValues,
                USER_PRIME_OWNED,
                PRIME_VAULTED,
                PRIME_TYPE,
                PRIME_NAME
        );
    }

    public void setSearch(String search) {
        this.search = search;
        updatePrimes();
    }

    public void setOrder(int position) {
        this.order = orderValues.get(position);
        updatePrimes();
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            filter.setValue("");
        else
            filter.setValue(newFilter);
        updatePrimes();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    private String getFilterValue() {
        if (filter.getValue() == null)
            return "";
        else return filter.getValue();
    }

    public void setSelectionCheck(boolean check) {
        selectionCheck.setValue(check);
    }

    public LiveData<Boolean> getSelectionCheck() {
        return selectionCheck;
    }

    private boolean getSelectionCheckValue() {
        if (selectionCheck.getValue() == null)
            return true;
        else return selectionCheck.getValue();
    }

    public LiveData<Integer> getSelectionNb() {
        return selectionNb;
    }

    public void setSelectionNb(int nb) {
        selectionNb.setValue(nb);
    }

    public LiveData<LiveData<List<PrimeComplete>>> getPrimes() {
        return primes;
    }

    public List<String> getPrimeNames() {
        List<String> primeNames = new ArrayList<>();
        List<PrimeComplete> primeList = primes.getValue().getValue();

        if (primeList == null)
            return primeNames;

        for (PrimeComplete prime : primeList)
            primeNames.add(prime.getName());

        return primeNames;
    }

    public void updatePrimes() {
        backgroundThread.execute(() -> {
            LiveData<List<PrimeComplete>> primeList = primeRepository.getPrimes(getFilterValue(), search, order);
            mainThread.execute(() -> primes.setValue(primeList));
        });
    }

    public void switchPrimeOwned(PrimeComplete prime) {
        backgroundThread.execute(() -> {
            primeRepository.setPrimeOwned(prime.getName(), !prime.isOwned());
            updatePrimes();
        });
    }

    public void setSelectionOwned(List<String> primes) {
        backgroundThread.execute(() -> {
            primeRepository.setPrimesOwned(primes, getSelectionCheckValue());
            updatePrimes();
        });
    }
}