package com.warframefarm.activities.list.components;

import static com.warframefarm.data.WarframeConstants.BLUEPRINT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.component.ComponentFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.data.FirestoreHelper;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.databinding.RecyclerComponentBinding;

import java.util.ArrayList;
import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentViewHolder> {

    private final Context context;
    private final ComponentListener listener;
    private List<ComponentComplete> components = new ArrayList<>();
    private List<ComponentComplete> componentsAfterChanges = new ArrayList<>();

    //PAYLOAD
    private static final int OWNED = 0;

    public ComponentAdapter(Context context, ComponentListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComponentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComponentViewHolder(RecyclerComponentBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ComponentViewHolder holder, final int position) {
        ComponentComplete component = components.get(position);

        final String id = component.getId();
        holder.layoutComponent.setOnClickListener(v -> onClickComponent(id));

        //Set image for the type of prime
        holder.imageComponent.setBackgroundResource(component.isBlueprint() ? R.drawable.blueprint_bg : R.color.transparent);

        if (component.getType().equals(BLUEPRINT))
            FirestoreHelper.loadPrimeImage(component.getPrime(), context, holder.imageComponent);
        else
            holder.imageComponent.setImageResource(component.getImage());

        holder.imageVault.setVisibility(component.isVaulted() ? View.VISIBLE : View.GONE);

        //Set name and number needed
        holder.textName.setText(component.getFullName());

        //Set owned
        boolean owned;
        int component_index = componentsAfterChanges.indexOf(component);
        if (component_index != -1)
            owned = componentsAfterChanges.get(component_index).isOwned();
        else owned = component.isOwned();
        holder.imageOwned.setImageResource(owned ? R.drawable.owned : R.drawable.not_owned);

        holder.imageOwned.setOnClickListener(v -> {
            listener.modifyComponent(component.clone());
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ComponentViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.contains(OWNED)) {
            ComponentComplete component = components.get(position);

            boolean owned;
            int component_index = componentsAfterChanges.indexOf(component);
            if (component_index != -1)
                owned = componentsAfterChanges.get(component_index).isOwned();
            else owned = component.isOwned();
            holder.imageOwned.setImageResource(owned ? R.drawable.owned : R.drawable.not_owned);
        }
        else super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public void updateChangedComponents(List<ComponentComplete> componentsAfterChanges) {
        this.componentsAfterChanges = componentsAfterChanges;
        notifyItemRangeChanged(0, components.size(), OWNED);
    }

    public void updateComponents(List<ComponentComplete> components){
        this.components = components;
        notifyDataSetChanged();
    }

    private void onClickComponent(String component_id) {
        ((MainActivity) context).multipleStackNavigator.start(new ComponentFragment(component_id));
    }

    public interface ComponentListener {
        void modifyComponent(ComponentComplete component);
    }
}
