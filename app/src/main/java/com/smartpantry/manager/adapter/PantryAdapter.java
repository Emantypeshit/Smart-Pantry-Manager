package com.smartpantry.manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.util.DateHelper;
import com.smartpantry.manager.util.UnitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter that binds pantry rows from SQLite to item_pantry.xml.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Callback so the Activity (not the adapter) decides what edit/delete do. */
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PantryItem item = items.get(position);
        Context ctx = h.itemView.getContext();

        h.name.setText(item.getName());
        h.quantity.setText(UnitUtils.formatQty(item.getQuantity()) + " " + item.getUnit());

        // Expiry line, colour-coded: red = expired, orange = soon, green = fine, grey = none
        Integer days = DateHelper.daysUntil(item.getExpiryDate());
        if (days == null) {
            h.expiry.setText(R.string.no_expiry);
            h.expiry.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
        } else if (days < 0) {
            h.expiry.setText(ctx.getString(R.string.expired_days_ago, -days));
            h.expiry.setTextColor(ContextCompat.getColor(ctx, R.color.tomato));
        } else if (days == 0) {
            h.expiry.setText(R.string.expires_today);
            h.expiry.setTextColor(ContextCompat.getColor(ctx, R.color.orange));
        } else if (days <= 3) {
            h.expiry.setText(ctx.getString(R.string.expires_in_days, days));
            h.expiry.setTextColor(ContextCompat.getColor(ctx, R.color.orange));
        } else {
            h.expiry.setText(ctx.getString(R.string.expires_on, DateHelper.pretty(item.getExpiryDate())));
            h.expiry.setTextColor(ContextCompat.getColor(ctx, R.color.green));
        }

        h.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(item));
        h.itemView.setOnClickListener(v -> listener.onEdit(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final ImageButton btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.itemQty);
            expiry = itemView.findViewById(R.id.itemExpiry);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
