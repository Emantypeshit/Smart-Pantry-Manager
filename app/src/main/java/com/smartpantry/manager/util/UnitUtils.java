package com.smartpantry.manager.util;

import java.util.Locale;

/**
 * Helpers for units of measure. Quantities are converted to a common base unit
 * (grams, millilitres or pieces) so that "1 kg" in the pantry satisfies "500 g" in a recipe.
 */
public final class UnitUtils {

    /** Units offered in the Add/Edit spinner. */
    public static final String[] UNITS = {"g", "kg", "ml", "l", "cup", "tbsp", "tsp", "pcs"};

    public static final int MASS = 0;
    public static final int VOLUME = 1;
    public static final int COUNT = 2;

    private UnitUtils() { }

    /** Which kind of quantity a unit measures (mass, volume or a simple count). */
    public static int dimension(String unit) {
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "g":
            case "kg":
                return MASS;
            case "ml":
            case "l":
            case "cup":
            case "tbsp":
            case "tsp":
                return VOLUME;
            default:
                return COUNT;
        }
    }

    /** Converts a quantity to its base unit: grams, millilitres or pieces. */
    public static double toBase(double quantity, String unit) {
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "kg": return quantity * 1000;
            case "l": return quantity * 1000;
            case "cup": return quantity * 240;
            case "tbsp": return quantity * 15;
            case "tsp": return quantity * 5;
            default: return quantity;
        }
    }

    /** Formats 2.0 as "2" and 0.50 as "0.5". */
    public static String formatQty(double q) {
        if (Math.abs(q - Math.rint(q)) < 1e-9) {
            return String.valueOf((long) Math.rint(q));
        }
        return String.format(Locale.US, "%.2f", q).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
