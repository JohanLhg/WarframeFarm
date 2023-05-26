package com.warframefarm.data;

import static com.warframefarm.CommunicationHandler.CHECK_FOR_NEW_RELIC_DATA;

import android.app.Application;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import okhttp3.OkHttpClient;

public class RequestAPI extends DataLoader {

    private final static OkHttpClient client = new OkHttpClient();

    private final static String WARFRAME_API_URL = "http://drops.warframestat.us/data";

    public RequestAPI(Application application) {
        super(application);
    }

    public static String sendGet(String url) throws Exception {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new Exception(response.message().isEmpty() ? "Error : " + response.code() : response.message());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void checkRewards() {
        backgroundThread.execute(() -> {
            try {
                String json = sendGet(WARFRAME_API_URL + "/all.json");

                checkMissionRewards(json);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void checkMissionRewards(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject missionRewards = jsonObject.getJSONObject("missionRewards");

            for (Iterator<String> it = missionRewards.keys(); it.hasNext(); ) {
                String planetName = it.next();
                JSONObject planet = missionRewards.getJSONObject(planetName);

                System.out.println("-----------------------" + planetName + "-----------------------");

                for (Iterator<String> it2 = planet.keys(); it2.hasNext(); ) {
                    String missionName = it2.next();
                    JSONObject mission = planet.getJSONObject(missionName);

                    System.out.println("       ----------------" + missionName + "----------------       ");

                    if (mission.has("rewards")) {
                        Object rewards = mission.get("rewards");
                        if (rewards instanceof JSONObject) {
                            JSONObject rewardsObject = (JSONObject) rewards;
                            for (Iterator<String> it3 = rewardsObject.keys(); it3.hasNext(); ) {
                                String rotation = it3.next();
                                JSONArray rewardsArray = rewardsObject.getJSONArray(rotation);

                                System.out.println("           ------------" + rotation + "------------           ");
                                for (int i = 0; i < rewardsArray.length(); i++) {
                                    JSONObject reward = rewardsArray.getJSONObject(i);
                                    System.out.println(" - " + reward.getString("itemName"));
                                }
                            }
                        } else if (rewards instanceof JSONArray) {
                            JSONArray rewardsArray = (JSONArray) rewards;
                            for (int i = 0; i < rewardsArray.length(); i++) {
                                JSONObject reward = rewardsArray.getJSONObject(i);
                                System.out.println(" - " + reward.getString("itemName"));
                            }
                        }
                    }
                }
            }
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    public void checkForNewRelicData() {
        backgroundThread.execute(() -> {
            setCurrentAction(CHECK_FOR_NEW_RELIC_DATA);
            int databaseTimestamp = database.appDao().getApiTimestamp();

            try {
                String json = sendGet(WARFRAME_API_URL + "/info.json");

                int apiTimestamp = JsonParser.parseString(json).getAsJsonObject().get("timestamp").getAsInt();

                if (databaseTimestamp != apiTimestamp) {
                    database.setUpRelics(getRelics());
                    database.setUpMissionRewards(getMissionRewards());
                    database.setUpBountyRewards(getBountyRewards());

                    database.appDao().updateApiTimestamp(apiTimestamp);
                }
            }
            catch (Exception e) {
                System.out.println("Error while checking for new relic data\n" + e.getMessage());
                e.printStackTrace();
            }
            finally {
                finishAction(CHECK_FOR_NEW_RELIC_DATA);
            }
        });
    }

    private String getRelics() throws Exception {
        return sendGet(WARFRAME_API_URL + "/relics.json");
    }

    private String getMissionRewards() throws Exception {
        return sendGet(WARFRAME_API_URL + "/missionRewards.json");
    }

    private String getBountyRewards(String bountyRewardsName) throws Exception {
        String json = sendGet(WARFRAME_API_URL + "/" + bountyRewardsName + ".json");
        return JsonParser.parseString(json).getAsJsonObject().get(bountyRewardsName).toString();
    }

    private HashMap<String, String> getBountyRewards() throws Exception {
        HashMap<String, String> bountyRewards = new HashMap<>();
        bountyRewards.put("Plains of Eidolon", getBountyRewards("cetusBountyRewards"));
        bountyRewards.put("Orb Vallis", getBountyRewards("solarisBountyRewards"));
        bountyRewards.put("Cambion Drift", getBountyRewards("deimosRewards"));
        bountyRewards.put("Chrysalith", getBountyRewards("zarimanRewards"));
        return bountyRewards;
    }
}
