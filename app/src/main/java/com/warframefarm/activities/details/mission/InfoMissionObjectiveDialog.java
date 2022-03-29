package com.warframefarm.activities.details.mission;

import static com.warframefarm.data.WarframeConstants.ARENA;
import static com.warframefarm.data.WarframeConstants.ASSASSINATION;
import static com.warframefarm.data.WarframeConstants.ASSAULT;
import static com.warframefarm.data.WarframeConstants.CAPTURE;
import static com.warframefarm.data.WarframeConstants.DEFECTION;
import static com.warframefarm.data.WarframeConstants.DEFENSE;
import static com.warframefarm.data.WarframeConstants.DISRUPTION;
import static com.warframefarm.data.WarframeConstants.EXCAVATION;
import static com.warframefarm.data.WarframeConstants.EXTERMINATE;
import static com.warframefarm.data.WarframeConstants.HIJACK;
import static com.warframefarm.data.WarframeConstants.INFESTED_SALVAGE;
import static com.warframefarm.data.WarframeConstants.INTERCEPTION;
import static com.warframefarm.data.WarframeConstants.MOBILE_DEFENSE;
import static com.warframefarm.data.WarframeConstants.ORPHIX;
import static com.warframefarm.data.WarframeConstants.PURSUIT;
import static com.warframefarm.data.WarframeConstants.RATHUUM;
import static com.warframefarm.data.WarframeConstants.RESCUE;
import static com.warframefarm.data.WarframeConstants.RUSH;
import static com.warframefarm.data.WarframeConstants.SABOTAGE;
import static com.warframefarm.data.WarframeConstants.SKIRMISH;
import static com.warframefarm.data.WarframeConstants.SPY;
import static com.warframefarm.data.WarframeConstants.SURVIVAL;
import static com.warframefarm.data.WarframeConstants.VOLATILE;

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

        switch (objective) {
            case ARENA:
                textInfoTitle.setText(R.string.arena_mission);
                textInfoDescription.setText(R.string.arena_description);
                break;
            case ASSASSINATION:
                textInfoTitle.setText(R.string.assassination_mission);
                textInfoDescription.setText(R.string.assassination_description);
                break;
            case ASSAULT:
                textInfoTitle.setText(R.string.assault_mission);
                textInfoDescription.setText(R.string.assault_description);
                break;
            case CAPTURE:
                textInfoTitle.setText(R.string.capture_mission);
                textInfoDescription.setText(R.string.capture_description);
                break;
            case DEFECTION:
                textInfoTitle.setText(R.string.defection_mission);
                textInfoDescription.setText(R.string.defection_description);
                textInfoRotations.setText(R.string.defection_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case DEFENSE:
                textInfoTitle.setText(R.string.defense_mission);
                textInfoDescription.setText(R.string.defense_description);
                textInfoRotations.setText(R.string.defense_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case DISRUPTION:
                textInfoTitle.setText(R.string.disruption_mission);
                textInfoDescription.setText(R.string.disruption_description);
                textInfoRotations.setText(R.string.disruption_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                tableDisruption.setVisibility(View.VISIBLE);
                textInfoEfficiency.setText(R.string.disruption_efficiency);
                textInfoEfficiency.setVisibility(View.VISIBLE);
                break;
            case EXCAVATION:
                textInfoTitle.setText(R.string.excavation_mission);
                textInfoDescription.setText(R.string.excavation_description);
                textInfoRotations.setText(R.string.excavation_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case EXTERMINATE:
                textInfoTitle.setText(R.string.exterminate_mission);
                textInfoDescription.setText(R.string.exterminate_description);
                break;
            case HIJACK:
                textInfoTitle.setText(R.string.hijack_mission);
                textInfoDescription.setText(R.string.hijack_description);
                break;
            case INFESTED_SALVAGE:
                textInfoTitle.setText(R.string.infested_salvage_mission);
                textInfoDescription.setText(R.string.infested_salvage_description);
                textInfoRotations.setText(R.string.infested_salvage_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case INTERCEPTION:
                textInfoTitle.setText(R.string.interception_mission);
                textInfoDescription.setText(R.string.interception_description);
                textInfoRotations.setText(R.string.interception_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case MOBILE_DEFENSE:
                textInfoTitle.setText(R.string.mobile_defense_mission);
                textInfoDescription.setText(R.string.mobile_defense_description);
                break;
            case PURSUIT:
                textInfoTitle.setText(R.string.pursuit_mission);
                textInfoDescription.setText(R.string.pursuit_description);
                break;
            case RATHUUM:
                textInfoTitle.setText(R.string.rathuum_mission);
                textInfoDescription.setText(R.string.rathuum_description);
                break;
            case RESCUE:
                textInfoTitle.setText(R.string.rescue_mission);
                textInfoDescription.setText(R.string.rescue_description);
                break;
            case RUSH:
                textInfoTitle.setText(R.string.rush_mission);
                textInfoDescription.setText(R.string.rush_description);
                textInfoRotations.setText(R.string.rush_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case SABOTAGE:
                textInfoTitle.setText(R.string.sabotage_mission);
                textInfoDescription.setText(R.string.sabotage_description);
                break;
            case SKIRMISH:
                textInfoTitle.setText(R.string.skirmish_mission);
                textInfoDescription.setText(R.string.skirmish_description);
                break;
            case SPY:
                textInfoTitle.setText(R.string.spy_mission);
                textInfoDescription.setText(R.string.spy_description);
                textInfoRotations.setText(R.string.spy_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case SURVIVAL:
                textInfoTitle.setText(R.string.survival_mission);
                textInfoDescription.setText(R.string.survival_description);
                textInfoRotations.setText(R.string.survival_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            case VOLATILE:
                textInfoTitle.setText(R.string.volatile_mission);
                textInfoDescription.setText(R.string.volatile_description);
                break;
            case ORPHIX:
                textInfoTitle.setText(R.string.orphix_mission);
                textInfoDescription.setText(R.string.orphix_description);
                textInfoRotations.setText(R.string.orphix_rotations);
                textInfoRotations.setVisibility(View.VISIBLE);
                break;
            default: dismiss(); break;
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return binding.getRoot();
    }
}
