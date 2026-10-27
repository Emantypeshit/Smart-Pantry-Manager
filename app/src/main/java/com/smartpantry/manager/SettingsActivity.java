package com.smartpantry.manager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.util.Prefs;

/**
 * Screen 5 - Settings: expiring-soon alerts toggle, "ignore expired food" toggle
 * (both saved with SharedPreferences) and a button to clear the pantry.
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar(getString(R.string.title_settings), false);
        setupBottomNav(R.id.nav_settings);

        SwitchMaterial swAlerts = findViewById(R.id.swAlerts);
        SwitchMaterial swExpired = findViewById(R.id.swExcludeExpired);
        MaterialButton btnClear = findViewById(R.id.btnClearPantry);

        swAlerts.setChecked(Prefs.isExpiryAlertsEnabled(this));
        swExpired.setChecked(Prefs.isExcludeExpired(this));

        swAlerts.setOnCheckedChangeListener((button, checked) -> Prefs.setExpiryAlertsEnabled(this, checked));
        swExpired.setOnCheckedChangeListener((button, checked) -> Prefs.setExcludeExpired(this, checked));

        btnClear.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clear_title)
                .setMessage(R.string.clear_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.clear_confirm, (dialog, which) -> {
                    DatabaseHelper.getInstance(this).clearPantry();
                    Toast.makeText(this, R.string.pantry_cleared, Toast.LENGTH_SHORT).show();
                })
                .show());
    }
}
