package com.warframefarm.repositories;

import android.app.Application;

import com.warframefarm.database.UserComponentDao;
import com.warframefarm.database.WarframeFarmDatabase;

public class UserComponentRepository {

    private final UserComponentDao userComponentDao;

    public UserComponentRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        userComponentDao = database.userComponentDao();
    }

    public void resetComponents() {
        userComponentDao.resetComponents();
    }
}
