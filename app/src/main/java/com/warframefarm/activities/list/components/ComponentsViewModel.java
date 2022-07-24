package com.warframefarm.activities.list.components;

import static com.warframefarm.database.WarframeFarmDatabase.ITEM_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_VAULTED;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.repositories.ComponentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class ComponentsViewModel extends AndroidViewModel {

    private final ComponentRepository componentRepository;

    private final Executor backgroundThread, mainThread;

    private String search = "";
    private String order = ITEM_NEEDED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> sortOptionValues = new ArrayList<>();

    private final HashMap<String, Boolean> componentsBeforeChanges = new HashMap<>();
    private final MutableLiveData<List<ComponentComplete>> componentsAfterChanges = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<LiveData<List<ComponentComplete>>> components = new MutableLiveData<>();

    public ComponentsViewModel(@NonNull Application application) {
        super(application);
        componentRepository = new ComponentRepository(application);

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        Collections.addAll(sortOptionValues,
                ITEM_NEEDED,
                PRIME_VAULTED,
                PRIME_TYPE,
                PRIME_NAME
        );
    }

    public void setSearch(String search) {
        this.search = search;
        updateComponents();
    }

    public void setOrder(int position) {
        order = sortOptionValues.get(position);
        updateComponents();
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            filter.setValue("");
        else
            filter.setValue(newFilter);
        updateComponents();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    private String getFilterValue() {
        if (filter.getValue() == null)
            return "";
        else return filter.getValue();
    }

    public void modifyComponent(ComponentComplete component) {
        List<ComponentComplete> componentsAfterChanges = this.componentsAfterChanges.getValue();
        String id = component.getId();
        component.switchOwned();

        if (componentsAfterChanges == null)
            componentsAfterChanges = new ArrayList<>();

        if (componentsBeforeChanges.containsKey(id)) {
            componentsAfterChanges.remove(component);
            if (component.isOwned() == componentsBeforeChanges.get(id))
                componentsBeforeChanges.remove(id);
            else componentsAfterChanges.add(component);
        }
        else {
            componentsBeforeChanges.put(id, !component.isOwned());
            componentsAfterChanges.add(component);
        }

        this.componentsAfterChanges.setValue(componentsAfterChanges);
    }

    public void clearChanges() {
        componentsBeforeChanges.clear();
        componentsAfterChanges.setValue(new ArrayList<>());
    }

    public void applyChanges() {
        backgroundThread.execute(() -> {
            componentRepository.setComponentsOwned(componentsAfterChanges.getValue());
            mainThread.execute(() -> {
                clearChanges();
                updateComponents();
            });
        });
    }

    public LiveData<List<ComponentComplete>> getComponentsAfterChanges() {
        return componentsAfterChanges;
    }

    public LiveData<LiveData<List<ComponentComplete>>> getComponents() {
        return components;
    }

    private void updateComponents() {
        backgroundThread.execute(() -> {
            LiveData<List<ComponentComplete>> components = componentRepository.getComponents(getFilterValue(), search, order);
            mainThread.execute(() -> this.components.setValue(components));
        });
    }
}
