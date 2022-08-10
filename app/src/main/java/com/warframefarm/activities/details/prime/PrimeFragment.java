package com.warframefarm.activities.details.prime;

import static com.warframefarm.activities.details.prime.PrimeViewModel.COMPONENT;
import static com.warframefarm.activities.details.prime.PrimeViewModel.MISSION;
import static com.warframefarm.activities.details.prime.PrimeViewModel.RELIC;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.RelicDisplayAdapter;
import com.warframefarm.activities.list.components.ComponentAdapter;
import com.warframefarm.activities.list.relics.PlanetMissionFarmAdapter;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.database.Mission;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.databinding.FragmentPrimeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimeFragment extends Fragment implements ComponentAdapter.ComponentListener {

    private Context context;
    private FragmentPrimeBinding binding;
    private PrimeViewModel primeViewModel;
    private final String prime;

    private boolean firstLoad = true;

    private ArrayAdapter<String> relicFilterAdapter, missionFilterAdapter;
    private ComponentAdapter componentAdapter;
    private RelicDisplayAdapter relicAdapter;
    private PlanetMissionFarmAdapter missionAdapter;

    private ImageView imagePrime, imageType, imageVault, imageOwned;
    private TextView textPrime;

    private LinearLayout toolbarPrime;
    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerFilter;

    private RecyclerView recyclerComponents, recyclerRelics, recyclerMissions;
    private TextView textEmptyStatePrime;

    private LinearLayout buttonComponent, buttonRelic, buttonMission;
    private ImageView iconComponent, iconRelic, iconMission;
    private TextView textComponent, textRelic, textMission;

    public PrimeFragment(String prime) {
        this.prime = prime;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        binding = FragmentPrimeBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        primeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PrimeViewModel.class);

        primeViewModel.setPrime(prime);

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

        recyclerComponents = binding.recyclerComponents;
        recyclerRelics = binding.recyclerRelics;
        recyclerMissions = binding.recyclerMissions;
        textEmptyStatePrime = binding.textEmptyStatePrime;

        buttonComponent = binding.buttonComponent;
        iconComponent = binding.iconComponent;
        textComponent = binding.textComponent;

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

        imageOwned.setOnClickListener(v -> primeViewModel.switchPrimeOwned());

        //region Adapters
        componentAdapter = new ComponentAdapter(context, this);
        recyclerComponents.setAdapter(componentAdapter);
        recyclerComponents.setLayoutManager(new GridLayoutManager(context, 3));

        ArrayList<String> relicFilterOptions = new ArrayList<>();
        Collections.addAll(relicFilterOptions,
                getString(R.string.component),
                getString(R.string.relic)
        );
        relicFilterAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, relicFilterOptions);

        relicAdapter = new RelicDisplayAdapter(context);
        recyclerRelics.setAdapter(relicAdapter);
        recyclerRelics.setLayoutManager(new LinearLayoutManager(context));

        ArrayList<String> missionFilterOptions = new ArrayList<>();
        Collections.addAll(missionFilterOptions,
                getString(R.string.best_places),
                getString(R.string.drop_chance),
                getString(R.string.mission_type),
                getString(R.string.planet)
        );
        missionFilterAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, missionFilterOptions);

        missionAdapter = new PlanetMissionFarmAdapter(context);
        recyclerMissions.setAdapter(missionAdapter);
        recyclerMissions.setLayoutManager(new LinearLayoutManager(context));
        //endregion

        buttonComponent.setOnClickListener(v -> primeViewModel.setMode(COMPONENT));

        buttonRelic.setOnClickListener(v -> primeViewModel.setMode(RELIC));

        buttonMission.setOnClickListener(v -> primeViewModel.setMode(MISSION));

        //region Observers
        primeViewModel.getPrime().observe(getViewLifecycleOwner(), new Observer<PrimeComplete>() {
            @Override
            public void onChanged(PrimeComplete prime) {
                if (prime == null)
                    return;

                prime.displayImage(context, imagePrime);
                imagePrime.setVisibility(View.VISIBLE);
                if (firstLoad) {
                    imagePrime.setX(-100);
                    imagePrime.animate().alpha(1).translationX(0).setDuration(400)
                            .start();
                    firstLoad = false;
                }

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

                primeViewModel.updateResults();
            }
        });

        primeViewModel.getMode().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer mode) {
                switch (mode) {
                    case COMPONENT:
                        iconComponent.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textComponent.setTextColor(context.getColor(R.color.colorAccent));

                        iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textRelic.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textMission.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        toolbarPrime.setVisibility(View.GONE);
                        recyclerRelics.setVisibility(View.GONE);
                        recyclerMissions.setVisibility(View.GONE);

                        recyclerComponents.setVisibility(View.VISIBLE);

                        textEmptyStatePrime.setVisibility(View.GONE);
                        break;

                    case RELIC:
                        iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textRelic.setTextColor(context.getColor(R.color.colorAccent));

                        iconComponent.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textComponent.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textMission.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        recyclerComponents.setVisibility(View.GONE);
                        recyclerMissions.setVisibility(View.GONE);

                        spinnerFilter.setAdapter(relicFilterAdapter);
                        spinnerFilter.setSelection(primeViewModel.getRelicFilterPos());

                        toolbarPrime.setVisibility(View.VISIBLE);
                        recyclerRelics.setVisibility(View.VISIBLE);
                        break;

                    case MISSION:
                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textMission.setTextColor(context.getColor(R.color.colorAccent));

                        iconComponent.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textComponent.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textRelic.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        recyclerComponents.setVisibility(View.GONE);
                        recyclerRelics.setVisibility(View.GONE);

                        spinnerFilter.setAdapter(missionFilterAdapter);
                        spinnerFilter.setSelection(primeViewModel.getMissionFilterPos());

                        toolbarPrime.setVisibility(View.VISIBLE);
                        recyclerMissions.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        primeViewModel.getComponents().observe(getViewLifecycleOwner(), new Observer<List<ComponentComplete>>() {
            @Override
            public void onChanged(List<ComponentComplete> components) {
                textEmptyStatePrime.setVisibility(View.GONE);
                componentAdapter.updateComponents(components);
            }
        });

        primeViewModel.getRelics().observe(getViewLifecycleOwner(), new Observer<List<RelicComplete>>() {
            @Override
            public void onChanged(List<RelicComplete> relics) {
                if (primeViewModel.getModeValue() != RELIC)
                    return;
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

        primeViewModel.getMissions().observe(getViewLifecycleOwner(), new Observer<List<Mission>>() {
            @Override
            public void onChanged(List<Mission> missions) {
                if (primeViewModel.getModeValue() != MISSION)
                    return;
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void modifyComponent(ComponentComplete component) {
        primeViewModel.switchComponentOwned(component);
    }
}