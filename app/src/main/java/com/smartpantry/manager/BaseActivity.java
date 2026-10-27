package com.smartpantry.manager;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Shared behaviour for all screens: the yellow toolbar (with the app logo) and the
 * bottom navigation bar that links Pantry, Recipes and Settings.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private int selectedNavId = -1;

    /** Fills in the toolbar title; sub-screens also get a back arrow instead of the logo. */
    protected void setupToolbar(String title, boolean showBack) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        TextView titleView = findViewById(R.id.toolbarTitle);
        ImageView logo = findViewById(R.id.toolbarLogo);

        titleView.setText(title);
        if (showBack) {
            logo.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationContentDescription(R.string.back);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    /** Wires the bottom navigation bar; uses Intents to move between the main screens. */
    protected void setupBottomNav(int selectedId) {
        selectedNavId = selectedId;
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedNavId) return true;

            Class<?> target;
            if (id == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = SuggestedRecipesActivity.class;
            } else {
                target = SettingsActivity.class;
            }

            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            // The pantry screen is the root of the back stack, so it stays alive
            if (!(this instanceof MainActivity)) finish();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When the user comes back (Back button) re-highlight this screen's tab
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        if (nav != null && selectedNavId != -1) {
            nav.getMenu().findItem(selectedNavId).setChecked(true);
        }
    }
}
