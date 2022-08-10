package com.warframefarm.activities.list.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warframefarm.R;
import com.warframefarm.activities.details.component.ComponentFragment;
import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.database.ComponentComplete;
import com.warframefarm.databinding.RecyclerComponentBinding;

import java.util.List;

public class ComponentDialogAdapter extends RecyclerView.Adapter<ComponentViewHolder> {

    private final Context context;

    private List<ComponentComplete> componentsAfterChanges;

    public ComponentDialogAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ComponentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComponentViewHolder(RecyclerComponentBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ComponentViewHolder holder, final int position) {
        ComponentComplete component = componentsAfterChanges.get(position);

        final String id = component.getId();
        holder.layoutComponent.setOnClickListener(v -> onClickComponent(id));

        //Set image for the component
        component.displayImage(context, holder.imageComponent);

        //Set name and number needed
        holder.textName.setText(component.getFullName());

        //Owned
        holder.imageOwned.setImageResource(component.isOwned() ? R.drawable.owned : R.drawable.not_owned);
    }

    @Override
    public int getItemCount() {
        return componentsAfterChanges.size();
    }

    public void updateComponents(List<ComponentComplete> componentsAfterChanges){
        this.componentsAfterChanges = componentsAfterChanges;
        notifyDataSetChanged();
    }

    private void onClickComponent(String component_id) {
        ((MainActivity) context).multipleStackNavigator.start(new ComponentFragment(component_id));
    }
}
