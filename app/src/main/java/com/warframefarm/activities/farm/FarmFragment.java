package com.warframefarm.activities.farm;

import static com.warframefarm.activities.farm.FarmRepository.MISSION;
import static com.warframefarm.activities.farm.FarmRepository.RELIC;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.RelicDisplayAdapter;
import com.warframefarm.activities.list.relics.PlanetMissionFarmAdapter;
import com.warframefarm.database.MissionComplete;
import com.warframefarm.database.RelicComplete;
import com.warframefarm.databinding.FragmentFarmBinding;
import com.warframefarm.database.Item;

import java.util.List;

public class FarmFragment extends Fragment implements ItemAdapter.ItemListener, AddItemMenuDialog.AddItemMenuListener {

    private Context context;
    private FragmentFarmBinding binding;
    private FarmViewModel farmViewModel;

    private ItemAdapter itemAdapter;
    private RelicDisplayAdapter relicAdapter;
    private PlanetMissionFarmAdapter missionAdapter;

    private Button buttonSecondary, buttonPrimary;
    private ImageButton buttonAddItem;

    private LinearLayout checkResult;
    private ImageView iconCheckResult;
    private TextView textEmptyStateItems, textEmptyStateResult, textCheckResult, textResult;
    private RecyclerView recyclerItems, recyclerResult;

    private LinearLayout buttonRelic, buttonMission;
    private ImageView iconRelic, iconMission;
    private TextView textRelic, textMission;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getContext();

        binding = FragmentFarmBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        farmViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(FarmViewModel.class);

        //region Binding
        buttonSecondary = binding.buttonSecondary;
        buttonPrimary = binding.buttonPrimary;
        buttonAddItem = binding.buttonAddItem;

        recyclerItems = binding.recyclerItems;
        textEmptyStateItems = binding.textEmptyStateItems;

        textResult = binding.textResult;
        checkResult = binding.checkResult;
        textCheckResult = binding.textCheckResult;
        iconCheckResult = binding.iconCheckResult;
        recyclerResult = binding.recyclerResult;
        textEmptyStateResult = binding.textEmptyStateResult;

        buttonRelic = binding.buttonRelic;
        iconRelic = binding.iconRelic;
        textRelic = binding.textRelic;

        buttonMission = binding.buttonMission;
        iconMission = binding.iconMission;
        textMission = binding.textMission;
        //endregion

        buttonSecondary.setOnClickListener(v -> farmViewModel.clearItems());

        buttonPrimary.setOnClickListener(v -> farmViewModel.addAllNeededItems());

        buttonAddItem.setOnClickListener(v -> {
            final FragmentManager fm = getActivity().getSupportFragmentManager();
            final AddItemMenuDialog dialog = new AddItemMenuDialog(this);
            dialog.show(fm, "Add Item Menu Dialog");
        });

        itemAdapter = new ItemAdapter(context, this);
        recyclerItems.setAdapter(itemAdapter);
        recyclerItems.setLayoutManager(new LinearLayoutManager(context));

        checkResult.setOnClickListener(v -> farmViewModel.checkFilter());

        relicAdapter = new RelicDisplayAdapter(context);
        missionAdapter = new PlanetMissionFarmAdapter(context);
        recyclerResult.setAdapter(relicAdapter);
        recyclerResult.setLayoutManager(new LinearLayoutManager(context));

        buttonRelic.setOnClickListener(v -> farmViewModel.setMode(RELIC));

        buttonMission.setOnClickListener(v -> farmViewModel.setMode(MISSION));

        //region Observers
        farmViewModel.getItems().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                if (items.isEmpty()) textEmptyStateItems.setVisibility(View.VISIBLE);
                else textEmptyStateItems.setVisibility(View.GONE);
                itemAdapter.updateItems(items);
            }
        });

        farmViewModel.getMode().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer mode) {
                if (mode == RELIC) {
                    iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                    textRelic.setTextColor(context.getColor(R.color.colorAccent));

                    iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                    textMission.setTextColor(context.getColor(R.color.colorBackgroundDark));

                    textResult.setText(R.string.relics_label);

                    textCheckResult.setText(R.string.vaulted_label);
                    if (farmViewModel.getRelicFilter().getValue())
                        iconCheckResult.setBackgroundResource(R.drawable.checked);
                    else
                        iconCheckResult.setBackgroundResource(R.drawable.not_checked);

                    recyclerResult.setAdapter(relicAdapter);
                    farmViewModel.updateRelics();
                }
                if (mode == MISSION) {
                    iconMission.setBackgroundTintList(context.getColorStateList(R.color.colorAccent));
                    textMission.setTextColor(context.getColor(R.color.colorAccent));

                    iconRelic.setBackgroundTintList(context.getColorStateList(R.color.colorBackgroundDark));
                    textRelic.setTextColor(context.getColor(R.color.colorBackgroundDark));

                    textResult.setText(R.string.missions_label);

                    textCheckResult.setText(R.string.best_places);
                    if (farmViewModel.getMissionFilter().getValue())
                        iconCheckResult.setBackgroundResource(R.drawable.checked);
                    else
                        iconCheckResult.setBackgroundResource(R.drawable.not_checked);

                    recyclerResult.setAdapter(missionAdapter);
                    farmViewModel.updateMissions();
                }
            }
        });

        farmViewModel.getRelics().observe(getViewLifecycleOwner(), new Observer<List<RelicComplete>>() {
            @Override
            public void onChanged(List<RelicComplete> relics) {
                if (relics.isEmpty())
                    textEmptyStateResult.setVisibility(View.VISIBLE);
                else
                    textEmptyStateResult.setVisibility(View.GONE);

                relicAdapter.updateRelics(relics);
            }
        });

        farmViewModel.getMissions().observe(getViewLifecycleOwner(), new Observer<List<MissionComplete>>() {
            @Override
            public void onChanged(List<MissionComplete> missions) {
                if (missions.isEmpty())
                    textEmptyStateResult.setVisibility(View.VISIBLE);
                else
                    textEmptyStateResult.setVisibility(View.GONE);

                missionAdapter.updateMissions(missions);
            }
        });

        farmViewModel.getRelicFilter().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean showVaulted) {
                if (showVaulted)
                    iconCheckResult.setBackgroundResource(R.drawable.checked);
                else
                    iconCheckResult.setBackgroundResource(R.drawable.not_checked);
            }
        });

        farmViewModel.getMissionFilter().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bestMissions) {
                if (bestMissions)
                    iconCheckResult.setBackgroundResource(R.drawable.checked);
                else
                    iconCheckResult.setBackgroundResource(R.drawable.not_checked);
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

    @Override
    public void showAddPrimeDialog() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final AddPrimeDialog dialog = new AddPrimeDialog(context);
        dialog.show(fm, "Add Prime Dialog");
    }

    @Override
    public void showAddComponentDialog() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final AddComponentDialog dialog = new AddComponentDialog(context);
        dialog.show(fm, "Add Component Dialog");
    }

    @Override
    public void removeItem(Item item) {
        farmViewModel.removeItem(item);
    }
}