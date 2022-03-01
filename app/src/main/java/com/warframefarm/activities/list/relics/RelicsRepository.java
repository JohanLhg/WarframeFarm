package com.warframefarm.activities.list.relics;

import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ERA;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_ID;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_NEEDED;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.RELIC_VAULTED;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_PART;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RARITY;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.R_REWARD_TABLE;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_ID;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_OWNED;
import static com.warframefarm.database.WarframeFarmDatabase.USER_PART_TABLE;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.warframefarm.AppExecutors;
import com.warframefarm.database.PartDao;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class RelicsRepository {

    private final RelicDao relicDao;
    private final PartDao partDao;

    private final Executor backgroundThread, mainThread;

    private final LiveData<List<String>> partIDs;
    private String search = "", order = RELIC_NEEDED;
    private final MutableLiveData<String> filter = new MutableLiveData<>("");
    private final List<String> orderValues = new ArrayList<>();

    private final MutableLiveData<LiveData<List<RelicComplete>>> relics = new MutableLiveData<>();

    public RelicsRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        relicDao = database.relicDao();
        partDao = database.partDao();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();

        partIDs = partDao.getPartIDs();

        Collections.addAll(orderValues,
                RELIC_NEEDED,
                RELIC_VAULTED,
                RELIC_ERA
        );
    }

    public LiveData<List<String>> getPartIDs() {
        return partIDs;
    }

    public void setSearch(String search) {
        this.search = search;
        updateRelics();
    }

    public void setOrder(int position) {
        order = orderValues.get(position);
        updateRelics();
    }

    public LiveData<String> getFilter() {
        return filter;
    }

    public void setFilter(String newFilter) {
        if (filter.getValue() != null && filter.getValue().equals(newFilter))
            newFilter = "";
        filter.setValue(newFilter);
        updateRelics();
    }

    public LiveData<LiveData<List<RelicComplete>>> getRelics() {
        return relics;
    }

    public void updateRelics() {
        backgroundThread.execute(() -> {
            String queryString = "SELECT " + RELIC_TABLE + ".*, COALESCE(" + R_REWARD_RARITY + ", 0) AS " + R_REWARD_RARITY +
                    " FROM " + RELIC_TABLE +
                    " LEFT JOIN (" +
                    " SELECT " + R_REWARD_RELIC + ", MAX(" + R_REWARD_RARITY + ", 0) AS " + R_REWARD_RARITY +
                    " FROM " + R_REWARD_TABLE +
                    " LEFT JOIN " + USER_PART_TABLE + " ON " + USER_PART_ID + " == " + R_REWARD_PART +
                    " WHERE " + USER_PART_OWNED + " == 0" +
                    " GROUP BY " + R_REWARD_RELIC +
                    ") ON " + R_REWARD_RELIC + " == " + RELIC_ID;

            boolean hasCondition = false;
            if (!search.equals("")) {
                queryString += " WHERE (" + RELIC_NAME + " LIKE '" + search + "%'" +
                        " OR " + RELIC_ID + " IN (" +
                        " SELECT " + R_REWARD_RELIC +
                        " FROM " + R_REWARD_TABLE +
                        " WHERE " + R_REWARD_PART + " LIKE '" + search + "%'" +
                        "))";
                hasCondition = true;
            }

            String filter = this.filter.getValue();
            if (!filter.equals("")) {
                if (hasCondition)
                    queryString += " AND " + RELIC_ERA + " == '" + filter + "'";
                else
                    queryString += " WHERE " + RELIC_ERA + " == '" + filter + "'";
            }

            switch (order) {
                case RELIC_ERA:
                    queryString += " ORDER BY " + RELIC_ERA + " == '" + AXI + "', " +
                            RELIC_ERA + " == '" + NEO + "', " +
                            RELIC_ERA + " == '" + MESO + "', " +
                            RELIC_ERA + " == '" + LITH + "', " +
                            RELIC_NAME;
                    break;

                case RELIC_VAULTED:
                    queryString += " ORDER BY " + RELIC_VAULTED + ", " +
                            RELIC_ERA + " == '" + AXI + "', " +
                            RELIC_ERA + " == '" + NEO + "', " +
                            RELIC_ERA + " == '" + MESO + "', " +
                            RELIC_ERA + " == '" + LITH + "', " +
                            RELIC_NAME;
                    break;

                case RELIC_NEEDED:
                    queryString += " ORDER BY " + R_REWARD_RARITY + " == 0 DESC, " +
                            RELIC_VAULTED + " , " +
                            RELIC_ERA + " == '" + AXI + "', " +
                            RELIC_ERA + " == '" + NEO + "', " +
                            RELIC_ERA + " == '" + MESO + "', " +
                            RELIC_ERA + " == '" + LITH + "', " +
                            R_REWARD_RARITY + ", " +
                            RELIC_NAME;
            }

            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
            LiveData<List<RelicComplete>> relicList = relicDao.getRelics(query);

            mainThread.execute(() -> relics.setValue(relicList));
        });
    }
}
