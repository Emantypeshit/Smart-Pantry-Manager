package com.smartpantry.manager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date helpers for expiry dates, which are stored as ISO text "yyyy-MM-dd".
 */
public final class DateHelper {

    private DateHelper() { }

    /** Whole days from today until the date (negative = already expired). Null if no/invalid date. */
    public static Integer daysUntil(String iso) {
        if (iso == null || iso.isEmpty()) return null;
        try {
            Date target = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diff = target.getTime() - today.getTimeInMillis();
            return (int) Math.round(diff / 86400000.0);
        } catch (ParseException e) {
            return null;
        }
    }

    /** "2026-10-23" -> "23 Oct 2026" */
    public static String pretty(String iso) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso);
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d);
        } catch (Exception e) {
            return iso;
        }
    }
}
