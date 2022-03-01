package com.warframefarm.activities.details.prime;

import static com.warframefarm.activities.details.prime.PrimeRepository.MISSION;
import static com.warframefarm.activities.details.prime.PrimeRepository.PART;
import static com.warframefarm.activities.details.prime.PrimeRepository.RELIC;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.RelicDisplayAdapter;
import com.warframefarm.activities.inventory.InventoryAdapter;
import com.warframefarm.activities.list.relics.PlanetMissionFarmAdapter;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.PartComplete;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.databinding.ActivityPrimeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimeActivity extends AppCompatActivity implements InventoryAdapter.InventoryListener {
    private ActivityPrimeBinding binding;
    private PrimeViewModel primeViewModel;

    private ArrayAdapter<String> relicFilterAdapter, missionFilterAdapter;
    private InventoryAdapter partAdapter;
    private RelicDisplayAdapter relicAdapter;
    private PlanetMissionFarmAdapter missionAdapter;

    private ImageView imagePrime, imageType, imageVault, imageOwned;
    private TextView textPrime;

    private LinearLayout toolbarPrime;
    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerFilter;

    private RecyclerView recyclerParts, recyclerRelics, recyclerMissions;
    private TextView textEmptyStatePrime;
    
    private LinearLayout buttonPart, buttonRelic, buttonMission;
    private ImageView iconPart, iconRelic, iconMission;
    private TextView textPart, textRelic, textMission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(200));

        binding = ActivityPrimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        primeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(PrimeViewModel.class);

        Bundle extras = getIntent().getExtras();
        String primeName = "";
        if (extras != null && extras.containsKey(PRIME_NAME))
            primeName = extras.getString(PRIME_NAME);
        else finish();

        primeViewModel.setPrime(primeName);

        //region Binding
        imagePrime = binding.imagePrime;
        imageType = binding.imageType;
        imageVault = binding.imageVault;
        imageOwned = binding.imageOwned;
        textPrime = binding.textPrime;

        toolbarPrime = binding.toolbarPrime;
        searchbar = binding.toolbar.searchbar;
        iconSearchbar = binding.toolbar.iconSearchbar;
        spinnerFilter = binding.toolbar.spinnerSort;

        recyclerParts = binding.recyclerParts;
        recyclerRelics = binding.recyclerRelics;
        recyclerMissions = binding.recyclerMissions;
        textEmptyStatePrime = binding.textEmptyStatePrime;

        buttonPart = binding.buttonPart;
        iconPart = binding.iconPart;
        textPart = binding.textPart;

        buttonRelic = binding.buttonRelic;
        iconRelic = binding.iconRelic;
        textRelic = binding.textRelic;

        buttonMission = binding.buttonMission;
        iconMission = binding.iconMission;
        textMission = binding.textMission;
        //endregion

        //region Toolbar
        //region Searchbar
        ArrayList<String> primeNames = new ArrayList<>();
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, primeNames);
        searchbar.setAdapter(searchAdapter);

        searchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                iconSearchbar.setImageTintList(getColorStateList(R.color.colorPrimary));
            else
                iconSearchbar.setImageTintList(getColorStateList(R.color.colorBackgroundDark));
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                if (search.contains("'")) {
                    searchbar.setText(search.replace("'", ""));
                    return;
                }
                if (search.equals(""))
                    iconSearchbar.setImageResource(R.drawable.search);
                else
                    iconSearchbar.setImageResource(R.drawable.searchbar_delete);

                primeViewModel.setSearch(search);
            }
        });

        iconSearchbar.setOnClickListener(v -> {
            searchbar.setText("");
            searchbar.requestFocus();
        });
        //endregion

        //region Spinner
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                primeViewModel.setFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                primeViewModel.setFilter(0);
            }
        });
        //endregion
        //endregion

        //region Adapters
        partAdapter = new InventoryAdapter(this, this);
        recyclerParts.setAdapter(partAdapter);
        recyclerParts.setLayoutManager(new GridLayoutManager(this, 3));

        ArrayList<String> relicFilterOptions = new ArrayList<>();
        Collections.addAll(relicFilterOptions,
                getString(R.string.part),
                getString(R.string.relic)
        );
        relicFilterAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, relicFilterOptions);

        relicAdapter = new RelicDisplayAdapter(this);
        recyclerRelics.setAdapter(relicAdapter);
        recyclerRelics.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> missionFilterOptions = new ArrayList<>();
        Collections.addAll(missionFilterOptions,
                getString(R.string.best_places),
                getString(R.string.drop_chance),
                getString(R.string.mission_type),
                getString(R.string.planet)
        );
        missionFilterAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, missionFilterOptions);

        missionAdapter = new PlanetMissionFarmAdapter(this);
        recyclerMissions.setAdapter(missionAdapter);
        recyclerMissions.setLayoutManager(new LinearLayoutManager(this));
        //endregion

        buttonPart.setOnClickListener(v -> primeViewModel.setMode(PART));

        buttonRelic.setOnClickListener(v -> primeViewModel.setMode(RELIC));

        buttonMission.setOnClickListener(v -> primeViewModel.setMode(MISSION));

        //region Observers
        primeViewModel.getPrime().observe(this, new Observer<PrimeComplete>() {
            @Override
            public void onChanged(PrimeComplete prime) {
                imagePrime.setImageResource(prime.getImage());

                textPrime.setText(prime.getFullName());

                imageType.setImageResource(prime.getImageType());

                if (prime.isVaulted()) {
                    imageVault.setVisibility(View.VISIBLE);
                    buttonMission.setVisibility(View.GONE);
                }
                else {
                    imageVault.setVisibility(View.GONE);
                    buttonMission.setVisibility(View.VISIBLE);
                }

                if (prime.isOwned()) imageOwned.setImageResource(R.drawable.owned);
                else imageOwned.setImageResource(R.drawable.not_owned);

                imageOwned.setOnClickListener(v -> {
                    primeViewModel.switchPrimeOwned();
                });
            }
        });

        primeViewModel.getMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer mode) {
                switch (mode) {
                    case PART:
                        iconPart.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                        textPart.setTextColor(getColor(R.color.colorAccent));

                        iconRelic.setBackgroundTintList(getColorStateList(R.color.colorBackgroundDark));
                        textRelic.setTextColor(getColor(R.color.colorBackgroundDark));

                        iconMission.setBackgroundTintList(getColorStateList(R.color.colorBackgroundDark));
                        textMission.setTextColor(getColor(R.color.colorBackgroundDark));

                        toolbarPrime.setVisibility(View.GONE);
                        recyclerRelics.setVisibility(View.GONE);
                        recyclerMissions.setVisibility(View.GONE);

                        recyclerParts.setVisibility(View.VISIBLE);
                        break;

                    case RELIC:
                        iconRelic.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                        textRelic.setTextColor(getColor(R.color.colorAccent));

                        iconPart.setBackgroundTintList(getColorStateList(R.color.colorBackgroundDark));
                        textPart.setTextColor(getColor(R.color.colorBackgroundDark));

                        iconMission.setBackgroundTintList(getColorStateList(R.color.colorBackgroundDark));
                        textMission.setTextColor(getColor(R.color.colorBackgroundDark));

                        recyclerParts.setVisibility(View.GONE);
                        recyclerMissions.setVisibility(View.GONE);

                        spinnerFilter.setAdapter(relicFilterAdapter);
                        spinnerFilter.setSelection(primeViewModel.getRelicFilterPos());

                        toolbarPrime.setVisibility(View.VISIBLE);
                        recyclerRelics.setVisibility(View.VISIBLE);
                        break;

                    case MISSION:
                        iconMission.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                        textMission.setTextColor(getColor(R.color.colorAccent));

                        iconPart.setBackgroundTintList(getColorStateList(R.color.colorBackgroundDark));
                        textPart.setTextColor(getColor(R.color.colorBackgroundDark));

                        iconRelic.setBackgroundTintList(getColorStateList(R.color.colorBackgroundDark));
                        textRelic.setTextColor(getColor(R.color.colorBackgroundDark));

                        recyclerParts.setVisibility(View.GONE);
                        recyclerRelics.setVisibility(View.GONE);

                        spinnerFilter.setAdapter(missionFilterAdapter);
                        spinnerFilter.setSelection(primeViewModel.getMissionFilterPos());

                        toolbarPrime.setVisibility(View.VISIBLE);
                        recyclerMissions.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        primeViewModel.getParts().observe(this, new Observer<List<PartComplete>>() {
            @Override
            public void onChanged(List<PartComplete> parts) {
                textEmptyStatePrime.setVisibility(View.GONE);
                partAdapter.updateParts(parts);
            }
        });

        primeViewModel.getRelics().observe(this, new Observer<List<RelicComplete>>() {
            @Override
            public void onChanged(List<RelicComplete> relics) {
                if (relics.isEmpty()) {
                    textEmptyStatePrime.setText(R.string.empty_state_results_relic);
                    recyclerRelics.setVisibility(View.GONE);
                    textEmptyStatePrime.setVisibility(View.VISIBLE);
                } else {
                    textEmptyStatePrime.setVisibility(View.GONE);
                    recyclerRelics.setVisibility(View.VISIBLE);
                }
                relicAdapter.updateRelics(relics);
            }
        });

        primeViewModel.getMissions().observe(this, new Observer<List<MissionComplete>>() {
            @Override
            public void onChanged(List<MissionComplete> missions) {
                if (missions.isEmpty()) {
                    textEmptyStatePrime.setText(R.string.empty_state_missions);
                    recyclerMissions.setVisibility(View.GONE);
                    textEmptyStatePrime.setVisibility(View.VISIBLE);
                } else {
                    textEmptyStatePrime.setVisibility(View.GONE);
                    recyclerMissions.setVisibility(View.VISIBLE);
                }
                missionAdapter.updateMissions(missions);
            }
        });
        //endregion
    }

    @Override
    public void modifyPart(PartComplete part) {
        primeViewModel.switchPartOwned(part);
    }
}