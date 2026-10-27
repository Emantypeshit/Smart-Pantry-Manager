package com.smartpantry.manager;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.util.Prefs;
import com.smartpantry.manager.util.RecipeMatcher;
import com.smartpantry.manager.util.UnitUtils;

import java.util.List;
import java.util.Map;

/**
 * Screen 4 - Recipe Detail: full ingredient list (with a tick/cross showing what the
 * pantry covers) and the numbered method.
 */
public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setupToolbar(getString(R.string.title_detail), true);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Recipe recipe = db.getRecipe(getIntent().getLongExtra(EXTRA_RECIPE_ID, -1));
        if (recipe == null) {
            Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.detailName)).setText(recipe.getName());
        ((TextView) findViewById(R.id.detailDescription)).setText(recipe.getDescription());

        // Ingredient list: green tick = in pantry, red cross = missing
        List<PantryItem> pantry = db.getAllItems();
        Map<String, Double> available = RecipeMatcher.buildAvailability(pantry, Prefs.isExcludeExpired(this));
        SpannableStringBuilder ingredients = new SpannableStringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            boolean have = RecipeMatcher.hasIngredient(available, ing);
            int start = ingredients.length();
            ingredients.append(have ? "\u2713  " : "\u2717  ");
            ingredients.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,
                            have ? R.color.green : R.color.tomato)),
                    start, ingredients.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ingredients.append(UnitUtils.formatQty(ing.getQuantity())).append(' ')
                    .append(ing.getUnit()).append(' ').append(ing.getName()).append('\n');
        }
        ((TextView) findViewById(R.id.detailIngredients)).setText(ingredients);

        // Method: number each line of the stored steps
        StringBuilder method = new StringBuilder();
        String[] steps = recipe.getSteps().split("\n");
        for (int i = 0; i < steps.length; i++) {
            method.append(i + 1).append(". ").append(steps[i]).append("\n\n");
        }
        ((TextView) findViewById(R.id.detailSteps)).setText(method.toString().trim());
    }
}
