package com.warframefarm.repositories;

import android.app.Application;

import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.database.RelicRewardDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.util.List;

public class RelicRewardRepository {

    private final RelicRewardDao relicRewardDao;

    public RelicRewardRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        relicRewardDao = database.relicRewardDao();
    }

    public List<RelicRewardComplete> getRelicRewards(String relicID) {
        return relicRewardDao.getRewardsForRelic(relicID);
    }
}
