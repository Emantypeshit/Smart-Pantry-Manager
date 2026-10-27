package com.smartpantry.manager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe with its required ingredients and preparation steps.
 */
public class Recipe {
    private final long id;
    private final String name;
    private final String description;
    /** Steps separated by new-line characters. */
    private final String steps;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(long id, String name, String description, String steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.steps = steps;
    }

    public void addIngredient(RecipeIngredient ingredient) { ingredients.add(ingredient); }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSteps() { return steps; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
