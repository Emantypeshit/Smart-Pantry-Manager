package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.model.Recipe;
import com.smartpantry.manager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local SQLite database (SQLiteOpenHelper). Holds three tables:
 *  - pantry              : what the user has at home (full CRUD from the UI)
 *  - recipes             : the seeded recipe collection (read-only for the user)
 *  - recipe_ingredients  : the ingredients of each recipe (child of recipes)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Table names
    public static final String T_PANTRY = "pantry";
    public static final String T_RECIPES = "recipes";
    public static final String T_INGREDIENTS = "recipe_ingredients";

    // Column names
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_STEPS = "steps";
    public static final String COL_RECIPE_ID = "recipe_id";

    private static DatabaseHelper instance;

    /** Singleton so the whole app shares one connection helper. */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);   // enables ON DELETE CASCADE
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_STEPS + " TEXT)");

        db.execSQL("CREATE TABLE " + T_INGREDIENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES " + T_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE)");

        RecipeSeeder.seed(db);   // pre-load the recipes on first run
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // ---------------------------------------------------------------- PANTRY: CREATE

    public long addItem(PantryItem item) {
        return getWritableDatabase().insert(T_PANTRY, null, toValues(item));
    }

    // ---------------------------------------------------------------- PANTRY: READ

    public List<PantryItem> getAllItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                items.add(readItem(c));
            }
        }
        return items;
    }

    public PantryItem getItem(long id) {
        try (Cursor c = getReadableDatabase().query(T_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readItem(c) : null;
        }
    }

    // ---------------------------------------------------------------- PANTRY: UPDATE

    public int updateItem(PantryItem item) {
        return getWritableDatabase().update(T_PANTRY, toValues(item), COL_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    // ---------------------------------------------------------------- PANTRY: DELETE

    public int deleteItem(long id) {
        return getWritableDatabase().delete(T_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearPantry() {
        getWritableDatabase().delete(T_PANTRY, null, null);
    }

    // ---------------------------------------------------------------- RECIPES

    /** Loads every recipe together with its ingredients (2 queries, no N+1 problem). */
    public List<Recipe> getAllRecipes() {
        Map<Long, Recipe> recipes = new LinkedHashMap<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor c = db.query(T_RECIPES, null, null, null, null, null, COL_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                Recipe r = readRecipe(c);
                recipes.put(r.getId(), r);
            }
        }
        try (Cursor c = db.query(T_INGREDIENTS, null, null, null, null, null, COL_ID + " ASC")) {
            while (c.moveToNext()) {
                Recipe owner = recipes.get(c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)));
                if (owner != null) owner.addIngredient(readIngredient(c));
            }
        }
        return new ArrayList<>(recipes.values());
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Recipe recipe;
        try (Cursor c = db.query(T_RECIPES, null, COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null)) {
            if (!c.moveToFirst()) return null;
            recipe = readRecipe(c);
        }
        try (Cursor c = db.query(T_INGREDIENTS, null, COL_RECIPE_ID + "=?", new String[]{String.valueOf(id)},
                null, null, COL_ID + " ASC")) {
            while (c.moveToNext()) {
                recipe.addIngredient(readIngredient(c));
            }
        }
        return recipe;
    }

    // ---------------------------------------------------------------- helpers

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, item.getName());
        v.put(COL_QUANTITY, item.getQuantity());
        v.put(COL_UNIT, item.getUnit());
        v.put(COL_EXPIRY, item.getExpiryDate());   // null is stored as NULL
        return v;
    }

    private PantryItem readItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(COL_UNIT)),
                c.getString(c.getColumnIndexOrThrow(COL_EXPIRY)));
    }

    private Recipe readRecipe(Cursor c) {
        return new Recipe(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)),
                c.getString(c.getColumnIndexOrThrow(COL_STEPS)));
    }

    private RecipeIngredient readIngredient(Cursor c) {
        return new RecipeIngredient(
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(COL_UNIT)));
    }
}
