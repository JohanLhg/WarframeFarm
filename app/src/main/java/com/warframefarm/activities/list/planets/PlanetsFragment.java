package com.warframefarm.activities.list.planets;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.Planet;
import com.warframefarm.databinding.FragmentPlanetsBinding;

import java.util.ArrayList;
import java.util.List;

public class PlanetsFragment extends Fragment {

    private Context context;
    private PlanetsViewModel planetsViewModel;
    private FragmentPlanetsBinding binding;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;

    private RecyclerView recyclerPlanets;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        planetsViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PlanetsViewModel.class);

        binding = FragmentPlanetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //region Binding
        searchbar = binding.searchbarPlanets;
        iconSearchbar = binding.iconSearchbarPlanets;

        recyclerPlanets = binding.recyclerPlanets;
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

                planetsViewModel.setSearch(search);
            }
        });

        iconSearchbar.setOnClickListener(v -> {
            searchbar.setText("");
            searchbar.requestFocus();
        });
        //endregion
        //endregion

        //region Recycler
        PlanetAdapter planetAdapter = new PlanetAdapter(context);
        planetsViewModel.setSearch("");
        recyclerPlanets.setAdapter(planetAdapter);
        recyclerPlanets.setLayoutManager(new GridLayoutManager(context, 3));
        //endregion

        //region Observers
        planetsViewModel.getPlanets().observe(getViewLifecycleOwner(), new Observer<List<Planet>>() {
            @Override
            public void onChanged(@Nullable List<Planet> planets) {
                planetAdapter.updatePlanets(planets);
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
}