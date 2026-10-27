package com.smartpantry.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;
import com.smartpantry.manager.util.IngredientNormalizer;
import com.smartpantry.manager.util.RecipeMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Unit tests for the strict-matching rule (run with ./gradlew test). */
public class RecipeMatcherTest {

    private Recipe recipe() {
        Recipe r = new Recipe(1, "Tomato Pasta", "", "step");
        r.addIngredient(new RecipeIngredient("pasta", 200, "g"));
        r.addIngredient(new RecipeIngredient("tomato", 4, "pcs"));
        r.addIngredient(new RecipeIngredient("onion", 1, "pcs"));
        r.addIngredient(new RecipeIngredient("garlic", 2, "pcs"));
        r.addIngredient(new RecipeIngredient("salt", 1, "tsp"));
        return r;
    }

    private PantryItem p(String n, double q, String u) { return new PantryItem(0, n, q, u, null); }

    @Test
    public void fourOfFiveIngredientsIsNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                p("Pasta", 500, "g"), p("Tomatoes", 4, "pcs"), p("Onion", 1, "pcs"), p("Garlic", 2, "pcs"));
        List<Recipe> recipes = Arrays.asList(recipe());
        assertTrue(RecipeMatcher.strictMatches(pantry, recipes, true).isEmpty());
        assertEquals(1, RecipeMatcher.almostThere(pantry, recipes, true).size());
    }

    @Test
    public void allIngredientsPresentIsSuggested_pluralsAndUnitsHandled() {
        List<PantryItem> pantry = Arrays.asList(
                p("pasta", 0.5, "kg"), p("TOMATOES", 6, "pcs"), p("onions", 2, "pcs"),
                p("garlic", 3, "pcs"), p("Salt", 1, "tbsp"));
        assertEquals(1, RecipeMatcher.strictMatches(pantry, Arrays.asList(recipe()), true).size());
    }

    @Test
    public void notEnoughQuantityIsNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                p("pasta", 100, "g"), p("tomato", 4, "pcs"), p("onion", 1, "pcs"),
                p("garlic", 2, "pcs"), p("salt", 1, "tsp"));
        assertTrue(RecipeMatcher.strictMatches(pantry, Arrays.asList(recipe()), true).isEmpty());
    }

    @Test
    public void allRecipesListShowsEverythingEvenWithEmptyPantry() {
        List<Recipe> recipes = Arrays.asList(recipe());
        List<PantryItem> empty = new ArrayList<PantryItem>();
        assertTrue(RecipeMatcher.strictMatches(empty, recipes, true).isEmpty());   // strict list stays strict
        assertEquals(1, RecipeMatcher.allWithStatus(empty, recipes, true).size()); // reference list shows it
        assertEquals(5, RecipeMatcher.allWithStatus(empty, recipes, true).get(0).getMissing().size());
    }

    @Test
    public void emptyPantryMatchesNothing() {
        assertTrue(RecipeMatcher.strictMatches(new ArrayList<PantryItem>(), Arrays.asList(recipe()), true).isEmpty());
    }

    @Test
    public void normalizerHandlesPlurals() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals("berry", IngredientNormalizer.normalize("berries"));
        assertEquals("chicken breast", IngredientNormalizer.normalize(" Chicken  Breasts "));
    }
}
