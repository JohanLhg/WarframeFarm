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

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Database(entities = {App.class, Prime.class, Component.class, Relic.class, RelicReward.class,
        Planet.class, Mission.class, MissionReward.class, CacheReward.class, BountyReward.class,
        UserPrime.class, UserComponent.class, Setting.class}, version = 1)
public abstract class WarframeFarmDatabase extends RoomDatabase {

    private static WarframeFarmDatabase instance;

    private static Executor backgroundThread;

    //region Names
    //App Table
    public static final String APP_TABLE = "APP_TABLE";
    public static final String APP_ID = "app_id";
    public static final String APP_BUILD = "app_build";
    public static final String APP_API_TIMESTAMP = "app_api_timestamp";

    //Primes Table
    public static final String PRIME_TABLE = "PRIME_TABLE";
    public static final String PRIME_NAME = "prime_name";
    public static final String PRIME_TYPE = "prime_type";
    public static final String PRIME_VAULTED = "prime_vaulted";

    //Components Table
    public static final String COMPONENT_TABLE = "COMPONENT_TABLE";
    public static final String COMPONENT_ID = "component_id";
    public static final String COMPONENT_PRIME = "component_prime";
    public static final String COMPONENT_TYPE = "component_type";
    public static final String COMPONENT_NEEDED = "component_needed";

    //Warframe Table
    public static final String WARFRAME_TABLE = "WARFRAME_TABLE";
    public static final String WARFRAME_NAME = "warframe_name";

    //Lich Weapons Table
    public static final String LICH_WEAPON_TABLE = "WARFRAME_TABLE";
    public static final String LICH_WEAPON_NAME = "lich_weapon_name";
    public static final String LICH_WEAPON_TYPE = "lich_weapon_type";
    public static final String LICH_WEAPON_FACTION = "lich_weapon_faction";

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
    public static final String R_REWARD_COMPONENT = "r_reward_component";
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

    //Cache Rewards Table
    public static final String C_REWARD_TABLE = "CACHE_REWARD_TABLE";
    public static final String C_REWARD_ID = "c_reward_id";
    public static final String C_REWARD_MISSION = "c_reward_mission";
    public static final String C_REWARD_RELIC = "c_reward_relic";
    public static final String C_REWARD_ROTATION = "c_reward_rotation";
    public static final String C_REWARD_DROP_CHANCE = "c_reward_drop_chance";

    //Bounty Rewards Table
    public static final String B_REWARD_TABLE = "BOUNTY_REWARD_TABLE";
    public static final String B_REWARD_ID = "b_reward_id";
    public static final String B_REWARD_MISSION = "b_reward_mission";
    public static final String B_REWARD_LEVEL = "b_reward_level";
    public static final String B_REWARD_STAGE = "b_reward_stage";
    public static final String B_REWARD_RELIC = "b_reward_relic";
    public static final String B_REWARD_ROTATION = "b_reward_rotation";
    public static final String B_REWARD_DROP_CHANCE = "b_reward_drop_chance";

    //region User Tables
    //User Primes Table
    public static final String USER_PRIME_TABLE = "USER_PRIME_TABLE";
    public static final String USER_PRIME_NAME = "user_prime_name";
    public static final String USER_PRIME_OWNED = "user_prime_owned";

    //User Components Table
    public static final String USER_COMPONENT_TABLE = "USER_COMPONENT_TABLE";
    public static final String USER_COMPONENT_ID = "user_component_id";
    public static final String USER_COMPONENT_OWNED = "user_component_owned";

    //User Warframes Table
    public static final String USER_WARFRAME_TABLE = "USER_WARFRAME_TABLE";
    public static final String USER_WARFRAME_NAME = "user_warframe_name";
    public static final String USER_WARFRAME_OWNED = "user_warframe_owned";

    //User Lich Weapons Table
    public static final String USER_LICH_WEAPONS_TABLE = "USER_LICH_WEAPONS_TABLE";
    public static final String USER_LICH_WEAPONS_NAME = "user_lich_weapons_name";
    public static final String USER_LICH_WEAPONS_OWNED = "user_lich_weapons_owned";

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
    public abstract ComponentDao componentDao();
    public abstract RelicDao relicDao();
    public abstract RelicRewardDao relicRewardDao();
    public abstract PlanetDao planetDao();
    public abstract MissionDao missionDao();
    public abstract MissionRewardDao missionRewardDao();
    public abstract CacheRewardDao cacheRewardDao();
    public abstract BountyRewardDao bountyRewardDao();
    public abstract UserPrimeDao userPrimeDao();
    public abstract UserComponentDao userComponentDao();
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

        appDao.insert(new App(0, 0, 0));
    }

    //Relic
    public void setUpRelics(String json) {
        RelicDao relicDao = relicDao();
        RelicRewardDao relicRewardDao = relicRewardDao();

        relicDao.clear();
        relicRewardDao.clear();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray relicArray = jsonObject.getJSONArray("relics");

            JSONObject relic;
            String prev_name = "", name, era, relic_id;

            JSONArray rewards;
            String component;
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

                    component = reward.getString("itemName");

                    component = component.replace(" Kubrow Collar", "");
                    component = component.replace("Blades", "Blade");

                    if (component.contains(" Prime Blueprint"))
                        component = component.replace(" Prime Blueprint", " Blueprint");
                    else {
                        component = component.replace(" Blueprint", "");
                        component = component.replace(" Prime", "");
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

                    relicRewardDao.insert(new RelicReward(relic_id, component, rarity));
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    //region Mission Rewards
    public void setUpMissionRewards(String json) {
        PlanetDao planetDao = planetDao();
        MissionDao missionDao = missionDao();
        MissionRewardDao missionRewardDao = missionRewardDao();
        CacheRewardDao cacheRewardDao = cacheRewardDao();

        missionRewardDao.clear();
        cacheRewardDao.clear();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject missionRewards = jsonObject.getJSONObject("missionRewards");

            List<String> planets = planetDao.getPlanetNames();
            assert planets != null;
            JSONObject planet;
            List<String> missions;
            JSONObject mission;
            Object r;
            JSONArray rewardsA;
            JSONArray rewardsB;
            JSONArray rewardsC;
            for (String p : planets) {
                planet = missionRewards.getJSONObject(p);

                missions = missionDao.getPlanetMissions(p);
                assert missions != null;
                for (String mission_name : missions) {
                    if (planet.has(mission_name)) {
                        mission = planet.getJSONObject(mission_name);

                        r = mission.get("rewards");
                        //If it has rotations
                        if (r instanceof JSONObject) {
                            JSONObject rewards = (JSONObject) r;

                            rewardsA = rewards.getJSONArray("A");
                            generateMissionRewardFromArray(rewardsA, mission_name, "A");

                            rewardsB = rewards.getJSONArray("B");
                            generateMissionRewardFromArray(rewardsB, mission_name, "B");

                            rewardsC = rewards.getJSONArray("C");
                            generateMissionRewardFromArray(rewardsC, mission_name, "C");
                        } else {
                            JSONArray rewards = (JSONArray) r;
                            generateMissionRewardFromArray(rewards, mission_name, "Z");
                        }
                    }
                    if (planet.has(mission_name + " (Caches)")) {
                        mission = planet.getJSONObject(mission_name + " (Caches)");

                        r = mission.get("rewards");
                        //If it has rotations
                        if (r instanceof JSONObject) {
                            JSONObject rewards = (JSONObject) r;

                            rewardsA = rewards.getJSONArray("A");
                            generateCacheRewardFromArray(rewardsA, mission_name, "A");

                            rewardsB = rewards.getJSONArray("B");
                            generateCacheRewardFromArray(rewardsB, mission_name, "B");

                            rewardsC = rewards.getJSONArray("C");
                            generateCacheRewardFromArray(rewardsC, mission_name, "C");
                        } else {
                            JSONArray rewards = (JSONArray) r;
                            generateCacheRewardFromArray(rewards, mission_name, "A");
                        }
                    }
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }

        relicDao().setVaultStates();
        primeDao().setVaultStates();
    }

    public void setUpBountyRewards(HashMap<String, String> bountyRewards) {
        BountyRewardDao bountyRewardDao = bountyRewardDao();
        bountyRewardDao.clear();

        try {
            Set<String> missions = bountyRewards.keySet();
            JSONArray bountiesArray;
            JSONObject bounty;
            String level;
            JSONObject rewards;
            JSONArray rewardsA;
            JSONArray rewardsB;
            JSONArray rewardsC;
            for (String mission : missions) {
                bountiesArray = new JSONArray(bountyRewards.get(mission));

                for (int i = 0; i < bountiesArray.length(); i++) {
                    bounty = bountiesArray.getJSONObject(i);
                    level = bounty.getString("bountyLevel");

                    rewards = bounty.getJSONObject("rewards");

                    rewardsA = rewards.getJSONArray("A");
                    generateBountyRewardFromArray(rewardsA, mission, level, "A");

                    rewardsB = rewards.getJSONArray("B");
                    generateBountyRewardFromArray(rewardsB, mission, level, "B");

                    rewardsC = rewards.getJSONArray("C");
                    generateBountyRewardFromArray(rewardsC, mission, level, "C");
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    public void generateMissionRewardFromArray(JSONArray rewards, String mission_name, String rotation) {
        MissionRewardDao missionRewardDao = missionRewardDao();

        JSONObject reward;
        String name, relic;
        double chance;
        for (int i = 0; i < rewards.length(); i++) {
            try {
                reward = rewards.getJSONObject(i);

                name = reward.getString("itemName");
                if (!name.endsWith("Relic"))
                    continue;
                name = name.replace(" Relic", "");
                int firstSpace = name.indexOf(" ");
                relic = name.substring(firstSpace + 1);

                relic = name.charAt(0) + relic;

                chance = reward.getDouble("chance");

                missionRewardDao.insert(new MissionReward(mission_name, relic, rotation, chance));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateCacheRewardFromArray(JSONArray rewards, String mission_name, String rotation) {
        CacheRewardDao cacheRewardDao = cacheRewardDao();

        JSONObject reward;
        String name, relic;
        double chance;
        for (int i = 0; i < rewards.length(); i++) {
            try {
                reward = rewards.getJSONObject(i);

                name = reward.getString("itemName");
                if (!name.endsWith("Relic"))
                    continue;
                name = name.replace(" Relic", "");
                int firstSpace = name.indexOf(" ");
                relic = name.substring(firstSpace + 1);

                relic = name.charAt(0) + relic;

                chance = reward.getDouble("chance");

                cacheRewardDao.insert(new CacheReward(mission_name, relic, rotation, chance));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateBountyRewardFromArray(JSONArray rewards, String mission_name, String level, String rotation) {
        BountyRewardDao bountyRewardDao = bountyRewardDao();

        JSONObject reward;
        String name, relic, stage;
        double chance;
        for (int i = 0; i < rewards.length(); i++) {
            try {
                reward = rewards.getJSONObject(i);

                name = reward.getString("itemName");
                if (!name.endsWith("Relic"))
                    continue;
                name = name.replace(" Relic", "");
                int firstSpace = name.indexOf(" ");
                relic = name.substring(firstSpace + 1);

                relic = name.charAt(0) + relic;

                stage = reward.getString("stage");

                chance = reward.getDouble("chance");
                bountyRewardDao.insert(new BountyReward(mission_name, relic, level, stage, rotation, chance));
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
