package com.johandrex.l6weather;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Klassen visar upp alla loggar från SQLdatabasen "LOGS",
 * Den senaste loggningen visas först och presenteras i en vy som användaren kan scrolla i.
 */
public class LogsFragment extends Fragment {
    private SQLiteOpenHelper dbHelper; // SQL helper
    private SQLiteDatabase db; // SQL databas

    private TextView text; // Text komponent

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logs, container, false);
    }

    /**
     * När aktiviteten har skapats presenteras den för användaren
     * Metoden hämtar ut data via SELECT och lägger sedan varje rad i en StringBuffer som presenteras för användaren
     */
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Activity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.nav_logs)); // titel i toolbar

        text = view.findViewById(R.id.logs); // hitta komponent
        dbHelper = new DatabaseHelper(activity.getApplicationContext()); // databas manager
        db = dbHelper.getWritableDatabase(); // kontakt med databasen
        Cursor cursor = db.rawQuery("SELECT * FROM LOGS ORDER BY _id DESC", null); // hämta all data från PLATESPOTTING till en cursor

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) { // Loopa igenom alla rader i databasen och lägg i StringBuffern
            buffer.append("ID: " + cursor.getString(0)+"\n");
            buffer.append("Date: " + cursor.getString(1)+"\n");
            buffer.append(cursor.getString(2)+"\n\n");
        }

        text.setText(buffer); // TextView komponent visar upp informationen vi hämtat från databasen
        db.close(); // stäng kopplingen till databasen
    }
}