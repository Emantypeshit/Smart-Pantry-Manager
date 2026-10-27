# Smart Pantry Manager
A Java Android app that helps reduce food waste by suggesting recipes based strictly on the ingredients
the user already has at home. A recipe is only suggested when every ingredient is in the pantry in at
least the required quantity.

## Author 
Emmanuel Phozeni

## Features
- Pantry management - add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list - RecyclerView + custom adapter, colour-coded expiry status
- 20 pre-loaded recipes - seeded into the database on first run
- Recipes screen with three clearly separate tabs: Ready to cook (strict-matching rule, only recipes you can make now), Almost there (missing exactly one ingredient) and all recipes (every recipe, with what is missing, for reference)
- Recipe detail - ingredients with tick/cross against your pantry, plus numbered method
- Settings - expiring-soon alerts toggle, ignore-expired-food toggle, clear pantry
- Friendly empty-state messages, input validation on the add/edit form
- Yellow + deep-green theme taken from the app logo


## Database choice: SQLite (SQLiteOpenHelper)
I chose local SQLite because the app only needs private, on-device data, works offline, needs no
server or account, and persists data between sessions. Tables: `pantry`, `recipes`,
`recipe_ingredients` (child table with `ON DELETE CASCADE`).

## The strict-matching rule (`util/RecipeMatcher.java`)
- Ingredient names are normalised (case, punctuation, plurals: "Tomatoes" = "tomato")
- Units are converted to a base unit (1 kg satisfies 500 g; 1 cup satisfies 100 ml)
- Several pantry rows of the same ingredient are added together
- Expired items can be ignored (Settings)
- If even one ingredient is missing or the quantity is too small, the recipe is **not** suggested

Unit tests: `app/src/test/.../RecipeMatcherTest.java` (run `./gradlew test`).

## Setup / run
1. Install Android Studio (Hedgehog or newer) with Android SDK 34.
2. `File > Open` and select the `SmartPantryManager` folder; wait for Gradle sync.
3. Create an emulator (API 24+) or plug in a phone with USB debugging.
4. Press **Run**.

## Project structure
app/src/main/java/com/smartpantry/manager/
  MainActivity                 pantry list (screen 1)
  AddEditIngredientActivity    add / edit form with validation (screen 2)
  SuggestedRecipesActivity     strict matching results (screen 3)
  RecipeDetailActivity         recipe details (screen 4)
  SettingsActivity             settings (screen 5)
  BaseActivity                 toolbar + bottom navigation
  adapter/   PantryAdapter, RecipeAdapter
  database/  DatabaseHelper, RecipeSeeder
  model/     PantryItem, Recipe, RecipeIngredient, RecipeMatch
  util/      RecipeMatcher, IngredientNormalizer, UnitUtils, DateHelper, Prefs

