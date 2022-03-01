package com.warframefarm.data;

import static com.warframefarm.data.FirestoreTags.APP_TAG;
import static com.warframefarm.data.FirestoreTags.BUILD_TAG;
import static com.warframefarm.data.FirestoreTags.FACTION_TAG;
import static com.warframefarm.data.FirestoreTags.INFO_TAG;
import static com.warframefarm.data.FirestoreTags.MISSIONS_TAG;
import static com.warframefarm.data.FirestoreTags.MISSION_OBJECTIVE_TAG;
import static com.warframefarm.data.FirestoreTags.MISSION_PLANET_TAG;
import static com.warframefarm.data.FirestoreTags.PARTS_TAG;
import static com.warframefarm.data.FirestoreTags.PLANETS_TAG;
import static com.warframefarm.data.FirestoreTags.PRIMES_TAG;
import static com.warframefarm.data.FirestoreTags.TYPE_TAG;
import static com.warframefarm.data.FirestoreTags.USERS_TAG;

import android.app.Application;
import android.os.StrictMode;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.warframefarm.AppExecutors;
import com.warframefarm.database.AppDao;
import com.warframefarm.database.Mission;
import com.warframefarm.database.MissionDao;
import com.warframefarm.database.Part;
import com.warframefarm.database.PartComplete;
import com.warframefarm.database.PartCorrection;
import com.warframefarm.database.PartDao;
import com.warframefarm.database.Planet;
import com.warframefarm.database.PlanetDao;
import com.warframefarm.database.Prime;
import com.warframefarm.database.PrimeCorrection;
import com.warframefarm.database.PrimeDao;
import com.warframefarm.database.UserPart;
import com.warframefarm.database.UserPartDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class FirestoreHelper {

    private static FirestoreHelper instance;

    private final WarframeFarmDatabase database;
    private final AppDao appDao;
    private final PrimeDao primeDao;
    private final PartDao partDao;
    private final PlanetDao planetDao;
    private final MissionDao missionDao;
    private final UserPrimeDao userPrimeDao;
    private final UserPartDao userPartDao;

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final FirebaseAuth auth;

    private final Executor backgroundThread;

    private FirestoreHelper(Application application) {
        database = WarframeFarmDatabase.getInstance(application);
        appDao = database.appDao();
        primeDao = database.primeDao();
        partDao = database.partDao();
        planetDao = database.planetDao();
        missionDao = database.missionDao();
        userPrimeDao = database.userPrimeDao();
        userPartDao = database.userPartDao();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        AppExecutors executors = new AppExecutors();
        backgroundThread = executors.getBackgroundThread();
    }

    public static FirestoreHelper getInstance(Application application) {
        if (instance == null)
            instance = new FirestoreHelper(application);
        return instance;
    }

    public void checkForUpdates() {
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

                                            HashMap<String, Long> parts = (HashMap<String, Long>) prime.get(PARTS_TAG);
                                            Set<String> partNames = parts.keySet();
                                            String partID;
                                            for (String partName : partNames) {
                                                partID = primeName + " " + partName;
                                                partDao.insert(new Part(
                                                        partID,
                                                        primeName,
                                                        partName,
                                                        Math.toIntExact(parts.get(partName))
                                                ));
                                                userPartDao.insert(new UserPart(partID, false));
                                            }
                                        }
                                    }
                                });
                            }
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
                                });
                            }
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
                                });
                            }
                        });

                        appDao.updateBuild(Math.toIntExact(build));
                    }
                });
            }
        });
    }

    public void loadRewards() {
        StorageReference fileRef = storage.getReference().child("all.json");
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
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
                    database.setUpRelics(buffer.toString());
                    database.setUpMissionRewards(buffer.toString());
                });

            } catch (IOException e) {
                e.printStackTrace();
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
        });
    }

    public void syncFromLocal() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null)
            return;

        String userID = user.getUid();
        DocumentReference docRef = firestore.collection(USERS_TAG).document(userID);

        HashMap<String, Object> userInfo = new HashMap<>();

        List<UserPart> userParts = userPartDao.getUserParts();
        assert userParts != null;
        HashMap<String, Object> partsInfo = new HashMap<>();
        for (UserPart userPart : userParts)
            partsInfo.put(userPart.getPart(), userPart.isOwned());
        userInfo.put(PARTS_TAG, partsInfo);

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
                        //Updating parts
                        HashMap<String, Boolean> parts = (HashMap<String, Boolean>) doc.get(PARTS_TAG);
                        userPartDao.resetParts();
                        if (parts != null) {
                            Set<String> part_names = parts.keySet();
                            UserPart userPart;
                            for (String part : part_names) {
                                userPart = new UserPart(part, parts.get(part));
                                if (userPartDao.update(userPart) < 1)
                                    userPartDao.insert(userPart);
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
                        userPartDao.resetParts();
                        userPrimeDao.resetPrimes();
                    }
                });
            }
        });
    }

    public void setPrimeOwned(String prime, boolean owned) {
        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> partChanges = new HashMap<>();

        userPrimeDao.setOwned(prime, owned);
        primeChanges.put(prime, owned);

        String part;
        boolean partOwned;
        List<PartCorrection> corrections = userPartDao.getCorrections(prime);
        for (PartCorrection correction : corrections) {
            part = correction.getId();
            partOwned = correction.isOwned();
            userPartDao.setOwned(part, partOwned);
            partChanges.put(part, partOwned);
        }

        updateUserInfo(primeChanges, partChanges);
    }

    public void setPrimesOwned(List<String> primes, boolean owned) {
        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> partChanges = new HashMap<>();

        userPrimeDao.setOwned(primes, owned);
        for (String prime : primes)
            primeChanges.put(prime, owned);

        String part;
        boolean partOwned;
        List<PartCorrection> corrections = userPartDao.getCorrections(primes);
        for (PartCorrection correction : corrections) {
            part = correction.getId();
            partOwned = correction.isOwned();
            userPartDao.setOwned(part, partOwned);
            partChanges.put(part, partOwned);
        }

        updateUserInfo(primeChanges, partChanges);
    }

    public void setPartOwned(String part, String prime, boolean owned) {
        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> partChanges = new HashMap<>();

        userPartDao.setOwned(part, owned);
        partChanges.put(part, owned);

        PrimeCorrection correction = userPrimeDao.getCorrection(prime);
        if (correction != null) {
            boolean primeOwned = correction.isOwned();
            userPrimeDao.setOwned(prime, correction.isOwned());
            primeChanges.put(prime, primeOwned);
        }

        updateUserInfo(primeChanges, partChanges);
    }

    public void setPartsOwned(List<PartComplete> parts) {
        if (parts == null || parts.isEmpty())
            return;

        HashMap<String, Object> primeChanges = new HashMap<>();
        HashMap<String, Object> partChanges = new HashMap<>();

        List<String> primes = new ArrayList<>();
        String partID;
        boolean partOwned;
        for (PartComplete part : parts) {
            partID = part.getId();
            partOwned = part.isOwned();
            userPartDao.setOwned(partID, partOwned);
            partChanges.put(partID, partOwned);
            primes.add(part.getPrime());
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

        updateUserInfo(primeChanges, partChanges);
    }

    public void updateUserInfo(HashMap<String, Object> primes, HashMap<String, Object> parts) {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DocumentReference docRef = firestore.document(USERS_TAG + "/" + user.getUid());

            HashMap<String, Object> userInfo = new HashMap<>();
            if (!parts.isEmpty()) userInfo.put(PARTS_TAG, parts);
            if (!primes.isEmpty()) userInfo.put(PRIMES_TAG, primes);

            docRef.set(userInfo, SetOptions.merge());
        }
    }
}
