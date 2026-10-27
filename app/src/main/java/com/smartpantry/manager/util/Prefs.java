package com.smartpantry.manager.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Thin wrapper around SharedPreferences for the Settings screen.
 */
public final class Prefs {
    private static final String FILE = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_EXCLUDE_EXPIRED = "exclude_expired";

    private Prefs() { }

    private static SharedPreferences get(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean isExpiryAlertsEnabled(Context c) {
        return get(c).getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public static void setExpiryAlertsEnabled(Context c, boolean value) {
        get(c).edit().putBoolean(KEY_EXPIRY_ALERTS, value).apply();
    }

    /** When true, expired pantry items are ignored by the recipe matcher. */
    public static boolean isExcludeExpired(Context c) {
        return get(c).getBoolean(KEY_EXCLUDE_EXPIRED, true);
    }

    public static void setExcludeExpired(Context c, boolean value) {
        get(c).edit().putBoolean(KEY_EXCLUDE_EXPIRED, value).apply();
    }
}
