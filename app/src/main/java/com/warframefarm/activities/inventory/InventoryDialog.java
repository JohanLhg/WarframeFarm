package com.warframefarm.activities.inventory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.database.PartComplete;
import com.warframefarm.databinding.DialogInventoryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InventoryDialog extends DialogFragment {
    private final Context context;
    private InventoryViewModel inventoryViewModel;
    private DialogInventoryBinding binding;

    private RecyclerView recycler;
    private Button buttonCancel, buttonSave;

    public InventoryDialog(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DialogInventoryBinding.inflate(inflater, container, false);

        recycler = binding.recyclerDialogInventory;

        buttonCancel = binding.buttonDialogInventoryCancel;
        buttonSave = binding.buttonDialogInventorySave;

        inventoryViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(InventoryViewModel.class);

        InventoryDialogAdapter adapter = new InventoryDialogAdapter(context);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(context, 3));

        buttonCancel.setOnClickListener(v -> {
            inventoryViewModel.clearChanges();
            dismiss();
        });

        buttonSave.setOnClickListener(v -> {
            inventoryViewModel.applyChanges();
            Toast.makeText(context, "Database Updated", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        inventoryViewModel.getPartsAfterChanges().observe(getViewLifecycleOwner(), new Observer<List<PartComplete>>() {
            @Override
            public void onChanged(List<PartComplete> partsAfterChanges) {
                adapter.updateParts(partsAfterChanges);
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return binding.getRoot();
    }
}
