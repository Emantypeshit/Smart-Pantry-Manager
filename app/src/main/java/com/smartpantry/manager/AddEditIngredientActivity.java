package com.smartpantry.manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.database.DatabaseHelper;
import com.smartpantry.manager.model.PantryItem;
import com.smartpantry.manager.util.DateHelper;
import com.smartpantry.manager.util.UnitUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Screen 2 - Add / Edit Ingredient. Used for CREATE (no extra) and UPDATE (EXTRA_ITEM_ID given).
 * Contains the input validation for the data-entry form.
 */
public class AddEditIngredientActivity extends BaseActivity {

    public static final String EXTRA_ITEM_ID = "item_id";

    private TextInputLayout tilName, tilQuantity;
    private TextInputEditText etName, etQuantity, etExpiry;
    private Spinner spUnit;

    private DatabaseHelper db;
    private long itemId = -1;          // -1 means "adding a new item"
    private String expiryIso = null;   // chosen expiry date as yyyy-MM-dd (optional)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = DatabaseHelper.getInstance(this);
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);   // receive data passed by the Intent
        setupToolbar(getString(itemId == -1 ? R.string.title_add : R.string.title_edit), true);

        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        MaterialButton btnClearDate = findViewById(R.id.btnClearDate);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UnitUtils.UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(unitAdapter);

        if (itemId != -1) {
            loadExistingItem();
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearDate.setOnClickListener(v -> {
            expiryIso = null;
            etExpiry.setText("");
        });
        btnSave.setOnClickListener(v -> saveItem());
    }

    /** Edit mode: pre-fill the form with the stored values. */
    private void loadExistingItem() {
        PantryItem item = db.getItem(itemId);
        if (item == null) {
            finish();
            return;
        }
        etName.setText(item.getName());
        etQuantity.setText(UnitUtils.formatQty(item.getQuantity()));
        for (int i = 0; i < UnitUtils.UNITS.length; i++) {
            if (UnitUtils.UNITS[i].equals(item.getUnit())) {
                spUnit.setSelection(i);
                break;
            }
        }
        expiryIso = item.getExpiryDate();
        if (expiryIso != null) {
            etExpiry.setText(DateHelper.pretty(expiryIso));
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (expiryIso != null) {
            String[] p = expiryIso.split("-");
            cal.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
        }
        new DatePickerDialog(this, (view, year, month, day) -> {
            expiryIso = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            etExpiry.setText(DateHelper.pretty(expiryIso));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /** Validates the form; shows an inline error under any bad field. */
    private boolean validate() {
        boolean ok = true;

        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_empty));
            ok = false;
        } else if (name.length() < 2 || name.length() > 40) {
            tilName.setError(getString(R.string.error_name_length));
            ok = false;
        } else if (!name.matches("[A-Za-z][A-Za-z \\-']*")) {
            tilName.setError(getString(R.string.error_name_letters));
            ok = false;
        } else {
            tilName.setError(null);
        }

        String qtyText = etQuantity.getText() == null ? "" : etQuantity.getText().toString().trim();
        if (qtyText.isEmpty()) {
            tilQuantity.setError(getString(R.string.error_qty_empty));
            ok = false;
        } else {
            try {
                double qty = Double.parseDouble(qtyText.replace(',', '.'));   // accept "1,5" too
                if (qty <= 0) {
                    tilQuantity.setError(getString(R.string.error_qty_positive));
                    ok = false;
                } else if (qty > 100000) {
                    tilQuantity.setError(getString(R.string.error_qty_large));
                    ok = false;
                } else {
                    tilQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError(getString(R.string.error_qty_number));
                ok = false;
            }
        }
        return ok;
    }

    /** CREATE a new row or UPDATE the existing one, then close the screen. */
    private void saveItem() {
        if (!validate()) return;

        String raw = etName.getText().toString().trim();
        String name = raw.substring(0, 1).toUpperCase(Locale.getDefault()) + raw.substring(1);
        double qty = Double.parseDouble(etQuantity.getText().toString().trim().replace(',', '.'));
        String unit = (String) spUnit.getSelectedItem();

        if (itemId == -1) {
            db.addItem(new PantryItem(0, name, qty, unit, expiryIso));
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        } else {
            db.updateItem(new PantryItem(itemId, name, qty, unit, expiryIso));
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
