package com.warframefarm.activities.details.mission;

import static com.warframefarm.activities.details.mission.BountyCategoryAdapter.LEVELS;
import static com.warframefarm.activities.details.mission.MissionViewModel.BOUNTIES;
import static com.warframefarm.activities.details.mission.MissionViewModel.CACHES;
import static com.warframefarm.activities.details.mission.MissionViewModel.REWARDS;
import static com.warframefarm.data.WarframeLists.MissionDescription;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_EMPYREAN;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_NORMAL;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.BountyRewardComplete;
import com.warframefarm.database.CacheRewardComplete;
import com.warframefarm.database.MissionRewardComplete;
import com.warframefarm.database.MissionWithRewardTypes;
import com.warframefarm.databinding.FragmentMissionBinding;

import java.util.ArrayList;
import java.util.List;

public class MissionFragment extends Fragment {

    private Context context;
    private MainActivity mainActivity;
    private FragmentMissionBinding binding;
    private MissionViewModel missionViewModel;
    private final String mission;

    private MissionRotationAdapter missionRotationAdapter;
    private CacheRotationAdapter cacheRotationAdapter;
    private BountyCategoryAdapter bountyCategoryAdapter;

    private ImageView imageSpecial, imagePlanet, imageType, imageFaction, imageWantedFilter, buttonObjectiveInfo;
    private TextView textMission, textMissionObjective;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;

    private RecyclerView recyclerRotations;

    private LinearLayout bottomNavMission, buttonReward, buttonBounty, buttonCache;
    private ImageView iconReward, iconBounty, iconCache;
    private TextView textReward, textBounty, textCache;

    public MissionFragment(String mission) {
        this.mission = mission;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        mainActivity = (MainActivity) getActivity();

        binding = FragmentMissionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        missionViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(mainActivity.getApplication())
                .create(MissionViewModel.class);

        missionViewModel.setMission(mission);

        //region Binding
        imageSpecial = binding.imageSpecial;

        imagePlanet = binding.imagePlanet;
        imageType = binding.imageType;
        buttonObjectiveInfo = binding.buttonObjectiveInfo;
        imageFaction = binding.imageFaction;
        imageWantedFilter = binding.imageWantedFilter;
        textMission = binding.textMission;
        textMissionObjective = binding.textMissionObjective;

        searchbar = binding.searchbarMission;
        iconSearchbar = binding.iconSearchbarMission;

        recyclerRotations = binding.recyclerRotations;

        bottomNavMission = binding.bottomNavMission;

        buttonReward = binding.buttonReward;
        iconReward = binding.iconReward;
        textReward = binding.textReward;

        buttonBounty = binding.buttonBounty;
        iconBounty = binding.iconBounty;
        textBounty = binding.textBounty;

        buttonCache = binding.buttonCache;
        iconCache = binding.iconCache;
        textCache = binding.textCache;
        //endregion

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

                missionViewModel.setSearch(search);
            }
        });

        iconSearchbar.setOnClickListener(v -> {
            searchbar.setText("");
            searchbar.requestFocus();
        });
        //endregion

        imageWantedFilter.setOnClickListener(v -> missionViewModel.switchFilter());

        missionRotationAdapter = new MissionRotationAdapter(context);
        cacheRotationAdapter = new CacheRotationAdapter(context);
        bountyCategoryAdapter = new BountyCategoryAdapter(context, LEVELS);

        recyclerRotations.setLayoutManager(new LinearLayoutManager(context));

        buttonReward.setOnClickListener(v -> missionViewModel.setMode(REWARDS));
        buttonBounty.setOnClickListener(v -> missionViewModel.setMode(BOUNTIES));
        buttonCache.setOnClickListener(v -> missionViewModel.setMode(CACHES));

        //region Observer
        missionViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<MissionWithRewardTypes>() {
            @Override
            public void onChanged(MissionWithRewardTypes mission) {
                switch (mission.getPlanet()) {
                    case "Void":
                    case "Veil":
                    case "Zariman":
                        imagePlanet.setVisibility(View.GONE);
                        imageSpecial.setVisibility(View.VISIBLE);
                        FirestoreHelper.loadPlanetBackgroundImage(mission.getPlanet(), context, imageSpecial);
                        break;

                    default:
                        FirestoreHelper.loadPlanetImage(mission.getPlanet(), context, imagePlanet);
                        break;
                }

                int type = mission.getType();
                cacheRotationAdapter.setType(type);
                if (type == TYPE_NORMAL) {
                    imageType.setVisibility(View.GONE);
                    iconCache.setBackgroundResource(R.drawable.cache);
                    textCache.setText(R.string.title_caches);
                }
                else {
                    imageType.setImageResource(mission.getImageType());
                    imageType.setVisibility(View.VISIBLE);

                    if (type == TYPE_EMPYREAN)
                        iconReward.setBackgroundResource(R.drawable.empyrean);
                }

                textMission.setText(mission.getName());
                textMissionObjective.setText(mission.getObjective());

                imageFaction.setImageResource(mission.getImageFaction());

                if (MissionDescription.containsKey(mission.getObjective())) {
                    buttonObjectiveInfo.setVisibility(View.VISIBLE);
                    buttonObjectiveInfo.setOnClickListener(v -> {
                        FragmentManager fm = getParentFragmentManager();
                        InfoMissionObjectiveDialog dialog = new InfoMissionObjectiveDialog(mission.getObjective());
                        dialog.show(fm, "Info Mission Objective Dialog");
                    });
                } else buttonObjectiveInfo.setVisibility(View.GONE);

                if (mission.getRewardTypes().size() > 1) {
                    bottomNavMission.setVisibility(View.VISIBLE);
                    buttonReward.setVisibility(mission.hasRewards() ? View.VISIBLE : View.GONE);
                    buttonBounty.setVisibility(mission.hasBounties() ? View.VISIBLE : View.GONE);
                    buttonCache.setVisibility(mission.hasCaches() ? View.VISIBLE : View.GONE);
                }
                else bottomNavMission.setVisibility(View.GONE);
            }
        });

        missionViewModel.getMode().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer mode) {
                iconReward.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                textReward.setTextColor(context.getColor(R.color.colorBackgroundDark));

                iconBounty.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                textBounty.setTextColor(context.getColor(R.color.colorBackgroundDark));

                iconCache.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                textCache.setTextColor(context.getColor(R.color.colorBackgroundDark));

                switch (mode) {
                    case REWARDS:
                        iconReward.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textReward.setTextColor(context.getColor(R.color.colorAccent));
                        break;
                    case BOUNTIES:
                        iconBounty.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textBounty.setTextColor(context.getColor(R.color.colorAccent));
                        break;
                    case CACHES:
                        iconCache.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                        textCache.setTextColor(context.getColor(R.color.colorAccent));
                        break;
                }
            }
        });

        missionViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean filter) {
                if (filter) imageWantedFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                else imageWantedFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
            }
        });

        missionViewModel.getMissionRewards().observe(getViewLifecycleOwner(), new Observer<List<MissionRewardComplete>>() {
            @Override
            public void onChanged(List<MissionRewardComplete> rewards) {
                missionRotationAdapter.updateMissionRotations(rewards);
                recyclerRotations.setAdapter(missionRotationAdapter);
            }
        });

        missionViewModel.getBountyRewards().observe(getViewLifecycleOwner(), new Observer<List<BountyRewardComplete>>() {
            @Override
            public void onChanged(List<BountyRewardComplete> rewards) {
                bountyCategoryAdapter.updateRewards(rewards);
                recyclerRotations.setAdapter(bountyCategoryAdapter);
            }
        });

        missionViewModel.getCacheRewards().observe(getViewLifecycleOwner(), new Observer<List<CacheRewardComplete>>() {
            @Override
            public void onChanged(List<CacheRewardComplete> rewards) {
                cacheRotationAdapter.updateCacheRotations(rewards);
                recyclerRotations.setAdapter(cacheRotationAdapter);
            }
        });
        //endregion

        return view;
    }
}