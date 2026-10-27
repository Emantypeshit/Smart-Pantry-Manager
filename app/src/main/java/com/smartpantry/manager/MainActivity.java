package com.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartpantry.manager.adapter.PantryAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.util.DateHelper;
import com.smartpantry.manager.util.Prefs;

import java.util.List;

/**
 * Screen 1 - Pantry List. READ (list) + entry points for CREATE, UPDATE and DELETE.
 */
public class MainActivity extends BaseActivity implements PantryAdapter.Listener {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView emptyView;
    private TextView expiryBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar(getString(R.string.app_name), false);
        setupBottomNav(R.id.nav_pantry);

        db = DatabaseHelper.getInstance(this);
        emptyView = findViewById(R.id.emptyView);
        expiryBanner = findViewById(R.id.expiryBanner);

        RecyclerView recycler = findViewById(R.id.pantryRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recycler.setAdapter(adapter);

        // CREATE: explicit Intent to the Add/Edit screen (no extras = add mode)
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();   // refresh whenever we return from Add/Edit or Settings
    }

    /** Reads all rows from SQLite and shows them (or the empty-state message). */
    private void loadPantry() {
        List<PantryItem> items = db.getAllItems();
        adapter.setItems(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        updateExpiryBanner(items);
    }

    /** Settings toggle: warn about items that are expired or expire within 3 days. */
    private void updateExpiryBanner(List<PantryItem> items) {
        if (!Prefs.isExpiryAlertsEnabled(this)) {
            expiryBanner.setVisibility(View.GONE);
            return;
        }
        int count = 0;
        for (PantryItem item : items) {
            Integer days = DateHelper.daysUntil(item.getExpiryDate());
            if (days != null && days <= 3) count++;
        }
        if (count > 0) {
            expiryBanner.setText(getResources().getQuantityString(R.plurals.expiring_alert, count, count));
            expiryBanner.setVisibility(View.VISIBLE);
        } else {
            expiryBanner.setVisibility(View.GONE);
        }
    }

    // UPDATE: pass the row id to the Add/Edit screen through an Intent extra
    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    // DELETE: ask for confirmation first, then remove the row
    @Override
    public void onDelete(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.deleteItem(item.getId());
                    Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
                    loadPantry();
                })
                .show();
    }
}
