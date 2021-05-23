package com.johandrex.l6weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Denna klassen skapar databasen när applikationen körs för första gången.
 * När databasen finns kommer transaktioner skapas när användaren trycker på "nummer" knappen och lägger in det i databasen i form av loggar.
 * När användaren trycker på "Show Logs" kommer LogsActivity.java komma åt all data i databasen som har skapats.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "logs"; // databasens namn
    private static final int DB_VERSION = 1; // databasens version

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    } // Konstruktor

    /**
     * Skapar själva databasen (table) ifall den inte redan finns
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) { // körs när databasen skapas för första gången
        db.execSQL("CREATE TABLE LOGS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT, "
                + "message TEXT);");
    }

    /**
     * Ifall vi behöver uppdatera databasen i framtiden, används för nuvarande inte
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /**
     * Skapa en ny transaktion i databasen med nummer, datum och position
     * @param message vart användaren befinner sig
     */
    public static void insertTransaction(SQLiteDatabase db, String message) {
        ContentValues transaction = new ContentValues();
        transaction.put("date", String.valueOf(Calendar.getInstance().getTime()));
        transaction.put("message", message);
        db.insert("LOGS", null, transaction);
    }

    /**
     * Radera databasen och skapa den på nytt
     */
    public static void clearTransactions(SQLiteDatabase db) {
        db.delete("LOGS", null, null);
    }
}