package com.warframefarm.activities.details.part;

import static com.warframefarm.activities.details.part.PartRepository.MISSION;
import static com.warframefarm.activities.details.part.PartRepository.RELIC;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.RelicDisplayAdapter;
import com.warframefarm.activities.details.prime.PrimeFragment;
import com.warframefarm.activities.list.relics.PlanetMissionFarmAdapter;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.PartComplete;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.databinding.FragmentPartBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartFragment extends Fragment {

    private Context context;
    private MainActivity mainActivity;
    private FragmentPartBinding binding;
    private PartViewModel partViewModel;
    private final String partID;

    private RelicDisplayAdapter relicAdapter;
    private PlanetMissionFarmAdapter missionAdapter;

    private ImageView imagePrime, imageVault, imagePart, imageOwned;
    private TextView textPart;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerFilter;

    private TextView textEmptyState;
    private RecyclerView recyclerRelics, recyclerMissions;

    private LinearLayout bottomNav, buttonRelic, buttonMission;
    private ImageView iconRelic, iconMission;
    private TextView textRelic, textMission;

    public PartFragment(String partID) {
        this.partID = partID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        mainActivity = (MainActivity) getActivity();

        binding = FragmentPartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        partViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(mainActivity.getApplication())
                .create(PartViewModel.class);

        partViewModel.setPart(partID);

        //region Binding
        imagePrime = binding.imagePrime;
        imageVault = binding.imageVault;
        imagePart = binding.imagePart;
        textPart = binding.textPart;
        imageOwned = binding.imageOwned;

        searchbar = binding.searchbarPart;
        iconSearchbar = binding.iconSearchbarPart;
        spinnerFilter = binding.spinnerPart;

        textEmptyState = binding.textEmptyStatePart;
        recyclerRelics = binding.recyclerRelics;
        recyclerMissions = binding.recyclerMissions;

        bottomNav = binding.bottomNavPart;

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
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, primeNames);
        searchbar.setAdapter(searchAdapter);

        searchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                iconSearchbar.setImageTintList(context.getColorStateList(R.color.colorPrimary));
            else
                iconSearchbar.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
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

                partViewModel.setSearch(search);
            }
        });

        iconSearchbar.setOnClickListener(v -> {
            searchbar.setText("");
            searchbar.requestFocus();
        });
        //endregion

        //region Spinner
        ArrayList<String> filterOptions = new ArrayList<>();
        Collections.addAll(filterOptions,
                getString(R.string.best_places),
                getString(R.string.drop_chance),
                getString(R.string.mission_type),
                getString(R.string.planet)
        );
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, filterOptions);
        spinnerFilter.setAdapter(filterAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                partViewModel.setFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                partViewModel.setFilter(0);
            }
        });
        //endregion
        //endregion

        imageOwned.setOnClickListener(v -> partViewModel.switchOwned());

        relicAdapter = new RelicDisplayAdapter(context);
        recyclerRelics.setAdapter(relicAdapter);
        recyclerRelics.setLayoutManager(new LinearLayoutManager(context));

        missionAdapter = new PlanetMissionFarmAdapter(context);
        recyclerMissions.setAdapter(missionAdapter);
        recyclerMissions.setLayoutManager(new LinearLayoutManager(context));

        buttonRelic.setOnClickListener(v -> partViewModel.setMode(RELIC));

        buttonMission.setOnClickListener(v -> partViewModel.setMode(MISSION));

        //region Observers
        partViewModel.getPart().observe(getViewLifecycleOwner(), new Observer<PartComplete>() {
            @Override
            public void onChanged(PartComplete part) {
                imagePrime.setImageResource(part.getImagePrime());
                imagePrime.setOnClickListener(v -> showPrime(part.getPrime()));

                if (part.isVaulted()) {
                    imageVault.setVisibility(View.VISIBLE);
                    bottomNav.setVisibility(View.GONE);
                }
                else {
                    imageVault.setVisibility(View.GONE);
                    bottomNav.setVisibility(View.VISIBLE);
                }

                if (part.isBlueprint())
                    imagePart.setBackgroundResource(R.drawable.blueprint_bg);

                imagePart.setImageResource(part.getImage());

                textPart.setText(part.getFullName());

                if (part.isOwned()) imageOwned.setImageResource(R.drawable.owned);
                else imageOwned.setImageResource(R.drawable.not_owned);

                partViewModel.updateResults();
            }
        });

        partViewModel.getMode().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer mode) {
                switch (mode) {
                    case RELIC:
                        iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textRelic.setTextColor(context.getColor(R.color.colorAccent));

                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textMission.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        spinnerFilter.setVisibility(View.INVISIBLE);

                        recyclerMissions.setVisibility(View.GONE);
                        recyclerRelics.setVisibility(View.VISIBLE);
                        break;

                    case MISSION:
                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textMission.setTextColor(context.getColor(R.color.colorAccent));

                        iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textRelic.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        spinnerFilter.setVisibility(View.VISIBLE);

                        recyclerRelics.setVisibility(View.GONE);
                        recyclerMissions.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        partViewModel.getRelics().observe(getViewLifecycleOwner(), relics -> {
            relics.observe(getViewLifecycleOwner(), new Observer<List<RelicComplete>>() {
                @Override
                public void onChanged(List<RelicComplete> relics) {
                    if (relics.isEmpty()) {
                        textEmptyState.setText(R.string.empty_state_relics);
                        recyclerRelics.setVisibility(View.INVISIBLE);
                        textEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        textEmptyState.setVisibility(View.GONE);
                        recyclerRelics.setVisibility(View.VISIBLE);
                    }

                    relicAdapter.updateRelics(relics);
                }
            });
        });

        partViewModel.getMissions().observe(getViewLifecycleOwner(), new Observer<List<MissionComplete>>() {
            @Override
            public void onChanged(List<MissionComplete> missions) {
                if (missions.isEmpty()) {
                    textEmptyState.setText(R.string.empty_state_missions);
                    recyclerMissions.setVisibility(View.INVISIBLE);
                    textEmptyState.setVisibility(View.VISIBLE);
                } else {
                    textEmptyState.setVisibility(View.GONE);
                    recyclerMissions.setVisibility(View.VISIBLE);
                }

                missionAdapter.updateMissions(missions);
            }
        });
        //endregion

        return view;
    }

    private void showPrime(String prime) {
        mainActivity.multipleStackNavigator.start(new PrimeFragment(prime));
    }
}