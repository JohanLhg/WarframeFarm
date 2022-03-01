package com.warframefarm.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.warframefarm.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;

@Database(entities = {App.class, Prime.class, Part.class, Relic.class, RelicReward.class, Planet.class,
        Mission.class, MissionReward.class, UserPrime.class, UserPart.class, Setting.class}, version = 1)
public abstract class WarframeFarmDatabase extends RoomDatabase {

    private static WarframeFarmDatabase instance;

    private static Executor backgroundThread;

    //region Names
    //App Table
    public static final String APP_TABLE = "APP_TABLE";
    public static final String APP_ID = "app_id";
    public static final String APP_BUILD = "app_build";

    //Primes Table
    public static final String PRIME_TABLE = "PRIME_TABLE";
    public static final String PRIME_NAME = "prime_name";
    public static final String PRIME_TYPE = "prime_type";
    public static final String PRIME_VAULTED = "prime_vaulted";

    //Parts Table
    public static final String PART_TABLE = "PART_TABLE";
    public static final String PART_ID = "part_id";
    public static final String PART_PRIME = "part_prime";
    public static final String PART_COMPONENT = "part_component";
    public static final String PART_NEEDED = "part_needed";

    //Relics Table
    public static final String RELIC_TABLE = "RELIC_TABLE";
    public static final String RELIC_ID = "relic_id";
    public static final String RELIC_ERA = "relic_era";
    public static final String RELIC_NAME = "relic_name";
    public static final String RELIC_VAULTED = "relic_vaulted";

    //Relics Rewards Table
    public static final String R_REWARD_TABLE = "RELIC_REWARD_TABLE";
    public static final String R_REWARD_ID = "r_reward_id";
    public static final String R_REWARD_RELIC = "r_reward_relic";
    public static final String R_REWARD_PART = "r_reward_part";
    public static final String R_REWARD_RARITY = "r_reward_rarity";

    //Planets Table
    public static final String PLANET_TABLE = "PLANET_TABLE";
    public static final String PLANET_NAME = "planet_name";
    public static final String PLANET_FACTION = "planet_faction";

    //Missions Table
    public static final String MISSION_TABLE = "MISSION_TABLE";
    public static final String MISSION_NAME = "mission_name";
    public static final String MISSION_PLANET = "mission_planet";
    public static final String MISSION_OBJECTIVE = "mission_objective";
    public static final String MISSION_FACTION = "mission_faction";
    public static final String MISSION_TYPE = "mission_type";

    //Mission Rewards Table
    public static final String M_REWARD_TABLE = "MISSION_REWARD_TABLE";
    public static final String M_REWARD_ID = "m_reward_id";
    public static final String M_REWARD_MISSION = "m_reward_mission";
    public static final String M_REWARD_RELIC = "m_reward_relic";
    public static final String M_REWARD_ROTATION = "m_reward_rotation";
    public static final String M_REWARD_DROP_CHANCE = "m_reward_drop_chance";

    //region User Tables
    //User Primes Table
    public static final String USER_PRIME_TABLE = "USER_PRIME_TABLE";
    public static final String USER_PRIME_NAME = "user_prime_name";
    public static final String USER_PRIME_OWNED = "user_prime_owned";

    //User Parts Table
    public static final String USER_PART_TABLE = "USER_PART_TABLE";
    public static final String USER_PART_ID = "user_part_id";
    public static final String USER_PART_OWNED = "user_part_owned";

    //User Settings Table
    public static final String SETTINGS_TABLE = "SETTINGS_TABLE";
    public static final String SETTINGS_ID = "settings_id";
    public static final String SETTINGS_LOAD_LIMIT = "settings_load_limit";
    public static final String SETTINGS_LIMITED = "settings_limited";
    //endregion
    //endregion

    //Constants
    public static final int TYPE_NORMAL = 0, TYPE_ARCHWING = 1, TYPE_EMPYREAN = 2;
    public static final int REWARD_COMMON = 1, REWARD_UNCOMMON = 2, REWARD_RARE = 3;

    //Special filters
    public static final String RELIC_NEEDED = "relic_needed", ITEM_NEEDED = "item_needed",
            BEST_PLACES = "best_places", DROP_CHANCE = "drop_chance";

    public abstract AppDao appDao();
    public abstract PrimeDao primeDao();
    public abstract PartDao partDao();
    public abstract RelicDao relicDao();
    public abstract RelicRewardDao relicRewardDao();
    public abstract PlanetDao planetDao();
    public abstract MissionDao missionDao();
    public abstract MissionRewardDao missionRewardDao();
    public abstract UserPrimeDao userPrimeDao();
    public abstract UserPartDao userPartDao();
    public abstract SettingDao settingDao();

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            backgroundThread.execute(() -> {
                instance.setUpApp();
                instance.setUpSettings();
            });
        }
    };

    public static synchronized WarframeFarmDatabase getInstance(Context context) {
        if (instance == null) {
            backgroundThread = new AppExecutors().getBackgroundThread();
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    WarframeFarmDatabase.class, "warframe_farm")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    //App
    public void setUpApp() {
        AppDao appDao = appDao();

        appDao.insert(new App(0, 0));
    }

    //Relic
    public void setUpRelics(String json) {
        RelicDao relicDao = relicDao();
        RelicRewardDao relicRewardDao = relicRewardDao();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray relicArray = jsonObject.getJSONArray("relics");

            JSONObject relic;
            String prev_name = "", name, era, relic_id;

            JSONArray rewards;
            String part;
            int rarity;
            for (int i = 0; i < relicArray.length(); i++) {
                relic = (JSONObject) relicArray.get(i);

                name = relic.getString("relicName");
                if (name.equals(prev_name))
                    continue;
                prev_name = name;

                era = relic.getString("tier");
                if (era.equals("Requiem"))
                    continue;
                era = era.toUpperCase();

                rewards = relic.getJSONArray("rewards");

                relic_id = era.charAt(0) + name;

                relicDao.insert(new Relic(relic_id, era, name));

                for (int j = 0; j < rewards.length(); j++) {
                    JSONObject reward = (JSONObject) rewards.get(j);

                    part = reward.getString("itemName");

                    part = part.replace(" Kubrow Collar", "");
                    part = part.replace("Blades", "Blade");

                    if (part.contains(" Prime Blueprint"))
                        part = part.replace(" Prime Blueprint", " Blueprint");
                    else {
                        part = part.replace(" Blueprint", "");
                        part = part.replace(" Prime", "");
                    }

                    String chance = String.valueOf(reward.getDouble("chance"));
                    switch (chance) {
                        case "11.0":
                            rarity = REWARD_UNCOMMON;
                            break;
                        case "2.0":
                            rarity = REWARD_RARE;
                            break;
                        default:
                            rarity = REWARD_COMMON;
                            break;
                    }

                    relicRewardDao.insert(new RelicReward(relic_id, part, rarity));
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    //region Mission Rewards
    public void setUpMissionRewards(String json) {
        PlanetDao planetDao = planetDao();
        MissionDao missionDao = missionDao();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject missionRewards = jsonObject.getJSONObject("missionRewards");

            List<String> planets = planetDao.getPlanetNames();
            assert planets != null;
            for (String p : planets) {
                JSONObject planet = missionRewards.getJSONObject(p);

                List<String> missions = missionDao.getPlanetMissions(p);
                assert missions != null;
                for (String mission_name : missions) {
                    if (planet.has(mission_name)) {
                        JSONObject mission = planet.getJSONObject(mission_name);

                        Object r = mission.get("rewards");
                        //If it has rotations
                        if (r instanceof JSONObject) {
                            JSONObject rewards = (JSONObject) r;

                            JSONArray rewardsA = rewards.getJSONArray("A");
                            generateRewardFromArray(rewardsA, mission_name, "A");

                            JSONArray rewardsB = rewards.getJSONArray("B");
                            generateRewardFromArray(rewardsB, mission_name, "B");

                            JSONArray rewardsC = rewards.getJSONArray("C");
                            generateRewardFromArray(rewardsC, mission_name, "C");
                        } else {
                            JSONArray rewards = (JSONArray) r;
                            generateRewardFromArray(rewards, mission_name, "Z");
                        }
                    }
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }

        relicDao().setVaultStates();
        primeDao().setVaultStates();
    }

    public void generateRewardFromArray(JSONArray rewards, String mission_name, String rotation) {
        MissionRewardDao missionRewardDao = missionRewardDao();

        for (int i = 0; i < rewards.length(); i++) {
            try {
                JSONObject reward = rewards.getJSONObject(i);

                String name = reward.getString("itemName");
                if (!name.endsWith("Relic"))
                    continue;
                name = name.replace(" Relic", "");
                int firstSpace = name.indexOf(" ");
                String relic = name.substring(firstSpace + 1);

                relic = name.charAt(0) + relic;

                double chance = reward.getDouble("chance");
                missionRewardDao.insert(new MissionReward(mission_name, relic, rotation, chance));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

    //Settings
    public void setUpSettings() {
        SettingDao settingDao = instance.settingDao();
        settingDao.insert(new Setting(20, false));
    }
}
