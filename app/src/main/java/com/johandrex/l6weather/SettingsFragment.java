package com.johandrex.l6weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Låter användaren kontrollera vissa variabler i applikationen, och lagrar sedan inställningarna i SharedPreferences.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private SharedPreferences preferences; // shared preferences
    private static final String Preferences = "Preferences"; // här lagras alla preferenser
    private static final String Lattitude = "Lattitude";
    private static final String Longitude = "Longitude";
    private static final String MapTheme = "MapTheme";
    private static final String ColorTheme = "ColorTheme";

    private SQLiteOpenHelper dbHelper; // SQL helper
    private SQLiteDatabase db; // SQL databas

    private Spinner spinnerMap;
    private Spinner spinnerColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * När aktiviteten har skapats presenteras den för användaren
     * Eftersom detta är ett fragment får knapparna en setOnClickListener här, sedan skaffar vi en koppling till databasen & preferenserna.
     * När detta är gjort skapar vi en Listener på spinners, som uppdaterar inställningarna när vi ändrar Spinnern.
     */
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Activity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.nav_settings)); // titel i toolbar

        // Knappar
        Button button1 = view.findViewById(R.id.clear_logs);
        button1.setOnClickListener(v -> onClickClearLogs()); // onClickClearLOgs sker när man trycker på "clear_logs" knappen
        Button button2 = (Button) view.findViewById(R.id.default_settings);
        button2.setOnClickListener(v -> onClickDefaultSettings()); // onClickDefaultSettings sker när man trycker på "default_settings" knappen

        // Lagring (preferenser & databas)
        preferences = activity.getSharedPreferences(Preferences, Context.MODE_PRIVATE); // kommer åt preferenserna
        dbHelper = new DatabaseHelper(activity.getApplicationContext()); // databas manager
        db = dbHelper.getWritableDatabase(); // kontakt med databasen

        // Spinners
        spinnerMap = view.findViewById(R.id.map_theme);
        spinnerColor = view.findViewById(R.id.color_theme);
        selectSpinnerValue(spinnerMap, preferences.getString(MapTheme, "Normal"));
        selectSpinnerValue(spinnerColor, preferences.getString(ColorTheme, "Blue"));
        spinnerMap.setOnItemSelectedListener(this);
        spinnerColor.setOnItemSelectedListener(this);
    }

    /**
     * Sätter spinner utifrån preferenserna (ifall det ej finns används default-värdena)
     * @param spinner
     * @param myString
     */
    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Ifall en av spinnerna förändras kommer användaren informeras, och inställningarna kommer uppdateras
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!(spinnerMap.getSelectedItem().toString().equals(preferences.getString(MapTheme, "Normal"))
        && spinnerColor.getSelectedItem().toString().equals(preferences.getString(ColorTheme, "Blue")))) {
            storePosition(spinnerMap.getSelectedItem().toString(), spinnerColor.getSelectedItem().toString());
            Toast toast = Toast.makeText(this.getContext(), "Settings were updated!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /**
     * Lagrar de nya inställningarna i både databasen och sharedpreferences.
     */
    public void storePosition(String mapTheme, String colorTheme) {
        SharedPreferences.Editor editor = preferences.edit(); // uppdatera inställningar
        editor.putString(MapTheme, mapTheme);
        editor.putString(ColorTheme, colorTheme);
        editor.commit();

        DatabaseHelper.insertTransaction(db, "Settings: ColorTheme: " + colorTheme + ", MapTheme: " + mapTheme); // lagra i loggen
    }

    /**
     * Rensa alla transaktioner i databasen
     */
    public void onClickClearLogs() {
        Toast toast = Toast.makeText(this.getContext(), "Transactions cleared!", Toast.LENGTH_SHORT);
        toast.show();

        DatabaseHelper.clearTransactions(db); // lagra i loggen
        DatabaseHelper.insertTransaction(db, "Reset: Logs were cleared"); // lagra i loggen
    }

    /**
     * Sätt inställningarna till default & Starta om appen
     */
    public void onClickDefaultSettings() {
        Toast toast = Toast.makeText(this.getContext(), "Reseted settings & restarted application!", Toast.LENGTH_SHORT);
        toast.show();

        SharedPreferences.Editor editor = preferences.edit(); // uppdatera inställningar
        editor.putString(MapTheme, "Normal");
        editor.putString(ColorTheme, "Blue");
        editor.putFloat(Lattitude, (float) 57.62);
        editor.putFloat(Longitude, (float) 18.23);
        editor.commit();

        DatabaseHelper.insertTransaction(db, "Reset: Application restarted with factory settings"); // lagra i loggen

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}