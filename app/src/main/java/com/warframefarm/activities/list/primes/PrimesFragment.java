package com.warframefarm.activities.list.primes;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.prime.PrimeFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.PrimeComplete;
import com.warframefarm.databinding.FragmentPrimesBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimesFragment extends Fragment implements PrimeAdapter.PrimeListener {

    private Context context;
    private MainActivity mainActivity;
    private FragmentPrimesBinding binding;
    private PrimesViewModel primesViewModel;

    private AutoCompleteTextView searchbar;
    private ImageView iconSearchbar;
    private ImageView imageWarframeFilter, imageArchwingFilter, imagePetFilter, imageSentinelFilter,
            imagePrimaryFilter, imageSecondaryFilter, imageMeleeFilter, imageArchGunFilter;
    private Spinner spinnerSort;
    private RecyclerView recyclerPrimes;

    private LinearLayout layoutSelectionMode;
    private Button buttonCancel, buttonSelectAll, buttonCheck;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        mainActivity = (MainActivity) getActivity();

        binding = FragmentPrimesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        primesViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PrimesViewModel.class);

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

        recyclerPrimes = binding.recyclerPrimes;

        layoutSelectionMode = binding.layoutSelectionMode;
        buttonCancel = binding.buttonCancel;
        buttonSelectAll = binding.buttonSelectAll;
        buttonCheck = binding.buttonCheck;
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

                primesViewModel.setSearch(search);
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
                primesViewModel.setOrder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                primesViewModel.setOrder(0);
            }
        });
        //endregion

        //region Filters
        imageWarframeFilter.setOnClickListener(v -> primesViewModel.setFilter(WARFRAME));
        imageArchwingFilter.setOnClickListener(v -> primesViewModel.setFilter(ARCHWING));
        imagePetFilter.setOnClickListener(v -> primesViewModel.setFilter(PET));
        imageSentinelFilter.setOnClickListener(v -> primesViewModel.setFilter(SENTINEL));
        imagePrimaryFilter.setOnClickListener(v -> primesViewModel.setFilter(PRIMARY));
        imageSecondaryFilter.setOnClickListener(v -> primesViewModel.setFilter(SECONDARY));
        imageMeleeFilter.setOnClickListener(v -> primesViewModel.setFilter(MELEE));
        imageArchGunFilter.setOnClickListener(v -> primesViewModel.setFilter(ARCH_GUN));
        //endregion
        //endregion

        PrimeAdapter primeAdapter = new PrimeAdapter(context, this);
        recyclerPrimes.setAdapter(primeAdapter);
        recyclerPrimes.setLayoutManager(new GridLayoutManager(context, 2));

        primeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                ((GridLayoutManager) recyclerPrimes.getLayoutManager()).scrollToPositionWithOffset(0, 0);
            }
        });

        buttonCancel.setOnClickListener(v -> primeAdapter.cancelSelection());

        buttonSelectAll.setOnClickListener(v -> primeAdapter.selectAll());

        buttonCheck.setOnClickListener(v -> {
            primesViewModel.setSelectionOwned(primeAdapter.getSelectedPrimes());
            primeAdapter.endSelection();
        });

        //region Observers
        primesViewModel.getFilter().observe(getViewLifecycleOwner(), new Observer<String>() {
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

        primesViewModel.getSelectionCheck().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean check) {
                if (check)
                    buttonCheck.setText(getString(R.string.check_selected, primesViewModel.getSelectionNb().getValue()));
                else
                    buttonCheck.setText(getString(R.string.uncheck_selected, primesViewModel.getSelectionNb().getValue()));
            }
        });

        primesViewModel.getSelectionNb().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer nb) {
                if (primesViewModel.getSelectionCheck().getValue())
                    buttonCheck.setText(getString(R.string.check_selected, nb));
                else
                    buttonCheck.setText(getString(R.string.uncheck_selected, nb));
            }
        });

        primesViewModel.getPrimes().observe(getViewLifecycleOwner(), new Observer<LiveData<List<PrimeComplete>>>() {
            @Override
            public void onChanged(LiveData<List<PrimeComplete>> primes) {
                primes.observe(getViewLifecycleOwner(), new Observer<List<PrimeComplete>>() {
                    @Override
                    public void onChanged(List<PrimeComplete> primes) {
                        primeAdapter.updatePrimes(primes);

                        searchAdapter.clear();
                        searchAdapter.addAll(primesViewModel.getPrimeNames());
                    }
                });
            }
        });
        //endregion

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        primesViewModel.updatePrimes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void showPrimeDetails(String prime) {
        mainActivity.multipleStackNavigator.start(new PrimeFragment(prime));
    }

    @Override
    public void switchPrimeOwned(PrimeComplete prime) {
        primesViewModel.switchPrimeOwned(prime);
    }

    @Override
    public void setSelectionMode(boolean selectionMode) {
        if (selectionMode) layoutSelectionMode.setVisibility(View.VISIBLE);
        else layoutSelectionMode.setVisibility(View.GONE);
    }

    @Override
    public void setSelectionParams(boolean check, int nb) {
        primesViewModel.setSelectionCheck(check);
        primesViewModel.setSelectionNb(nb);
    }
}