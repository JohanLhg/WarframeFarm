package com.warframefarm.activities.list.components;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.ARCH_GUN;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.databinding.FragmentComponentsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentsFragment extends Fragment implements ComponentAdapter.ComponentListener {

    private Context context;
    private FragmentComponentsBinding binding;
    private ComponentsViewModel componentsViewModel;

    private ComponentAdapter componentAdapter;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerSort;
    private ImageView imageWarframeFilter, imageArchwingFilter, imagePetFilter, imageSentinelFilter,
            imagePrimaryFilter, imageSecondaryFilter, imageMeleeFilter, imageArchGunFilter;

    private TextView textEmptyState;
    private RecyclerView recycler;

    private LinearLayout layoutComponentChanges;
    private Button buttonCancel, buttonSave;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        componentsViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(ComponentsViewModel.class);

        binding = FragmentComponentsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        //region Binding
        searchbar = binding.searchbar.searchbar;
        iconSearchbar = binding.searchbar.iconSearchbar;
        spinnerSort = binding.searchbar.spinnerSort;

        imageWarframeFilter = binding.primeFilter.imageWarframeFilter;
        imageArchwingFilter = binding.primeFilter.imageArchwingFilter;
        imagePetFilter = binding.primeFilter.imagePetFilter;
        imageSentinelFilter = binding.primeFilter.imageSentinelFilter;
        imagePrimaryFilter = binding.primeFilter.imagePrimaryFilter;
        imageSecondaryFilter = binding.primeFilter.imageSecondaryFilter;
        imageMeleeFilter = binding.primeFilter.imageMeleeFilter;
        imageArchGunFilter = binding.primeFilter.imageArchGunFilter;

        textEmptyState = binding.textEmptyStateComponents;
        recycler = binding.recyclerComponents;

        layoutComponentChanges = binding.layoutComponentsChanges;
        buttonCancel = binding.buttonComponentsCancel;
        buttonSave = binding.buttonComponentsSave;
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

                componentsViewModel.setSearch(search);
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
                getString(R.string.type),
                getString(R.string.name)
        );

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, sortOptions);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                componentsViewModel.setOrder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                componentsViewModel.setOrder(0);
            }
        });
        //endregion

        //region Filters
        imageWarframeFilter.setOnClickListener(v -> componentsViewModel.setFilter(WARFRAME));
        imageArchwingFilter.setOnClickListener(v -> componentsViewModel.setFilter(ARCHWING));
        imagePetFilter.setOnClickListener(v -> componentsViewModel.setFilter(PET));
        imageSentinelFilter.setOnClickListener(v -> componentsViewModel.setFilter(SENTINEL));
        imagePrimaryFilter.setOnClickListener(v -> componentsViewModel.setFilter(PRIMARY));
        imageSecondaryFilter.setOnClickListener(v -> componentsViewModel.setFilter(SECONDARY));
        imageMeleeFilter.setOnClickListener(v -> componentsViewModel.setFilter(MELEE));
        imageArchGunFilter.setOnClickListener(v -> componentsViewModel.setFilter(ARCH_GUN));
        //endregion
        //endregion

        componentAdapter = new ComponentAdapter(context, this);
        recycler.setAdapter(componentAdapter);
        recycler.setLayoutManager(new GridLayoutManager(context, 3));

        buttonCancel.setOnClickListener(v -> componentsViewModel.clearChanges());

        buttonSave.setOnClickListener(v -> showConfirmDialog());

        //region Observers
        componentsViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String filter) {
                imageWarframeFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imagePetFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageSentinelFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imagePrimaryFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageSecondaryFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageMeleeFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageArchGunFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));

                switch (filter) {
                    case WARFRAME:
                        imageWarframeFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case ARCHWING:
                        imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case PET:
                        imagePetFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case SENTINEL:
                        imageSentinelFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case PRIMARY:
                        imagePrimaryFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case SECONDARY:
                        imageSecondaryFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case MELEE:
                        imageMeleeFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                    case ARCH_GUN:
                        imageArchGunFilter.setImageTintList(context.getColorStateList(R.color.colorPrimary));
                        break;
                }
            }
        });

        componentsViewModel.getComponents().observe(getViewLifecycleOwner(), new Observer<LiveData<List<ComponentComplete>>>() {
            @Override
            public void onChanged(LiveData<List<ComponentComplete>> components) {
                components.observe(getViewLifecycleOwner(), new Observer<List<ComponentComplete>>() {
                    @Override
                    public void onChanged(List<ComponentComplete> components) {
                        componentAdapter.updateComponents(components);
                        if (components.isEmpty()) {
                            recycler.setVisibility(View.GONE);
                            textEmptyState.setVisibility(View.VISIBLE);
                        }
                        else {
                            recycler.setVisibility(View.VISIBLE);
                            textEmptyState.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        componentsViewModel.getComponentsAfterChanges().observe(getViewLifecycleOwner(), new Observer<List<ComponentComplete>>() {
            @Override
            public void onChanged(List<ComponentComplete> componentsAfterChanges) {
                if (componentsAfterChanges.isEmpty())
                    layoutComponentChanges.setVisibility(View.GONE);
                else
                    layoutComponentChanges.setVisibility(View.VISIBLE);

                componentAdapter.updateChangedComponents(componentsAfterChanges);
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

    private void showConfirmDialog() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final ComponentDialog dialog = new ComponentDialog(context);
        dialog.show(fm, "Component Dialog");
    }

    @Override
    public void modifyComponent(ComponentComplete component) {
        componentsViewModel.modifyComponent(component);
    }
}