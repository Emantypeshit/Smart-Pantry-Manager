package com.smartpantry.manager.util;

import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.model.RecipeMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE CORE BUSINESS LOGIC OF THE APP: the strict-matching rule.
 *
 * A recipe is "suggested" only if EVERY ingredient it needs is in the pantry in at least
 * the required quantity. One missing ingredient (or too little of one) rules the recipe out.
 *
 * To cope with real-world messiness the matcher:
 *  - normalises names ("Tomatoes" == "tomato"),
 *  - converts units to a base unit (1 kg satisfies 500 g, 1 cup satisfies 100 ml),
 *  - adds up several pantry rows of the same ingredient (e.g. two packets of rice).
 * Units of a different kind (grams vs. pieces) are never mixed, so the rule stays strict.
 */
public final class RecipeMatcher {

    private static final double EPSILON = 1e-9;

    private RecipeMatcher() { }

    /** Recipes the user can cook right now: nothing missing at all. */
    public static List<RecipeMatch> strictMatches(List<PantryItem> pantry, List<Recipe> recipes,
                                                  boolean excludeExpired) {
        return collect(pantry, recipes, excludeExpired, 0);
    }

    /** Optional bonus list: recipes missing exactly ONE ingredient (kept separate from strict list). */
    public static List<RecipeMatch> almostThere(List<PantryItem> pantry, List<Recipe> recipes,
                                                boolean excludeExpired) {
        return collect(pantry, recipes, excludeExpired, 1);
    }

    /**
     * Reference list: EVERY recipe, whether or not the pantry covers it. Each result still carries
     * its missing ingredients. Sorted so cookable recipes come first, then those missing the fewest.
     * (The strict "suggested" list above is unaffected by this method.)
     */
    public static List<RecipeMatch> allWithStatus(List<PantryItem> pantry, List<Recipe> recipes,
                                                  boolean excludeExpired) {
        Map<String, Double> available = buildAvailability(pantry, excludeExpired);
        List<RecipeMatch> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            result.add(evaluate(recipe, available));
        }
        Collections.sort(result, new Comparator<RecipeMatch>() {
            @Override
            public int compare(RecipeMatch a, RecipeMatch b) {
                int byMissing = Integer.compare(a.getMissing().size(), b.getMissing().size());
                return byMissing != 0 ? byMissing
                        : a.getRecipe().getName().compareToIgnoreCase(b.getRecipe().getName());
            }
        });
        return result;
    }

    private static List<RecipeMatch> collect(List<PantryItem> pantry, List<Recipe> recipes,
                                             boolean excludeExpired, int missingCount) {
        Map<String, Double> available = buildAvailability(pantry, excludeExpired);
        List<RecipeMatch> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            RecipeMatch match = evaluate(recipe, available);
            if (match.getMissing().size() == missingCount && !recipe.getIngredients().isEmpty()) {
                result.add(match);
            }
        }
        return result;
    }

    /** Totals the pantry per (normalised name + kind of unit), in base units. */
    public static Map<String, Double> buildAvailability(List<PantryItem> pantry, boolean excludeExpired) {
        Map<String, Double> available = new HashMap<>();
        for (PantryItem item : pantry) {
            if (excludeExpired) {
                Integer days = DateHelper.daysUntil(item.getExpiryDate());
                if (days != null && days < 0) continue;   // expired food does not count
            }
            String key = key(item.getName(), item.getUnit());
            double qty = UnitUtils.toBase(item.getQuantity(), item.getUnit());
            Double existing = available.get(key);
            available.put(key, (existing == null ? 0 : existing) + qty);
        }
        return available;
    }

    /** Tests one recipe against the pantry totals. */
    public static RecipeMatch evaluate(Recipe recipe, Map<String, Double> available) {
        List<String> missing = new ArrayList<>();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            if (!hasIngredient(available, ing)) {
                missing.add(UnitUtils.formatQty(ing.getQuantity()) + " " + ing.getUnit() + " " + ing.getName());
            }
        }
        return new RecipeMatch(recipe, missing);
    }

    /** True when the pantry holds at least the quantity this recipe line needs. */
    public static boolean hasIngredient(Map<String, Double> available, RecipeIngredient ing) {
        Double have = available.get(key(ing.getName(), ing.getUnit()));
        double need = UnitUtils.toBase(ing.getQuantity(), ing.getUnit());
        return have != null && have + EPSILON >= need;
    }

    private static String key(String name, String unit) {
        return IngredientNormalizer.normalize(name) + "|" + UnitUtils.dimension(unit);
    }
}
