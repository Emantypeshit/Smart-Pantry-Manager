package com.smartpantry.manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter for the Suggested Recipes list.
 * Used for both lists: "Ready to cook" (strict) and "Almost there" (bonus).
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<RecipeMatch> matches = new ArrayList<>();
    private final Listener listener;
    public static final int MODE_READY = 0;   // strict matches
    public static final int MODE_ALMOST = 1;  // missing exactly one ingredient
    public static final int MODE_ALL = 2;     // every recipe, for reference
    private int mode = MODE_READY;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setMatches(List<RecipeMatch> newMatches, int mode) {
        this.mode = mode;
        matches.clear();
        matches.addAll(newMatches);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        RecipeMatch match = matches.get(position);
        Recipe recipe = match.getRecipe();
        int count = recipe.getIngredients().size();

        h.name.setText(recipe.getName());
        h.info.setText(h.itemView.getContext().getResources().getQuantityString(
                R.plurals.ingredient_count, count, count));

        Context ctx = h.itemView.getContext();
        int missingCount = match.getMissing().size();
        if (missingCount == 0) {
            h.status.setText(R.string.you_have_everything);
            h.status.setTextColor(ContextCompat.getColor(ctx, R.color.green));
        } else if (mode == MODE_ALMOST || missingCount == 1) {
            // Names the one missing ingredient
            h.status.setText(ctx.getString(R.string.missing_label, match.getMissing().get(0)));
            h.status.setTextColor(ContextCompat.getColor(ctx, R.color.orange));
        } else {
            h.status.setText(ctx.getResources().getQuantityString(
                    R.plurals.missing_count, missingCount, missingCount));
            h.status.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
        }
        h.itemView.setOnClickListener(v -> listener.onRecipeClicked(recipe));
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, info, status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipeName);
            info = itemView.findViewById(R.id.recipeInfo);
            status = itemView.findViewById(R.id.recipeStatus);
        }
    }
}
