package com.smartpantry.manager.model;

/**
 * A single ingredient the user currently has at home (one row of the "pantry" table).
 */
public class PantryItem {
    private final long id;
    private final String name;
    private final double quantity;
    private final String unit;
    /** Expiry date as ISO text "yyyy-MM-dd", or null when the user did not enter one. */
    private final String expiryDate;

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiryDate() { return expiryDate; }
}
