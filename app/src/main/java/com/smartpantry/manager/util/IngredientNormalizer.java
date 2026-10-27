package com.smartpantry.manager.util;

import java.util.Locale;

/**
 * Makes ingredient names comparable: lower-case, no punctuation, and a simple
 * singular form, so "Tomatoes", "tomato " and "TOMATO" all become "tomato".
 */
public final class IngredientNormalizer {

    private IngredientNormalizer() { }

    public static String normalize(String name) {
        if (name == null) return "";
        String s = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (s.isEmpty()) return s;

        // Only the last word carries the plural: "chicken breasts" -> "chicken breast"
        int space = s.lastIndexOf(' ');
        String head = space >= 0 ? s.substring(0, space + 1) : "";
        String last = s.substring(space + 1);
        return head + singular(last);
    }

    /** Very small rule-based singulariser (enough for pantry words, no NLP needed). */
    static String singular(String w) {
        if (w.length() <= 3) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";          // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);                // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes") || w.endsWith("sses")) {
            return w.substring(0, w.length() - 2);                                    // peaches -> peach
        }
        if (w.endsWith("ss") || w.endsWith("us") || w.endsWith("is")) return w;       // hummus, swiss
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);                  // eggs -> egg
        return w;
    }
}
