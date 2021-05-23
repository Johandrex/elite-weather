package com.johandrex.l6weather;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.navigation.NavigationView;

/***
 * Lab 6 - Elite Weather
 * Applikationen är en simpel väderapp som hämtar data från SMHI.se.
 * Användaren navigerar i appen genom en så kallad DrawerLayout, när användaren trycker på en sak i menyn skapas ett fragment och presenteras inför användaren.
 * I "Maps" går det att ändra position för vart vädret ska hämtas genom att användaren trycker på kartan.
 * Sedan kan användaren ändra tema, map-typ, rensa logs och inställningar i "Settings".
 *
 * Inställningarna och positionen lagras i SharedPreferences
 * När användaren ändrar inställningarna och positionen lagras detta i SQLdatabasen "LOGS" som lagrar loggar.
 *
 * @author Johannes Seldevall
 * @version 2021-02-26
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences preferences; // shared preferences
    private static final String Preferences = "Preferences"; // här lagras alla preferenser
    private static final String ColorTheme = "ColorTheme";

    /**
     * onCreate hämtar först ut temat utifrån användarens preferenser, ifall detta inte finns används det vanliga temat "Blue".
     * Sedan Skapar vi en DrawerLayout meny som användaren kan navigera i. För att slippa göra detta i alla aktiviteter har vi valt att bara ha en aktivitet som via menyn kommer åt Fragment.
     * Detta gör vi via FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); ft.replace(R.id.content_frame, fragment); och ft.commit();
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(Preferences, Context.MODE_PRIVATE); // kommer åt preferenserna
        switch (preferences.getString(ColorTheme, "Blue")) { // Sätter tema utifrån preferenserna
            case "Red":
                super.setTheme(R.style.Red);
                break;
            case "Green":
                super.setTheme(R.style.Green);
                break;
            case "Black":
                super.setTheme(R.style.Black);
                break;
            default:
                super.setTheme(R.style.Blue);;
                break;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout); // lägger in drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open_drawer, R.string.nav_close_drawer); // Strängarna är till för synskadade
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new ForecastFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment);
        ft.commit();
    }

    /**
     * Hanterar navigationen, när användaren trycker på en knapp i navigationen skapas ett nytt fragment som presenteras inför användaren
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        switch(id) {
            case R.id.nav_forecast:
                fragment = new ForecastFragment();
                break;
            case R.id.nav_map:
                fragment = new MapFragment();
                break;
            case R.id.nav_logs:
                fragment = new LogsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                break;
            case R.id.nav_exit:
                this.finishAffinity();;
                return false;
            default:
                fragment = new ForecastFragment();
                break;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Ifall användaren trycker på "Back" när drawern är öppen ska den stängas, annars öppnas drawern.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
            // super.onBackPressed();
        }
    }
}