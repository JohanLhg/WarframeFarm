package com.warframefarm.activities.inventory;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
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
import com.warframefarm.database.PartComplete;
import com.warframefarm.databinding.FragmentInventoryBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryFragment extends Fragment implements InventoryAdapter.InventoryListener {

    private Context context;
    private FragmentInventoryBinding binding;
    private InventoryViewModel inventoryViewModel;

    private InventoryAdapter inventoryAdapter;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private Spinner spinnerSort;
    private ImageView imageWarframeFilter, imageArchwingFilter, imagePetFilter,
            imageSentinelFilter, imagePrimaryFilter, imageSecondaryFilter, imageMeleeFilter;

    private TextView textEmptyState;
    private RecyclerView recycler;

    private LinearLayout layoutInventoryChanges;
    private Button buttonCancel, buttonSave;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        inventoryViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(InventoryViewModel.class);

        binding = FragmentInventoryBinding.inflate(getLayoutInflater());
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

        textEmptyState = binding.textEmptyStateInventory;
        recycler = binding.recyclerInventory;

        layoutInventoryChanges = binding.layoutInventoryChanges;
        buttonCancel = binding.buttonInventoryCancel;
        buttonSave = binding.buttonInventorySave;
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

                inventoryViewModel.setSearch(search);
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
                inventoryViewModel.setOrder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                inventoryViewModel.setOrder(0);
            }
        });
        //endregion

        //region Filters
        imageWarframeFilter.setOnClickListener(v -> inventoryViewModel.setFilter(WARFRAME));
        imageArchwingFilter.setOnClickListener(v -> inventoryViewModel.setFilter(ARCHWING));
        imagePetFilter.setOnClickListener(v -> inventoryViewModel.setFilter(PET));
        imageSentinelFilter.setOnClickListener(v -> inventoryViewModel.setFilter(SENTINEL));
        imagePrimaryFilter.setOnClickListener(v -> inventoryViewModel.setFilter(PRIMARY));
        imageSecondaryFilter.setOnClickListener(v -> inventoryViewModel.setFilter(SECONDARY));
        imageMeleeFilter.setOnClickListener(v -> inventoryViewModel.setFilter(MELEE));
        //endregion
        //endregion

        inventoryAdapter = new InventoryAdapter(context, this);
        recycler.setAdapter(inventoryAdapter);
        recycler.setLayoutManager(new GridLayoutManager(context, 3));

        buttonCancel.setOnClickListener(v -> inventoryViewModel.clearChanges());

        buttonSave.setOnClickListener(v -> showConfirmDialog());

        //region Observers
        inventoryViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String filter) {
                imageWarframeFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imagePetFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageSentinelFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imagePrimaryFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageSecondaryFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));
                imageMeleeFilter.setImageTintList(context.getColorStateList(R.color.colorBackgroundDark));

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
                }
            }
        });

        inventoryViewModel.getParts().observe(getViewLifecycleOwner(), new Observer<LiveData<List<PartComplete>>>() {
            @Override
            public void onChanged(LiveData<List<PartComplete>> parts) {
                parts.observe(getViewLifecycleOwner(), new Observer<List<PartComplete>>() {
                    @Override
                    public void onChanged(List<PartComplete> parts) {
                        inventoryAdapter.updateParts(parts);
                        if (parts.isEmpty()) {
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

        inventoryViewModel.getPartsAfterChanges().observe(getViewLifecycleOwner(), new Observer<List<PartComplete>>() {
            @Override
            public void onChanged(List<PartComplete> partsAfterChanges) {
                if (partsAfterChanges.isEmpty())
                    layoutInventoryChanges.setVisibility(View.GONE);
                else
                    layoutInventoryChanges.setVisibility(View.VISIBLE);

                inventoryAdapter.updateChangedParts(partsAfterChanges);
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
        final InventoryDialog dialog = new InventoryDialog(context);
        dialog.show(fm, "Inventory Dialog");
    }

    @Override
    public void modifyPart(PartComplete part) {
        inventoryViewModel.modifyPart(part);
    }
}