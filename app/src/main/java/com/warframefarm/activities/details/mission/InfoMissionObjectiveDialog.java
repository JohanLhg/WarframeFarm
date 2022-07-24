package com.warframefarm.activities.details.mission;

import static com.warframefarm.data.WarframeConstants.DISRUPTION;
import static com.warframefarm.data.WarframeLists.MissionDescription;
import static com.warframefarm.data.WarframeLists.MissionEfficiencyInfo;
import static com.warframefarm.data.WarframeLists.MissionName;
import static com.warframefarm.data.WarframeLists.MissionRotationsInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.warframefarm.R;
import com.warframefarm.databinding.DialogInfoMissionObjectiveBinding;

import org.jetbrains.annotations.NotNull;

public class InfoMissionObjectiveDialog extends DialogFragment {

    private DialogInfoMissionObjectiveBinding binding;
    private final String objective;

    private TextView textInfoTitle, textInfoDescription, textInfoRotations, textInfoEfficiency;
    private ConstraintLayout tableDisruption;

    public InfoMissionObjectiveDialog(String objective) {
        this.objective = objective;
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
        binding = DialogInfoMissionObjectiveBinding.inflate(inflater, container, false);

        textInfoTitle = binding.textInfoTitle;
        textInfoDescription = binding.textInfoDescription;
        textInfoRotations = binding.textInfoRotations;
        tableDisruption = binding.tableDisruption;
        textInfoEfficiency = binding.textInfoEfficiency;

        textInfoTitle.setText(MissionName.containsKey(objective) ? getString(MissionName.get(objective)) : objective);

        if (MissionDescription.containsKey(objective))
            textInfoDescription.setText(MissionDescription.get(objective));

        if (MissionRotationsInfo.containsKey(objective)) {
            textInfoRotations.setVisibility(View.VISIBLE);
            textInfoRotations.setText(MissionRotationsInfo.get(objective));
        } else textInfoRotations.setVisibility(View.GONE);

        if (MissionEfficiencyInfo.containsKey(objective)) {
            textInfoEfficiency.setVisibility(View.VISIBLE);
            textInfoEfficiency.setText(MissionEfficiencyInfo.get(objective));
        } else textInfoEfficiency.setVisibility(View.GONE);

        if (objective.equals(DISRUPTION))
            tableDisruption.setVisibility(View.VISIBLE);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return binding.getRoot();
    }
}
