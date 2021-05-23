package com.johandrex.l6weather;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragmentet presenterar användaren med information om applikationen.
 */
public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    /**
     * När aktiviteten har skapats presenteras den för användaren
     */
    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.nav_about)); // titel i toolbar
    }
}