package com.smartpantry.manager.model;

import java.util.List;

/**
 * Result of testing one recipe against the pantry: the recipe plus the ingredients
 * that are missing (or not available in a large enough quantity).
 * An empty "missing" list means the recipe passes the strict-matching rule.
 */
public class RecipeMatch {
    private final Recipe recipe;
    private final List<String> missing;

    public RecipeMatch(Recipe recipe, List<String> missing) {
        this.recipe = recipe;
        this.missing = missing;
    }

    public Recipe getRecipe() { return recipe; }
    public List<String> getMissing() { return missing; }
    public boolean isComplete() { return missing.isEmpty(); }
}
