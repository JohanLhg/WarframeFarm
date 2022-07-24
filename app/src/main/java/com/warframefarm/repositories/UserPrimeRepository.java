package com.warframefarm.repositories;

import android.app.Application;

import com.warframefarm.database.UserPrimeDao;
import com.warframefarm.database.WarframeFarmDatabase;

public class UserPrimeRepository {

    private final UserPrimeDao userPrimeDao;

    public UserPrimeRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        userPrimeDao = database.userPrimeDao();
    }

    public void resetPrimes() {
        userPrimeDao.resetPrimes();
    }
}
