package com.warframefarm.activities.list.components;

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
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.databinding.DialogComponentsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComponentDialog extends DialogFragment {
    private final Context context;
    private ComponentsViewModel componentsViewModel;
    private DialogComponentsBinding binding;

    private RecyclerView recycler;
    private Button buttonCancel, buttonSave;

    public ComponentDialog(Context context) {
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
        binding = DialogComponentsBinding.inflate(inflater, container, false);

        recycler = binding.recyclerDialogComponents;

        buttonCancel = binding.buttonDialogComponentsCancel;
        buttonSave = binding.buttonDialogComponentsSave;

        componentsViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(ComponentsViewModel.class);

        ComponentDialogAdapter adapter = new ComponentDialogAdapter(context);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(context, 3));

        buttonCancel.setOnClickListener(v -> {
            componentsViewModel.clearChanges();
            dismiss();
        });

        buttonSave.setOnClickListener(v -> {
            componentsViewModel.applyChanges();
            Toast.makeText(context, "Database Updated", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        componentsViewModel.getComponentsAfterChanges().observe(getViewLifecycleOwner(), new Observer<List<ComponentComplete>>() {
            @Override
            public void onChanged(List<ComponentComplete> componentsAfterChanges) {
                adapter.updateComponents(componentsAfterChanges);
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return binding.getRoot();
    }
}
