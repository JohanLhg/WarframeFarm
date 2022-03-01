package com.warframefarm.activities.details.relic;

import static com.warframefarm.activities.details.relic.RelicRepository.MISSION;
import static com.warframefarm.activities.details.relic.RelicRepository.REWARD;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_COMMON;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_RARE;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_UNCOMMON;

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
import com.warframefarm.activities.list.relics.PlanetMissionFarmAdapter;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.database.RelicRewardComplete;
import com.warframefarm.databinding.FragmentRelicBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RelicFragment extends Fragment {

    private Context context;
    private MainActivity activity;
    private FragmentRelicBinding binding;
    private RelicViewModel relicViewModel;
    private final String relicID;

    private PlanetMissionFarmAdapter missionAdapter;

    private TextView textRelic;
    private ImageView imageReward, imageVault, imageEra;

    private LinearLayout toolbarRelic;
    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerFilter;

    private LinearLayout layoutRewards;
    private RecyclerView recyclerCommonRewards, recyclerUncommonRewards, recyclerFarmPlanets;

    private LinearLayout bottomNav, buttonReward, buttonMission;
    private ImageView iconReward, iconMission;
    private TextView textReward, textMission;

    public RelicFragment(String relicID) {
        this.relicID = relicID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        activity = (MainActivity) getActivity();

        binding = FragmentRelicBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        relicViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(activity.getApplication())
                .create(RelicViewModel.class);

        relicViewModel.setRelic(relicID);

        //region Binding
        imageReward = binding.imageReward;
        imageVault = binding.imageVault;
        imageEra = binding.imageEra;
        textRelic = binding.textRelic;

        toolbarRelic = binding.toolbarRelic;
        searchbar = binding.toolbar.searchbar;
        iconSearchbar = binding.toolbar.iconSearchbar;
        spinnerFilter = binding.toolbar.spinnerSort;

        layoutRewards = binding.layoutRewards;
        recyclerCommonRewards = binding.relicRewards.recyclerCommonRewards;
        recyclerUncommonRewards = binding.relicRewards.recyclerUncommonRewards;

        recyclerFarmPlanets = binding.recyclerFarmPlanets;

        bottomNav = binding.bottomNavRelic;

        buttonReward = binding.buttonReward;
        iconReward = binding.iconReward;
        textReward = binding.textReward;

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
                iconSearchbar.setImageTintList(context.getColorStateList(R.color.colorAccent));
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

                relicViewModel.setSearch(search);
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
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, filterOptions);
        spinnerFilter.setAdapter(sortAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relicViewModel.setFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relicViewModel.setFilter(0);
            }
        });
        //endregion
        //endregion

        RelicRewardAdapter commonRewardAdapter = new RelicRewardAdapter(context);
        recyclerCommonRewards.setAdapter(commonRewardAdapter);
        recyclerCommonRewards.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        RelicRewardAdapter uncommonRewardAdapter = new RelicRewardAdapter(context);
        recyclerUncommonRewards.setAdapter(uncommonRewardAdapter);
        recyclerUncommonRewards.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        missionAdapter = new PlanetMissionFarmAdapter(context);
        recyclerFarmPlanets.setAdapter(missionAdapter);
        recyclerFarmPlanets.setLayoutManager(new LinearLayoutManager(context));

        buttonReward.setOnClickListener(v -> relicViewModel.setMode(REWARD));

        buttonMission.setOnClickListener(v -> relicViewModel.setMode(MISSION));

        //region Observers
        relicViewModel.getRelic().observe(getViewLifecycleOwner(), new Observer<RelicComplete>() {
            @Override
            public void onChanged(RelicComplete relic) {
                if (relic == null) {
                    activity.multipleStackNavigator.goBack();
                    return;
                }
                int wantedRarity = relic.getRarityNeeded();
                switch (wantedRarity) {
                    case REWARD_COMMON:
                        imageReward.setImageResource(R.drawable.reward_common);
                        break;

                    case REWARD_UNCOMMON:
                        imageReward.setImageResource(R.drawable.reward_uncommon);
                        break;

                    case REWARD_RARE:
                        imageReward.setImageResource(R.drawable.reward_rare);
                        break;

                    default:
                        imageReward.setVisibility(View.GONE);
                }

                if (relic.isVaulted()) {
                    imageVault.setVisibility(View.VISIBLE);
                    bottomNav.setVisibility(View.GONE);
                } else {
                    imageVault.setVisibility(View.GONE);
                    bottomNav.setVisibility(View.VISIBLE);
                }

                imageEra.setImageResource(relic.getImage());

                textRelic.setText(relic.getFullName());
            }
        });

        relicViewModel.getMode().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer mode) {
                switch (mode) {
                    case REWARD:
                        iconReward.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textReward.setTextColor(context.getColor(R.color.colorAccent));

                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textMission.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        layoutRewards.setVisibility(View.VISIBLE);
                        recyclerFarmPlanets.setVisibility(View.GONE);
                        toolbarRelic.setVisibility(View.GONE);
                        break;

                    case MISSION:
                        iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textMission.setTextColor(context.getColor(R.color.colorAccent));

                        iconReward.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        textReward.setTextColor(context.getColor(R.color.colorBackgroundDark));

                        recyclerFarmPlanets.setVisibility(View.VISIBLE);
                        toolbarRelic.setVisibility(View.VISIBLE);
                        layoutRewards.setVisibility(View.GONE);
                        break;
                }
            }
        });

        relicViewModel.getRewards().observe(getViewLifecycleOwner(), new Observer<List<RelicRewardComplete>>() {
            @Override
            public void onChanged(List<RelicRewardComplete> rewards) {
                if (rewards.size() < 6)
                    return;
                commonRewardAdapter.setRewards(rewards.subList(0, 3));
                uncommonRewardAdapter.setRewards(rewards.subList(3, 6));
            }
        });

        relicViewModel.getMissions().observe(getViewLifecycleOwner(), new Observer<List<MissionComplete>>() {
            @Override
            public void onChanged(List<MissionComplete> missions) {
                missionAdapter.updateMissions(missions);
            }
        });
        //endregion

        return view;
    }
}