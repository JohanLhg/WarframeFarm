package com.warframefarm.activities.farm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.warframefarm.R;
import com.warframefarm.databinding.DialogAddItemMenuBinding;

import org.jetbrains.annotations.NotNull;

public class AddItemMenuDialog extends DialogFragment {
    private final AddItemMenuListener listener;
    private DialogAddItemMenuBinding binding;

    private Button buttonAddPrimes, buttonAddComponents, buttonCancel;

    public AddItemMenuDialog(AddItemMenuListener listener) {
        this.listener = listener;
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
        binding = DialogAddItemMenuBinding.inflate(inflater);
        View root = binding.getRoot();

        buttonAddPrimes = binding.buttonAddPrimes;
        buttonAddComponents = binding.buttonAddComponents;
        buttonCancel = binding.buttonCancel;

        buttonAddPrimes.setOnClickListener(v -> {
            listener.showAddPrimeDialog();
            dismiss();
        });

        buttonAddComponents.setOnClickListener(v -> {
            listener.showAddComponentDialog();
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return root;
    }

    public interface AddItemMenuListener {
        void showAddPrimeDialog();
        void showAddComponentDialog();
    }
}
