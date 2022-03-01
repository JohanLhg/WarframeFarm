package com.warframefarm.activities.farm;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_NAME;
import static com.warframefarm.database.WarframeFarmDatabase.PRIME_TYPE;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.PartComplete;
import com.warframefarm.databinding.DialogAddItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddPartDialog extends DialogFragment {
    private final Context context;
    private DialogAddItemBinding binding;
    private FarmDialogViewModel dialogViewModel;

    private SelectPartAdapter itemAdapter;

    private TextView titleAddItem;
    private AutoCompleteTextView searchbar;
    private Spinner spinnerSort;
    private ImageView iconSearchbar, imageWarframeFilter, imageArchwingFilter, imagePetFilter,
            imageSentinelFilter, imagePrimaryFilter, imageSecondaryFilter, imageMeleeFilter;
    private RecyclerView recyclerItems;
    private Button buttonAddItems, buttonCancel;

    public AddPartDialog(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //region Binding
        binding = DialogAddItemBinding.inflate(inflater);
        View root = binding.getRoot();

        titleAddItem = binding.titleAddItem;

        searchbar = binding.searchBarAddItem.searchbar;
        iconSearchbar = binding.searchBarAddItem.iconSearchbar;

        spinnerSort = binding.searchBarAddItem.spinnerSort;

        imageWarframeFilter = binding.primeFilter.imageWarframeFilter;
        imageArchwingFilter = binding.primeFilter.imageArchwingFilter;
        imagePetFilter = binding.primeFilter.imagePetFilter;
        imageSentinelFilter = binding.primeFilter.imageSentinelFilter;
        imagePrimaryFilter = binding.primeFilter.imagePrimaryFilter;
        imageSecondaryFilter = binding.primeFilter.imageSecondaryFilter;
        imageMeleeFilter = binding.primeFilter.imageMeleeFilter;

        recyclerItems = binding.recyclerItems;

        buttonAddItems = binding.buttonAddItems;
        buttonCancel = binding.buttonCancel;
        //endregion

        dialogViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(FarmDialogViewModel.class);

        titleAddItem.setText(R.string.title_add_parts);

        //region Toolbar
        //region Searchbar
        ArrayList<String> searchPrimes = new ArrayList<>();
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, searchPrimes);
        searchbar.setAdapter(searchAdapter);

        searchbar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                iconSearchbar.setImageTintList(context.getColorStateList(R.color.colorAccent));
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

                dialogViewModel.setSearch(search);
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
                getString(R.string.type),
                getString(R.string.name),
                getString(R.string.needed)
        );
        ArrayList<String> sortOptionValues = new ArrayList<>();
        Collections.addAll(sortOptionValues,
                PRIME_TYPE,
                PRIME_NAME,
                ""
        );
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, sortOptions);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dialogViewModel.setOrder(sortOptionValues.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dialogViewModel.setOrder(sortOptionValues.get(0));
            }
        });
        //endregion

        //region Filters
        imageWarframeFilter.setOnClickListener(v -> dialogViewModel.setFilter(WARFRAME));
        imageArchwingFilter.setOnClickListener(v -> dialogViewModel.setFilter(ARCHWING));
        imagePetFilter.setOnClickListener(v -> dialogViewModel.setFilter(PET));
        imageSentinelFilter.setOnClickListener(v -> dialogViewModel.setFilter(SENTINEL));
        imagePrimaryFilter.setOnClickListener(v -> dialogViewModel.setFilter(PRIMARY));
        imageSecondaryFilter.setOnClickListener(v -> dialogViewModel.setFilter(SECONDARY));
        imageMeleeFilter.setOnClickListener(v -> dialogViewModel.setFilter(MELEE));
        //endregion
        //endregion

        itemAdapter = new SelectPartAdapter(context);
        recyclerItems.setAdapter(itemAdapter);
        recyclerItems.setLayoutManager(new LinearLayoutManager(context));

        buttonAddItems.setText(R.string.button_add_part);
        buttonAddItems.setOnClickListener(v -> {
            List<String> partNames = itemAdapter.getSelectedPartNames();
            List<PartComplete> parts = itemAdapter.getSelectedParts();
            dialogViewModel.addParts(partNames, parts);
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        //region Observers
        dialogViewModel.getRemainingParts().observe(getViewLifecycleOwner(), new Observer<List<PartComplete>>() {
            @Override
            public void onChanged(List<PartComplete> parts) {
                itemAdapter.updateItems(parts);
            }
        });

        dialogViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<String>() {
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
                        imageWarframeFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                    case ARCHWING:
                        imageArchwingFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                    case PET:
                        imagePetFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                    case SENTINEL:
                        imageSentinelFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                    case PRIMARY:
                        imagePrimaryFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                    case SECONDARY:
                        imageSecondaryFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                    case MELEE:
                        imageMeleeFilter.setImageTintList(context.getColorStateList(R.color.colorAccent));
                        break;
                }
            }
        });
        //endregion

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return root;
    }
}
