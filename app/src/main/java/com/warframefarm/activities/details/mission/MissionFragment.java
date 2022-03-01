package com.warframefarm.activities.details.mission;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.MissionRewardComplete;
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

    private ImageView imageSpecial, imagePlanet, imageType, imageFaction, imageWantedFilter;
    private TextView textMission, textMissionObjective;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;

    private RecyclerView recyclerRotations;

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
        imageFaction = binding.imageFaction;
        imageWantedFilter = binding.imageWantedFilter;
        textMission = binding.textMission;
        textMissionObjective = binding.textMissionObjective;

        searchbar = binding.searchbarMission;
        iconSearchbar = binding.iconSearchbarMission;

        recyclerRotations = binding.recyclerRotations;
        //endregion

        //region Searchbar
        ArrayList<String> primeNames = new ArrayList<>();
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, primeNames);
        searchbar.setAdapter(searchAdapter);

        searchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                iconSearchbar.setImageTintList(context.getColorStateList(R.color.colorPrimary));
            else
                iconSearchbar.setImageTintList(context.getColorStateList(R.color.text));
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
        recyclerRotations.setAdapter(missionRotationAdapter);
        recyclerRotations.setLayoutManager(new LinearLayoutManager(context));

        //region Observer
        missionViewModel.getMission().observe(getViewLifecycleOwner(), new Observer<MissionComplete>() {
            @Override
            public void onChanged(MissionComplete mission) {
                switch (mission.getPlanet()) {
                    case "Void":
                        imagePlanet.setVisibility(View.GONE);
                        imageSpecial.setVisibility(View.VISIBLE);
                        imageSpecial.setImageResource(R.drawable.planet_background_void);
                        break;

                    case "Veil":
                        imagePlanet.setVisibility(View.GONE);
                        imageSpecial.setVisibility(View.VISIBLE);
                        imageSpecial.setImageResource(R.drawable.planet_background_veil);
                        break;

                    default:
                        imagePlanet.setImageResource(mission.getImagePlanet());
                        break;
                }

                if (mission.getType() == TYPE_NORMAL) imageType.setVisibility(View.GONE);
                else {
                    imageType.setImageResource(mission.getImageType());
                    imageType.setVisibility(View.VISIBLE);
                }

                textMission.setText(mission.getName());
                textMissionObjective.setText(mission.getObjective());

                imageFaction.setImageResource(mission.getImageFaction());
            }
        });

        missionViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean filter) {
                if (filter) imageWantedFilter.setImageResource(R.drawable.reward_rare);
                else imageWantedFilter.setImageResource(R.drawable.reward_no_filter);
            }
        });

        missionViewModel.getMissionRewards().observe(getViewLifecycleOwner(), new Observer<List<MissionRewardComplete>>() {
            @Override
            public void onChanged(List<MissionRewardComplete> rewards) {
                missionRotationAdapter.updateMissionRotations(rewards);
            }
        });
        //endregion

        return view;
    }
}