package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Pre-loads the 20 starter recipes the first time the database is created.
 * Ingredient format: "name:quantity:unit;name:quantity:unit". Steps are separated by "|".
 */
final class RecipeSeeder {

    private RecipeSeeder() { }

    static void seed(SQLiteDatabase db) {
        add(db, "Scrambled Eggs on Toast", "A quick, protein-rich breakfast.",
                "Whisk the eggs with the salt.|Melt the butter in a pan on medium heat.|Pour in the eggs and stir gently until just set.|Toast the bread and serve the eggs on top.",
                "egg:2:pcs;bread:2:pcs;butter:10:g;salt:1:tsp");
        add(db, "Cheese Omelette", "Fluffy omelette filled with melted cheese.",
                "Beat the eggs with the salt.|Melt the butter in a non-stick pan.|Pour in the eggs and cook until the edges set.|Add the grated cheese, fold in half and serve.",
                "egg:3:pcs;cheese:50:g;butter:10:g;salt:1:tsp");
        add(db, "Tomato Pasta", "Simple pasta in a fresh tomato and garlic sauce.",
                "Boil the pasta in salted water until al dente.|Fry the chopped onion and garlic in olive oil.|Add the chopped tomatoes and simmer for 10 minutes.|Toss the drained pasta in the sauce and serve.",
                "pasta:200:g;tomato:4:pcs;onion:1:pcs;garlic:2:pcs;olive oil:2:tbsp;salt:1:tsp");
        add(db, "Pancakes", "Classic breakfast pancakes.",
                "Whisk the flour, sugar, egg and milk into a smooth batter.|Heat a little butter in a pan.|Pour in a ladle of batter and cook until bubbles form.|Flip and cook the other side until golden.",
                "flour:200:g;milk:300:ml;egg:1:pcs;sugar:2:tbsp;butter:20:g");
        add(db, "Egg Fried Rice", "A great way to use up leftover rice.",
                "Cook the rice and let it cool.|Scramble the eggs in a hot pan with oil and set aside.|Stir-fry the diced onion and carrot.|Add the rice, eggs and soy sauce and toss until hot.",
                "rice:200:g;egg:2:pcs;onion:1:pcs;carrot:1:pcs;soy sauce:2:tbsp;oil:1:tbsp");
        add(db, "Cheese Toastie", "Crispy, gooey grilled cheese sandwich.",
                "Butter one side of each slice of bread.|Place the cheese between the unbuttered sides.|Toast in a pan until golden on both sides.",
                "bread:2:pcs;cheese:40:g;butter:10:g");
        add(db, "Banana Smoothie", "Creamy smoothie in two minutes.",
                "Peel the bananas and break them into chunks.|Blend with the milk and honey until smooth.|Pour into a glass and serve cold.",
                "banana:2:pcs;milk:250:ml;honey:1:tbsp");
        add(db, "Tuna Pasta", "Budget-friendly pantry pasta.",
                "Cook the pasta until al dente.|Fry the chopped onion in olive oil until soft.|Stir in the drained tuna and salt.|Mix with the pasta and serve.",
                "pasta:200:g;tuna:1:pcs;onion:1:pcs;olive oil:1:tbsp;salt:1:tsp");
        add(db, "Potato Soup", "Thick, comforting soup.",
                "Melt the butter and soften the chopped onion.|Add the diced potatoes and cover with water.|Simmer for 20 minutes until soft.|Mash roughly, stir in the milk and salt, and heat through.",
                "potato:4:pcs;onion:1:pcs;butter:20:g;milk:200:ml;salt:1:tsp");
        add(db, "Vegetable Stir-fry", "Colourful, quick vegetable stir-fry.",
                "Slice the carrots, onion and pepper into thin strips.|Heat the oil in a wok and add the garlic.|Stir-fry the vegetables for 5 minutes.|Add the soy sauce and toss to coat.",
                "carrot:2:pcs;onion:1:pcs;bell pepper:1:pcs;soy sauce:2:tbsp;oil:1:tbsp;garlic:2:pcs");
        add(db, "Garlic Bread", "Buttery garlic bread from the oven.",
                "Preheat the oven to 200 C.|Mash the crushed garlic into the butter.|Spread on the bread.|Bake for 8 to 10 minutes until golden.",
                "bread:4:pcs;butter:40:g;garlic:2:pcs");
        add(db, "Beans on Toast", "Warm, filling and ready in 5 minutes.",
                "Heat the baked beans in a small pot.|Toast and butter the bread.|Spoon the beans over the toast.",
                "baked beans:1:pcs;bread:2:pcs;butter:10:g");
        add(db, "Rice Pudding", "Creamy, sweet dessert.",
                "Add the rice, milk and sugar to a pot.|Simmer gently for 30 minutes, stirring often.|Serve warm.",
                "rice:100:g;milk:500:ml;sugar:3:tbsp");
        add(db, "French Toast", "Eggy bread, fried until golden.",
                "Whisk the eggs, milk and sugar in a shallow dish.|Dip each slice of bread on both sides.|Fry in butter until golden on each side.",
                "bread:4:pcs;egg:2:pcs;milk:100:ml;sugar:1:tbsp;butter:10:g");
        add(db, "Tomato Soup", "Smooth, warming tomato soup.",
                "Soften the onion and garlic in butter.|Add the chopped tomatoes and a splash of water.|Simmer for 20 minutes, season with salt.|Blend until smooth and serve.",
                "tomato:6:pcs;onion:1:pcs;garlic:2:pcs;butter:20:g;salt:1:tsp");
        add(db, "Mashed Potatoes", "Creamy mash, the perfect side.",
                "Peel, chop and boil the potatoes until soft.|Drain well and mash.|Beat in the butter, milk and salt until smooth.",
                "potato:4:pcs;butter:30:g;milk:100:ml;salt:1:tsp");
        add(db, "Cheesy Rice", "Rice baked with onion and melted cheese.",
                "Cook the rice and fry the chopped onion in butter.|Mix the rice and onion in a dish.|Top with the grated cheese.|Bake at 180 C until bubbling.",
                "rice:200:g;cheese:100:g;onion:1:pcs;butter:20:g");
        add(db, "Banana Pancakes", "Naturally sweet pancakes.",
                "Mash the bananas and mix in the eggs and flour.|Heat the butter in a pan.|Cook spoonfuls of batter until golden on both sides.",
                "banana:2:pcs;egg:2:pcs;flour:50:g;butter:10:g");
        add(db, "Lentil Curry", "Hearty, spiced lentil curry.",
                "Fry the onion and garlic in oil.|Stir in the curry powder for 1 minute.|Add the lentils, chopped tomatoes and water.|Simmer for 25 minutes until soft.",
                "lentil:200:g;onion:1:pcs;tomato:2:pcs;garlic:2:pcs;curry powder:2:tsp;oil:1:tbsp");
        add(db, "Chicken Rice Bowl", "Soy-glazed chicken over rice.",
                "Cook the rice.|Fry the sliced chicken breast in oil until golden.|Add the sliced onion and the soy sauce and cook for 5 minutes.|Serve over the rice.",
                "chicken breast:300:g;rice:200:g;onion:1:pcs;soy sauce:2:tbsp;oil:1:tbsp");
    }

    private static void add(SQLiteDatabase db, String name, String description, String steps, String ingredients) {
        ContentValues recipe = new ContentValues();
        recipe.put(DatabaseHelper.COL_NAME, name);
        recipe.put(DatabaseHelper.COL_DESCRIPTION, description);
        recipe.put(DatabaseHelper.COL_STEPS, steps.replace("|", "\n"));
        long recipeId = db.insert(DatabaseHelper.T_RECIPES, null, recipe);

        for (String line : ingredients.split(";")) {
            String[] f = line.split(":");
            ContentValues ing = new ContentValues();
            ing.put(DatabaseHelper.COL_RECIPE_ID, recipeId);
            ing.put(DatabaseHelper.COL_NAME, f[0].trim());
            ing.put(DatabaseHelper.COL_QUANTITY, Double.parseDouble(f[1]));
            ing.put(DatabaseHelper.COL_UNIT, f[2].trim());
            db.insert(DatabaseHelper.T_INGREDIENTS, null, ing);
        }
    }
}
