package com.warframefarm.repositories;

import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.Component;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Item;
import com.warframefarm.database.Mission;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.RelicComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class FarmRepository {

    private static FarmRepository instance;

    private final PrimeRepository primeRepository;
    private final ComponentRepository componentRepository;
    private final RelicRepository relicRepository;
    private final MissionRepository missionRepository;

    private final Executor backgroundThread, mainThread;

    //region Fragment
    public static final int RELIC = 0, MISSION = 1;
    private final MutableLiveData<Integer> mode = new MutableLiveData<>(RELIC);
    private final MutableLiveData<Boolean> relicFilter = new MutableLiveData<>(false),
            missionFilter = new MutableLiveData<>(false);

    private final MutableLiveData<List<Item>> items = new MutableLiveData<>(new ArrayList<>());
    private final List<String> itemNames = new ArrayList<>();
    private final MutableLiveData<List<RelicComplete>> relics = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Mission>> missions = new MutableLiveData<>(new ArrayList<>());
    //endregion

    //Dialogs
    private final MutableLiveData<String> dialogFilter = new MutableLiveData<>("");
    private String dialogSearch = "", dialogOrder = PRIME_VAULTED;
    private final MutableLiveData<List<PrimeComplete>> remainingPrimes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ComponentComplete>> remainingComponents = new MutableLiveData<>(new ArrayList<>());

    private FarmRepository(Application application) {
        primeRepository = new PrimeRepository(application);
        componentRepository = new ComponentRepository(application);
        relicRepository = new RelicRepository(application);
        missionRepository = new MissionRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        addAllNeededItems();
    }

    public static FarmRepository getInstance(Application application) {
        if (instance == null)
            instance = new FarmRepository(application);
        return instance;
    }

    public LiveData<Integer> getMode() {
        return mode;
    }

    public LiveData<Boolean> getRelicFilter() {
        return relicFilter;
    }

    public LiveData<Boolean> getMissionFilter() {
        return missionFilter;
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public LiveData<List<RelicComplete>> getRelics() {
        return relics;
    }

    public LiveData<List<Mission>> getMissions() {
        return missions;
    }

    public LiveData<List<PrimeComplete>> getRemainingPrimes() {
        return remainingPrimes;
    }

    public LiveData<List<ComponentComplete>> getRemainingComponents() {
        return remainingComponents;
    }

    public LiveData<String> getDialogFilter() {
        return dialogFilter;
    }

    public void setDialogSearch(String search) {
        dialogSearch = search;
        updateRemainingPrimes();
        updateRemainingComponents();
    }

    public void setDialogFilter(String filter) {
        if (dialogFilter.getValue().equals(filter))
            dialogFilter.setValue("");
        else
            dialogFilter.setValue(filter);
        updateRemainingPrimes();
        updateRemainingComponents();
    }

    public void setDialogOrder(String order) {
        dialogOrder = order;
        updateRemainingPrimes();
        updateRemainingComponents();
    }

    public void setMode(int mode) {
        this.mode.setValue(mode);
        updateResults();
    }

    public void checkFilter() {
        if (mode.getValue() == RELIC) {
            relicFilter.setValue(!relicFilter.getValue());
            updateRelics();
        }
        else if (mode.getValue() == MISSION) {
            missionFilter.setValue(!missionFilter.getValue());
            updateMissions();
        }
    }

    public void clearItems() {
        items.setValue(new ArrayList<>());
        itemNames.clear();
        updateResults();
    }

    public void addAllNeededItems() {
        backgroundThread.execute(() -> {
            List<ComponentComplete> neededItems = getNeededComponents();

            if (neededItems.isEmpty()) return;

            String itemName;
            for (Item item : neededItems) {
                itemName = item.getId();

                if (itemNames.contains(itemName) || itemName.equals("") ||
                        (item instanceof Component && itemNames.contains(((Component) item).getPrime())))
                    continue;

                itemNames.add(itemName);
                List<Item> newList = items.getValue();
                newList.add(item);
                mainThread.execute(() -> items.setValue(newList));
            }
            updateResults();
        });
    }

    public void addPrimes(List<String> primeNames, List<PrimeComplete> primes) {
        itemNames.addAll(primeNames);
        List<Item> newList = items.getValue();
        newList.addAll(primes);
        items.setValue(newList);

        updateResults();
    }

    public void addComponents(List<String> componentNames, List<ComponentComplete> components) {
        itemNames.addAll(componentNames);
        List<Item> newList = items.getValue();
        newList.addAll(components);
        items.setValue(newList);

        updateResults();
    }

    public void removeItem(Item item) {
        itemNames.remove(item.getId());
        List<Item> newList = items.getValue();
        newList.remove(item);
        items.setValue(newList);

        updateResults();
    }

    public List<String> getSelectedPrimeNames() {
        List<String> names = new ArrayList<>();
        for (Item item : items.getValue()) {
            if (item instanceof PrimeComplete)
                names.add(item.getId());
        }
        return names;
    }

    public void updateResults() {
        if (mode.getValue() == RELIC)
            updateRelics();
        else if (mode.getValue() == MISSION)
            updateMissions();
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            List<RelicComplete> relicList = getRelicsForItems(itemNames, relicFilter.getValue());
            mainThread.execute(() -> relics.setValue(relicList));
        });
    }

    public void updateMissions() {
        backgroundThread.execute(() -> {
            List<Mission> missionList = getMissionsForItems(itemNames, missionFilter.getValue());
            mainThread.execute(() -> missions.setValue(missionList));
        });
    }

    public void updateRemainingPrimes() {
        backgroundThread.execute(() -> {
            List<PrimeComplete> primes = primeRepository.getPrimesNotIn(getSelectedPrimeNames(), dialogFilter.getValue(), dialogSearch, dialogOrder);

            mainThread.execute(() -> remainingPrimes.setValue(primes));
        });
    }

    public void updateRemainingComponents() {
        backgroundThread.execute(() -> {
            List<ComponentComplete> components = componentRepository.getComponentsNotIn(items.getValue(), dialogFilter.getValue(), dialogSearch, dialogOrder);

            mainThread.execute(() -> remainingComponents.setValue(components));
        });
    }

    public List<ComponentComplete> getNeededComponents() {
        return componentRepository.getNeededComponents();
    }

    public List<RelicComplete> getRelicsForItems(List<String> items, boolean showVaulted) {
        return relicRepository.getRelicsForItems(items, showVaulted);
    }

    public List<Mission> getMissionsForItems(List<String> items, boolean bestPlaces) {
        return missionRepository.getMissionsForItems(items, bestPlaces);
    }
}
