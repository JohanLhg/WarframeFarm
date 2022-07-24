package com.warframefarm.data;

import static com.warframefarm.data.FirestoreTags.APP_TAG;
import static com.warframefarm.data.FirestoreTags.BUILD_TAG;
import static com.warframefarm.data.FirestoreTags.COMPONENT_TAG;
import static com.warframefarm.data.FirestoreTags.FACTION_TAG;
import static com.warframefarm.data.FirestoreTags.INFO_TAG;
import static com.warframefarm.data.FirestoreTags.LAST_MODIFIED_BY_TAG;
import static com.warframefarm.data.FirestoreTags.MISSIONS_TAG;
import static com.warframefarm.data.FirestoreTags.MISSION_OBJECTIVE_TAG;
import static com.warframefarm.data.FirestoreTags.MISSION_PLANET_TAG;
import static com.warframefarm.data.FirestoreTags.PLANETS_TAG;
import static com.warframefarm.data.FirestoreTags.PRIMES_TAG;
import static com.warframefarm.data.FirestoreTags.TYPE_TAG;
import static com.warframefarm.data.FirestoreTags.USERS_TAG;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.warframefarm.AppExecutors;
import com.warframefarm.CommunicationHandler;
import com.warframefarm.R;
import com.warframefarm.database.AppDao;
import com.warframefarm.database.Component;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.ComponentCorrection;
import com.warframefarm.database.ComponentDao;
import com.warframefarm.database.Mission;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.Planet;
import com.warframefarm.database.PlanetDao;
import com.warframefarm.database.Prime;
import com.warframefarm.database.PrimeCorrection;
import com.warframefarm.database.PrimeDao;
import com.warframefarm.database.UserComponent;
import com.warframefarm.database.UserComponentDao;
import com.warframefarm.database.UserPrime;
import com.warframefarm.database.UserPrimeDao;
import com.warframefarm.database.WarframeFarmDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class FirestoreHelper {

    private static FirestoreHelper instance;

    private final WarframeFarmDatabase database;
    private final AppDao appDao;
    private final PrimeDao primeDao;
    private final ComponentDao componentDao;
    private final PlanetDao planetDao;
    private final MissionDao missionDao;
    private final UserPrimeDao userPrimeDao;
    private final UserComponentDao userComponentDao;

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final FirebaseAuth auth;

    private String ID = "";
    private CommunicationHandler communicationHandler;
    private Integer currentAction;
    private final List<Integer> taskQueue = new ArrayList<>();
    public final static int CHECK_FOR_UPDATES = 0, CHECK_FOR_NEW_USER_DATA = 1,
            CHECK_FOR_OFFLINE_CHANGES = 2;
    public final static int LOADING_PRIMES = -1, LOADING_COMPONENTS = -2, LOADING_RELICS = -3, LOADING_PLANETS = -4,
            LOADING_MISSIONS = -5;

    private final Executor backgroundThread, mainThread;

    public FirestoreHelper(Application application, CommunicationHandler communicationHandler) {
        this(application);
        this.communicationHandler = communicationHandler;
    }

    private FirestoreHelper(Application application) {
        setID();

        database = WarframeFarmDatabase.getInstance(application);
        appDao = database.appDao();
        primeDao = database.primeDao();
        componentDao = database.componentDao();
        planetDao = database.planetDao();
        missionDao = database.missionDao();
        userPrimeDao = database.userPrimeDao();
        userComponentDao = database.userComponentDao();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
        mainThread = executors.getMainThread();
    }

    public static FirestoreHelper getInstance(Application application) {
        if (instance == null)
            instance = new FirestoreHelper(application);
        return instance;
    }

    public static boolean isConnectedToInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void setID() {
        ID = "";
        FirebaseInstallations.getInstance().getId().addOnSuccessListener(result -> ID = result);
    }

    public void checkForUpdates() {
        Collections.addAll(taskQueue, LOADING_PRIMES, LOADING_PLANETS, LOADING_MISSIONS, LOADING_RELICS);
        currentAction = CHECK_FOR_UPDATES;
        DocumentReference docRef = firestore.collection(APP_TAG).document(INFO_TAG);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                backgroundThread.execute(() -> {
                    DocumentSnapshot info = task.getResult();

                    long build = 0;
                    if (info != null && info.contains(BUILD_TAG))
                        build = info.getLong(BUILD_TAG);

                    //GET CURRENT UPDATE IN APP TABLE
                    long current_build = appDao.getCurrentVersion();

                    if (build > current_build) {
                        Query queryPrimes = firestore.collection(PRIMES_TAG).whereGreaterThan(BUILD_TAG, current_build);
                        Query queryPlanets = firestore.collection(PLANETS_TAG).whereGreaterThan(BUILD_TAG, current_build);
                        Query queryMissions = firestore.collection(MISSIONS_TAG).whereGreaterThan(BUILD_TAG, current_build);

                        queryPrimes.get().addOnCompleteListener(taskPrimes -> {
                            if (taskPrimes.isSuccessful()) {
                                backgroundThread.execute(() -> {
                                    List<DocumentSnapshot> primes = taskPrimes.getResult().getDocuments();
                                    if (!primes.isEmpty()) {
                                        String primeName;
                                        for (DocumentSnapshot prime : primes) {
                                            primeName = prime.getId();
                                            primeDao.insert(new Prime(
                                                    primeName,
                                                    prime.getString(TYPE_TAG))
                                            );
                                            userPrimeDao.insert(new UserPrime(primeName, false));

                                            HashMap<String, Long> components = (HashMap<String, Long>) prime.get(COMPONENT_TAG);
                                            Set<String> componentNames = components.keySet();
                                            String componentID;
                                            for (String componentName : componentNames) {
                                                componentID = primeName + " " + componentName;
                                                componentDao.insert(new Component(
                                                        componentID,
                                                        primeName,
                                                        componentName,
                                                        Math.toIntExact(components.get(componentName))
                                                ));
                                                userComponentDao.insert(new UserComponent(componentID, false));
                                            }
                                        }
                                    }
                                    finishTask(LOADING_PRIMES);
                                });
                            }
                            else finishTask(LOADING_PRIMES);
                        });

                        queryPlanets.get().addOnCompleteListener(taskPlanets -> {
                            if (taskPlanets.isSuccessful()) {
                                backgroundThread.execute(() -> {
                                    List<DocumentSnapshot> planets = taskPlanets.getResult().getDocuments();
                                    if (!planets.isEmpty())
                                        for (DocumentSnapshot planet : planets)
                                            planetDao.insert(new Planet(
                                                    planet.getId(),
                                                    planet.getString(FACTION_TAG)
                                            ));
                                    finishTask(LOADING_PLANETS);
                                });
                            }
                            else finishTask(LOADING_PLANETS);
                        });

                        queryMissions.get().addOnCompleteListener(taskMissions -> {
                            if (taskMissions.isSuccessful()) {
                                backgroundThread.execute(() -> {
                                    List<DocumentSnapshot> missions = taskMissions.getResult().getDocuments();
                                    if (!missions.isEmpty()) {
                                        for (DocumentSnapshot mission : missions)
                                            missionDao.insert(new Mission(
                                                    mission.getId(),
                                                    mission.getString(MISSION_PLANET_TAG),
                                                    mission.getString(MISSION_OBJECTIVE_TAG),
                                                    mission.getString(FACTION_TAG),
                                                    Math.toIntExact(mission.getLong(TYPE_TAG))
                                            ));

                                        loadRewards();
                                    }
                                    loadRewards();
                                    finishTask(LOADING_MISSIONS);
                                });
                            }
                            else {
                                finishTask(LOADING_MISSIONS);
                                finishTask(LOADING_RELICS);
                            }
                        });

                        appDao.updateBuild(Math.toIntExact(build));
                    }
                    else finishAction(CHECK_FOR_UPDATES);
                });
            }
            else finishAction(CHECK_FOR_UPDATES);
        });
    }

    public void loadRewards() {
        StorageReference fileRef = storage.getReference().child("all.json");
        fileRef.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    URL url = new URL(uri.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();


                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder buffer = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                        buffer.append(line + "\n");

                    backgroundThread.execute(() -> {
                        String json = buffer.toString();
                        database.setUpRelics(json);
                        database.setUpMissionRewards(json);
                        database.setUpBountyRewards(json);
                        finishTask(LOADING_RELICS);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    finishTask(LOADING_RELICS);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else finishTask(LOADING_RELICS);
        });
    }

    public void syncFromLocal() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null)
            return;

        String userID = user.getUid();
        DocumentReference docRef = firestore.collection(USERS_TAG).document(userID);

        HashMap<String, Object> userInfo = new HashMap<>();

        List<UserComponent> userComponents = userComponentDao.getUserComponents();
        assert userComponents != null;
        HashMap<String, Object> componentsInfo = new HashMap<>();
        for (UserComponent userComponent : userComponents)
            componentsInfo.put(userComponent.getComponent(), userComponent.isOwned());
        userInfo.put(COMPONENT_TAG, componentsInfo);

        List<UserPrime> userPrimes = userPrimeDao.getUserPrimes();
        assert userPrimes != null;
        HashMap<String, Object> primesInfo = new HashMap<>();
        for (UserPrime userPrime : userPrimes) {
            primesInfo.put(userPrime.getPrime(), userPrime.isOwned());
        }
        userInfo.put(PRIMES_TAG, primesInfo);

        docRef.set(userInfo, SetOptions.merge()).addOnCompleteListener(task -> {});
    }

    public void syncFromOnline() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null)
            return;

        String userID = user.getUid();
        DocumentReference docRef = firestore.collection(USERS_TAG).document(userID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                backgroundThread.execute(() -> {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    if (doc.exists()) {
                        //Updating components
                        HashMap<String, Boolean> components = (HashMap<String, Boolean>) doc.get(COMPONENT_TAG);
                        userComponentDao.resetComponents();
                        if (components != null) {
                            Set<String> component_names = components.keySet();
                            UserComponent userComponent;
                            for (String component : component_names) {
                                userComponent = new UserComponent(component, components.get(component));
                                if (userComponentDao.update(userComponent) < 1)
                                    userComponentDao.insert(userComponent);
                            }
                        }

                        //Updating primes
                        HashMap<String, Boolean> primes = (HashMap<String, Boolean>) doc.get(PRIMES_TAG);
                        userPrimeDao.resetPrimes();
                        if (primes != null) {
                            Set<String> prime_names = primes.keySet();
                            UserPrime userPrime;
                            for (String prime : prime_names) {
                                userPrime = new UserPrime(prime, primes.get(prime));
                                if (userPrimeDao.update(userPrime) < 1)
                                    userPrimeDao.insert(userPrime);
                            }
                        }
                    } else {
                        userComponentDao.resetComponents();
                        userPrimeDao.resetPrimes();
                    }
                });
            }
        });
    }

    public void checkForNewUserData() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            finishAction(CHECK_FOR_NEW_USER_DATA);
            return;
        }

        Collections.addAll(taskQueue, LOADING_PRIMES, LOADING_COMPONENTS);
        currentAction = CHECK_FOR_NEW_USER_DATA;

        String userID = user.getUid();
        DocumentReference docRef = firestore.collection(USERS_TAG).document(userID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                backgroundThread.execute(() -> {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    if (doc.exists()) {
                        String lastAuthor = doc.getString(LAST_MODIFIED_BY_TAG);
                        if (lastAuthor == null || lastAuthor.equals("") || (!ID.equals("") && lastAuthor.equals(ID))) {
                            finishAction(CHECK_FOR_NEW_USER_DATA);
                            return;
                        }

                        //Updating components
                        HashMap<String, Boolean> components = (HashMap<String, Boolean>) doc.get(COMPONENT_TAG);
                        userComponentDao.resetComponents();
                        if (components != null) {
                            Set<String> component_names = components.keySet();
                            UserComponent userComponent;
                            for (String component : component_names) {
                                userComponent = new UserComponent(component, components.get(component));
                                if (userComponentDao.update(userComponent) < 1)
                                    userComponentDao.insert(userComponent);
                            }
                        }
                        finishTask(LOADING_COMPONENTS);

                        //Updating primes
                        HashMap<String, Boolean> primes = (HashMap<String, Boolean>) doc.get(PRIMES_TAG);
                        userPrimeDao.resetPrimes();
                        if (primes != null) {
                            Set<String> prime_names = primes.keySet();
                            UserPrime userPrime;
                            for (String prime : prime_names) {
                                userPrime = new UserPrime(prime, primes.get(prime));
                                if (userPrimeDao.update(userPrime) < 1)
                                    userPrimeDao.insert(userPrime);
                            }
                        }
                        finishTask(LOADING_PRIMES);
                    }
                    else {
                        userComponentDao.resetComponents();
                        userPrimeDao.resetPrimes();
                        finishAction(CHECK_FOR_NEW_USER_DATA);
                    }
                });
            }
            else finishAction(CHECK_FOR_NEW_USER_DATA);
        });
    }

    public void setPrimeOwned(String prime, boolean owned) {
        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> componentChanges = new HashMap<>();

        userPrimeDao.setOwned(prime, owned);
        primeChanges.put(prime, owned);

        String component;
        boolean componentOwned;
        List<ComponentCorrection> corrections = userComponentDao.getCorrections(prime);
        for (ComponentCorrection correction : corrections) {
            component = correction.getId();
            componentOwned = correction.isOwned();
            userComponentDao.setOwned(component, componentOwned);
            componentChanges.put(component, componentOwned);
        }

        updateUserInfo(primeChanges, componentChanges);
    }

    public void setPrimesOwned(List<String> primes, boolean owned) {
        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> componentChanges = new HashMap<>();

        userPrimeDao.setOwned(primes, owned);
        for (String prime : primes)
            primeChanges.put(prime, owned);

        String component;
        boolean componentOwned;
        List<ComponentCorrection> corrections = userComponentDao.getCorrections(primes);
        for (ComponentCorrection correction : corrections) {
            component = correction.getId();
            componentOwned = correction.isOwned();
            userComponentDao.setOwned(component, componentOwned);
            componentChanges.put(component, componentOwned);
        }

        updateUserInfo(primeChanges, componentChanges);
    }

    public void setComponentOwned(String component, String prime, boolean owned) {
        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> componentChanges = new HashMap<>();

        userComponentDao.setOwned(component, owned);
        componentChanges.put(component, owned);

        PrimeCorrection correction = userPrimeDao.getCorrection(prime);
        if (correction != null) {
            boolean primeOwned = correction.isOwned();
            userPrimeDao.setOwned(prime, correction.isOwned());
            primeChanges.put(prime, primeOwned);
        }

        updateUserInfo(primeChanges, componentChanges);
    }

    public void setComponentsOwned(List<ComponentComplete> components) {
        if (components == null || components.isEmpty())
            return;

        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> componentChanges = new HashMap<>();

        List<String> primes = new ArrayList<>();
        String componentID;
        boolean componentOwned;
        for (ComponentComplete component : components) {
            componentID = component.getId();
            componentOwned = component.isOwned();
            userComponentDao.setOwned(componentID, componentOwned);
            componentChanges.put(componentID, componentOwned);
            primes.add(component.getPrime());
        }

        if (!primes.isEmpty()) {
            List<PrimeCorrection> corrections = userPrimeDao.getCorrections(primes);
            if (!(corrections == null || corrections.isEmpty())) {
                String prime;
                boolean primeOwned;
                for (PrimeCorrection correction : corrections) {
                    prime = correction.getId();
                    primeOwned = correction.isOwned();
                    userPrimeDao.setOwned(prime, primeOwned);
                    primeChanges.put(prime, primeOwned);
                }
            }
        }

        updateUserInfo(primeChanges, componentChanges);
    }

    public static void loadPrimeImage(String prime, Context context, ImageView view) {
        loadImage("Images/Primes/" + prime + ".png", context, view);
    }

    public static void loadPlanetImage(String planet, Context context, ImageView view) {
        loadImage("Images/Planets/Complete/" + planet + ".png", context, view);
    }

    public static void loadPlanetBackgroundImage(String planet, Context context, ImageView view) {
        loadImage("Images/Planets/Background/" + planet + ".png", context, view);
    }

    public static void loadPlanetSquareImage(String planet, Context context, ImageView view) {
        loadImage("Images/Planets/Square/" + planet + ".png", context, view);
    }

    public static void loadPlanetTopImage(String planet, Context context, ImageView view) {
        loadImage("Images/Planets/Top/" + planet + ".png", context, view);
    }

    private static void loadImage(String path, Context context, ImageView view) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(path);
        Glide.with(context)
                .load(reference)
                .thumbnail(Glide.with(context).load(R.drawable.loading))
                .centerInside()
                .into(view);
    }

    public void updateUserInfo(HashMap<String, Object> primes, HashMap<String, Object> components) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DocumentReference docRef = firestore.document(USERS_TAG + "/" + user.getUid());

            HashMap<String, Object> userInfo = new HashMap<>();
            if (!components.isEmpty()) userInfo.put(COMPONENT_TAG, components);
            if (!primes.isEmpty()) userInfo.put(PRIMES_TAG, primes);
            userInfo.put(LAST_MODIFIED_BY_TAG, ID);

            docRef.set(userInfo, SetOptions.merge());
        }
    }

    private void finishTask(Integer taskID) {
        taskQueue.remove(taskID);
        if (taskQueue.isEmpty())
            finishAction(currentAction);
    }

    private void startAction(Integer actionID) {
        if (communicationHandler != null)
            mainThread.execute(() -> communicationHandler.startAction(actionID));
    }

    private void finishAction(Integer actionID) {
        taskQueue.clear();
        if (communicationHandler != null)
            mainThread.execute(() -> communicationHandler.finishAction(actionID));
    }
}
