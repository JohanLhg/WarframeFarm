package com.warframefarm.activities.list.relics;

import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;

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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.databinding.FragmentRelicsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RelicsFragment extends Fragment {

    private Context context;
    private RelicsViewModel relicsViewModel;
    private FragmentRelicsBinding binding;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerSort;
    private ImageView imageLithFilter, imageMesoFilter, imageNeoFilter, imageAxiFilter;

    private RecyclerView recyclerRelics;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        binding = FragmentRelicsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        relicsViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(RelicsViewModel.class);

        //region Binding
        searchbar = binding.searchbar.searchbar;
        iconSearchbar = binding.searchbar.iconSearchbar;
        spinnerSort = binding.searchbar.spinnerSort;

        imageLithFilter = binding.imageLithFilter;
        imageMesoFilter = binding.imageMesoFilter;
        imageNeoFilter = binding.imageNeoFilter;
        imageAxiFilter = binding.imageAxiFilter;
        recyclerRelics = binding.recyclerRelics;
        //endregion

        //region Toolbar
        //region Searchbar
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

                relicsViewModel.setSearch(search);
            }
        });

        iconSearchbar.setOnClickListener(v -> {
            searchbar.setText("");
            searchbar.requestFocus();
        });
        //endregion

        //region Spinner
        ArrayList<String> sortOptions = new ArrayList<>();
        Collections.addAll(sortOptions,
                getString(R.string.needed),
                getString(R.string.vaulted),
                getString(R.string.era)
        );

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, sortOptions);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relicsViewModel.setOrder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                relicsViewModel.setOrder(0);
            }
        });
        //endregion

        //region Filters
        imageLithFilter.setOnClickListener(v -> relicsViewModel.setFilter(LITH));
        imageMesoFilter.setOnClickListener(v -> relicsViewModel.setFilter(MESO));
        imageNeoFilter.setOnClickListener(v -> relicsViewModel.setFilter(NEO));
        imageAxiFilter.setOnClickListener(v -> relicsViewModel.setFilter(AXI));
        //endregion
        //endregion

        //region Recycler
        RelicAdapter relicAdapter = new RelicAdapter(context);
        recyclerRelics.setAdapter(relicAdapter);
        recyclerRelics.setLayoutManager(new GridLayoutManager(context, 3));
        //endregion

        //region Observers
        relicsViewModel.getComponentIDs().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> ids) {
                ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, ids);
                searchbar.setAdapter(searchAdapter);
            }
        });

        relicsViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String filter) {
                imageLithFilter.setImageResource(R.drawable.relic_lith);
                imageMesoFilter.setImageResource(R.drawable.relic_meso);
                imageNeoFilter.setImageResource(R.drawable.relic_neo);
                imageAxiFilter.setImageResource(R.drawable.relic_axi);

                switch (filter) {
                    case LITH:
                        imageLithFilter.setImageResource(R.drawable.relic_lith_selected);
                        break;
                    case MESO:
                        imageMesoFilter.setImageResource(R.drawable.relic_meso_selected);
                        break;
                    case NEO:
                        imageNeoFilter.setImageResource(R.drawable.relic_neo_selected);
                        break;
                    case AXI:
                        imageAxiFilter.setImageResource(R.drawable.relic_axi_selected);
                        break;
                }
            }
        });

        relicsViewModel.getRelics().observe(getViewLifecycleOwner(), new Observer<LiveData<List<RelicComplete>>>() {
            @Override
            public void onChanged(LiveData<List<RelicComplete>> relics) {
                relics.observe(getViewLifecycleOwner(), new Observer<List<RelicComplete>>() {
                    @Override
                    public void onChanged(List<RelicComplete> relics) {
                        relicAdapter.updateRelics(relics);
                    }
                });
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