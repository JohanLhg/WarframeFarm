package com.warframefarm.activities.details.planet;

import static com.warframefarm.database.WarframeFarmDatabase.TYPE_ARCHWING;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.warframefarm.R;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.Mission;
import com.warframefarm.database.Planet;
import com.warframefarm.databinding.FragmentPlanetBinding;

import java.util.ArrayList;
import java.util.List;

public class PlanetFragment extends Fragment {

    private Context context;
    private MainActivity activity;
    private FragmentPlanetBinding binding;
    private PlanetViewModel planetViewModel;
    private final String planet;

    private MissionAdapter missionAdapter;

    private ImageView imageSpecial, imagePlanet, imageFaction, imageNormalFilter,
            imageArchwingFilter, imageEmpyreanFilter, imageWantedFilter;
    private TextView textPlanet;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;

    private RecyclerView recyclerMissions;
    private ProgressBar loadingPlanet;
    private TextView textEmptyStatePlanet;

    public PlanetFragment(String planet) {
        this.planet = planet;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        activity = (MainActivity) getActivity();

        binding = FragmentPlanetBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        planetViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(activity.getApplication())
                .create(PlanetViewModel.class);

        planetViewModel.setPlanet(planet);

        //region Binding
        imageFaction = binding.imageFaction;
        textPlanet = binding.textPlanet;
        imageSpecial = binding.imageSpecial;
        imagePlanet = binding.imagePlanet;
        imageNormalFilter = binding.imageNormalFilter;
        imageArchwingFilter = binding.imageArchwingFilter;
        imageEmpyreanFilter = binding.imageEmpyreanFilter;
        imageWantedFilter = binding.imageWantedFilter;

        searchbar = binding.searchbarPlanet;
        iconSearchbar = binding.iconSearchbarPlanet;

        recyclerMissions = binding.recyclerMissions;
        loadingPlanet = binding.loadingPlanet;
        textEmptyStatePlanet = binding.textEmptyStatePlanet;
        //endregion

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

                planetViewModel.setSearch(search);
            }
        });

        iconSearchbar.setOnClickListener(v -> {
            searchbar.setText("");
            searchbar.requestFocus();
        });
        //endregion

        imageNormalFilter.setOnClickListener(v -> planetViewModel.setTypeFilter(TYPE_NORMAL));
        imageArchwingFilter.setOnClickListener(v -> planetViewModel.setTypeFilter(TYPE_ARCHWING));
        imageEmpyreanFilter.setOnClickListener(v -> planetViewModel.setTypeFilter(TYPE_EMPYREAN));
        imageWantedFilter.setOnClickListener(v -> planetViewModel.switchRelicFilter());

        missionAdapter = new MissionAdapter(context);
        recyclerMissions.setAdapter(missionAdapter);
        recyclerMissions.setLayoutManager(new LinearLayoutManager(context));

        //region Observers
        planetViewModel.getPlanet().observe(getViewLifecycleOwner(), new Observer<Planet>() {
            @Override
            public void onChanged(Planet planet) {
                imageFaction.setBackgroundResource(planet.getImageFaction());

                switch (planet.getName()) {
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

                    case "Zariman":
                        imagePlanet.setVisibility(View.GONE);
                        imageSpecial.setVisibility(View.VISIBLE);
                        imageSpecial.setImageResource(R.drawable.planet_background_zariman);
                        break;

                    default:
                        imagePlanet.setImageResource(planet.getImage());
                        //StorageReference ref = FirebaseStorage.getInstance().getReference().child("Images/Planets/" + planet.getName().toLowerCase() + ".png");
                        //Glide.with(context)
                        //        .load(ref)
                        //        .into(imagePlanet);
                        break;
                }

                textPlanet.setText(planet.getName());
            }
        });

        planetViewModel.getTypeList().observe(getViewLifecycleOwner(), new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> types) {
                imageNormalFilter.setVisibility(View.GONE);
                imageArchwingFilter.setVisibility(View.GONE);
                imageEmpyreanFilter.setVisibility(View.GONE);
                if (types.size() == 1)
                    return;
                if (types.contains(TYPE_NORMAL))
                    imageNormalFilter.setVisibility(View.VISIBLE);
                if (types.contains(TYPE_ARCHWING))
                    imageArchwingFilter.setVisibility(View.VISIBLE);
                if (types.contains(TYPE_EMPYREAN))
                    imageEmpyreanFilter.setVisibility(View.VISIBLE);
            }
        });

        planetViewModel.getRelicFilter().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean filter) {
                if (filter) imageWantedFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                else imageWantedFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
            }
        });

        planetViewModel.getTypeFilter().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer type) {
                switch (type) {
                    case TYPE_NORMAL:
                        imageNormalFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        imageEmpyreanFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        break;
                    case TYPE_ARCHWING:
                        imageNormalFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        imageEmpyreanFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        break;
                    case TYPE_EMPYREAN:
                        imageNormalFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        imageEmpyreanFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    default:
                        imageNormalFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        imageEmpyreanFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                        break;
                }
            }
        });

        planetViewModel.getMissions().observe(getViewLifecycleOwner(), new Observer<List<Mission>>() {
            @Override
            public void onChanged(List<Mission> missions) {
                if (missions.isEmpty()) {
                    textEmptyStatePlanet.setVisibility(View.VISIBLE);
                    recyclerMissions.setVisibility(View.GONE);
                }
                else {
                    textEmptyStatePlanet.setVisibility(View.GONE);
                    recyclerMissions.setVisibility(View.VISIBLE);
                }
                missionAdapter.updateMissions(missions);
            }
        });
        //endregion

        return view;
    }
}