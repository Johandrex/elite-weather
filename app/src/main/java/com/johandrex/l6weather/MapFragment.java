package com.johandrex.l6weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Detta fragment visar användaren en karta som går att interagera med,
 * Tanken är att användaren ska trycka på kartan för att välja nya positioner som applikationen använder för att hämta väderdata.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private SharedPreferences preferences; // shared preferences
    private static final String Preferences = "Preferences"; // här lagras alla preferenser
    private static final String Lattitude = "Lattitude";
    private static final String Longitude = "Longitude";
    private static final String MapTheme = "MapTheme";

    private GoogleMap gmap; // map

    private SQLiteOpenHelper dbHelper; // SQL helper
    private SQLiteDatabase db; // SQL databas

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * När aktiviteten har skapats presenteras den för användaren
     * Metoden initierar databas kopplingen och hämtar preferenserna, sedan skapas mappen via onMapRead().
     */
    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.nav_map)); // titel i toolbar

        // Lagring (preferenser & databas)
        preferences = activity.getSharedPreferences(Preferences, Context.MODE_PRIVATE); // kommer åt preferenserna
        dbHelper = new DatabaseHelper(activity.getApplicationContext()); // databas manager
        db = dbHelper.getWritableDatabase(); // kontakt med databasen

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map); // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment.getMapAsync(this);
    }

    /**
     * När mappen är tillgänglig läggs den in på valda positioner.
     * Ifall användaren inte tidigare har valt en position kommer positionen 57.62, 18.23 att användas (VISBY).
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        // Hämta från sharedpreferences, ifall det inte finns någon data används Visby's positioner by default
        float lattitude = preferences.getFloat(Lattitude, (float) 57.62);
        float longitude = preferences.getFloat(Longitude, (float) 18.23);

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(lattitude, longitude); // visby
        gmap.addMarker(new MarkerOptions().position(position).title(position.latitude + " : " + position.longitude)); // lägger på markören och titeln blir positionen (lattitude+longitude);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(position));

        switch (preferences.getString(MapTheme, "Normal")) { // Sätter typ utifrån preferenserna // Normal (1), Satellite (2), Terrain (3), Hybrid (4)
            case "Satellite":
                gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "Terrain":
                gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "Hybrid":
                gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }

        gmap.setOnMapClickListener(this);
    }


    /**
     * När användaren trycker på mappen kommer en ny markör sättas, och positionen uppdateras.
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions(); // Skapa markör
        markerOptions.position(latLng); // Sätter position på markör
        markerOptions.title(latLng.latitude + " : " + latLng.longitude); // Titeln på markören blir positionen (latitude+longitude)

        gmap.clear(); // Rensa övriga markörer
        gmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        gmap.addMarker(markerOptions); // placerar markören på kartan

        storePosition(latLng);
    }

    /**
     * Lagrar den nya positionen i både databasen och sharedpreferences.
     */
    public void storePosition(LatLng latLng) {
        SharedPreferences.Editor editor = preferences.edit(); // uppdatera position
        editor.putFloat(Lattitude, (float) latLng.latitude);
        editor.putFloat(Longitude, (float) latLng.longitude);
        editor.commit();

        DatabaseHelper.insertTransaction(db, "Position: " + latLng.latitude + ", " + latLng.longitude); // lagra i loggen
    }
}