package com.warframefarm.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.warframefarm.database.Setting;
import com.warframefarm.database.SettingDao;
import com.warframefarm.database.WarframeFarmDatabase;

public class SettingsRepository {

    private final SettingDao settingDao;

    public SettingsRepository(Application application) {
        WarframeFarmDatabase database = WarframeFarmDatabase.getInstance(application);
        settingDao = database.settingDao();
    }

    public LiveData<Setting> getSettings() {
        return settingDao.getSettings();
    }

    public void setLoadLimit(int limit) {
        settingDao.setLoadLimit(limit);
    }

    public void setLimited(boolean limited) {
        settingDao.setLimited(limited);
    }
}
