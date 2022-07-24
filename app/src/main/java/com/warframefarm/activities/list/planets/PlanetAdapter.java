package com.warframefarm.activities.list.planets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.activities.details.planet.PlanetFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.Planet;
import com.warframefarm.databinding.RecyclerPlanetBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlanetAdapter extends RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder> {

    private final Context context;
    private List<Planet> planets = new ArrayList<>();

    public PlanetAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public PlanetViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerPlanetBinding binding = RecyclerPlanetBinding.inflate(inflater, parent, false);
        return new PlanetViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlanetViewHolder holder, int position) {
        Planet planet = planets.get(position);
        String name = planet.getName();

        FirestoreHelper.loadPlanetSquareImage(name, context, holder.imagePlanet);

        holder.textPlanetName.setText(name);

        holder.imagePlanetFaction.setImageResource(planet.getImageFaction());

        holder.layoutPlanet.setOnClickListener(v -> showPlanetDetails(planet.getName()));
    }

    @Override
    public int getItemCount() {
        return planets.size();
    }

    public void updatePlanets(List<Planet> planets) {
        this.planets = planets;
        notifyDataSetChanged();
    }

    public void showPlanetDetails(String planet) {
        ((MainActivity) context).multipleStackNavigator.start(new PlanetFragment(planet));
    }

    public static class PlanetViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutPlanet;
        ImageView imagePlanet, imagePlanetFaction;
        TextView textPlanetName;

        public PlanetViewHolder(@NonNull @NotNull RecyclerPlanetBinding binding) {
            super(binding.getRoot());
            layoutPlanet = binding.layoutPlanet;
            imagePlanet = binding.imagePlanet;
            imagePlanetFaction = binding.imagePlanetFaction;
            textPlanetName = binding.textPlanetName;
        }
    }
}
