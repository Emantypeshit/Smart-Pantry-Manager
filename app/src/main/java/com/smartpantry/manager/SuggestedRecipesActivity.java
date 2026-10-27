package com.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.smartpantry.manager.adapter.RecipeAdapter;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeMatch;
import com.smartpantry.manager.util.Prefs;
import com.smartpantry.manager.util.RecipeMatcher;

import java.util.List;

/**
 * Screen 3 - Suggested Recipes. Runs the strict-matching rule against the pantry.
 * The optional "Almost there" tab (missing exactly one ingredient) and the "All recipes"
 * reference tab (every recipe, so the user can browse meals) are kept visibly separate
 * from the strict "Ready to cook" list.
 */
public class SuggestedRecipesActivity extends BaseActivity implements RecipeAdapter.Listener {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView emptyView, summary;
    private int mode = RecipeAdapter.MODE_READY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setupToolbar(getString(R.string.title_recipes), false);
        setupBottomNav(R.id.nav_recipes);

        db = DatabaseHelper.getInstance(this);
        emptyView = findViewById(R.id.emptyView);
        summary = findViewById(R.id.summary);

        RecyclerView recycler = findViewById(R.id.recipeRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);

        MaterialButtonToggleGroup toggle = findViewById(R.id.toggleGroup);
        toggle.check(R.id.btnReady);
        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnAlmost) {
                    mode = RecipeAdapter.MODE_ALMOST;
                } else if (checkedId == R.id.btnAll) {
                    mode = RecipeAdapter.MODE_ALL;
                } else {
                    mode = RecipeAdapter.MODE_READY;
                }
                refresh();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /** Re-runs the matching algorithm with the current pantry and settings. */
    private void refresh() {
        List<PantryItem> pantry = db.getAllItems();
        List<Recipe> recipes = db.getAllRecipes();
        boolean excludeExpired = Prefs.isExcludeExpired(this);

        List<RecipeMatch> list;
        if (mode == RecipeAdapter.MODE_ALL) {
            list = RecipeMatcher.allWithStatus(pantry, recipes, excludeExpired);
        } else if (mode == RecipeAdapter.MODE_ALMOST) {
            list = RecipeMatcher.almostThere(pantry, recipes, excludeExpired);
        } else {
            list = RecipeMatcher.strictMatches(pantry, recipes, excludeExpired);   // the strict rule
        }

        adapter.setMatches(list, mode);
        summary.setText(getString(R.string.summary_count, list.size(), recipes.size()));

        // Friendly feedback instead of a blank screen when nothing matches
        emptyView.setText(mode == RecipeAdapter.MODE_ALMOST ? R.string.empty_almost
                : mode == RecipeAdapter.MODE_ALL ? R.string.empty_all : R.string.empty_suggestions);
        emptyView.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
