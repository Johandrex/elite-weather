package com.johandrex.l6weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ForecastFragment extends Fragment {
    private SharedPreferences preferences; // shared preferences
    private static final String Preferences = "Preferences"; // här lagras alla preferenser
    private static final String Lattitude = "Lattitude";
    private static final String Longitude = "Longitude";

    private ListView datesListView;
    private ListView weatherDateListView;

    private ArrayList<String> datesList = new ArrayList<>(); // här finns alla datum som hämtas från SMHI
    private ArrayList<Weather> weatherDateList = new ArrayList<>(); // här finns väder objekt för ett specifikt datum, fylls med ny information varje gång anvädnaren trycker på ett datum (hämtas från weatherList)
    private ArrayList<Weather> weatherList = new ArrayList<>(); // här finns ALLA väder objekt som hämtas från SMHI.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    /**
     * När aktiviteten har skapats presenteras den för användaren.
     * Metoden hämtar ut positionen på kartan från SharedPreferences, hämtar informationen om positionen från SMHI och parsars sedan JSON i parseJsonData() metoden.
     */
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Activity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(getString(R.string.nav_forecast)); // titel i toolbar

        DecimalFormat df = new DecimalFormat("#.######"); // SMHI tar bara emot 6 decimaler
        preferences = activity.getSharedPreferences(Preferences, Context.MODE_PRIVATE); // kommer åt preferenserna
        float longitude = preferences.getFloat(Longitude, (float) 18.23);
        float lattitude = preferences.getFloat(Lattitude, (float) 57.62);
        String url = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/" + df.format(longitude) + "/lat/" + df.format(lattitude) + "/data.json"; // Hämta data, EX;

        datesListView = view.findViewById(R.id.dates_objects);
        weatherDateListView = view.findViewById(R.id.weather_object);

        StringRequest request = new StringRequest(url, string -> parseJsonData(string), volleyError -> {
            TextView error_message = view.findViewById(R.id.error_message);
            error_message.setText("\nError! Message from SMHI:\n'" + new String(volleyError.networkResponse.data) +"'");
        });

        RequestQueue rQueue = Volley.newRequestQueue(getContext());
        rQueue.add(request);
    }

    /**
     * Parsar JSON datan från SMHI till Weather objekt som presenteras i en ListView.
     * @param jsonString
     */
    void parseJsonData(String jsonString) {
        try {
            JSONObject json_data = new JSONObject(jsonString); // all json
            JSONArray timeSeries = json_data.getJSONArray("timeSeries"); // hämta bara datumen

            for (int i = 0; i < timeSeries.length(); ++i) {
                JSONObject date = timeSeries.getJSONObject(i); // datum
                Weather weather = new Weather(date.getString("validTime")); // skapa nytt väder objekt

                JSONArray parameters = date.getJSONArray("parameters"); // parameterna i datumet
                for (int ii = 0; ii < parameters.length(); ++ii) {
                    JSONObject parameter = parameters.getJSONObject(ii);

                    if (parameter.get("name").equals("t")) {
                        weather.setTemp(parameter.getJSONArray("values").getDouble(0));
                    } else if (parameter.get("name").equals("ws")) {
                        weather.setWs(parameter.getJSONArray("values").getDouble(0));
                    } else if (parameter.get("name").equals("Wsymb2")) {
                        weather.setDescription(parameter.getJSONArray("values").getInt(0));
                    }
                }

                weatherList.add(weather); // Lägg på nytt objekt
                if (!datesList.contains(weather.getDate())) datesList.add(weather.getDate());
            }

            setAdapters(); // sätter adapters och listeners

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sätter ListView och Listeners för listan.
     */
    public void setAdapters() {
        ArrayAdapter dates_adapter = new ArrayAdapter(getView().getContext(), android.R.layout.simple_list_item_1, datesList);
        datesListView.setAdapter(dates_adapter);

        weatherDateListView.setVisibility(View.INVISIBLE); // dölj weatherDateListView tills användaren trycker på ett datum

        // När användaren trycker på ett datum kommer vi hämta all väder-information om datumet från weatherList och lägger in det i weatherDateList
        datesListView.setOnItemClickListener((parent, view, position, id) -> {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_forecast) + " for " + datesList.get(position)); // titel i toolbar
            weatherDateList.clear();

            for (int i = 0; i < weatherList.size(); ++i) { // Hämta all information om ett specifikt datum och lägg till i weatherDateList från weatherList.
                if (weatherList.get(i).getDate().equals(datesList.get(position))) {
                    weatherDateList.add(weatherList.get(i));
                }
            }

            ArrayAdapter weather_adapter = new ArrayAdapter(getView().getContext(), android.R.layout.simple_list_item_1, weatherDateList); // skapa om listan utifrån ny data
            weatherDateListView.setAdapter(weather_adapter); // set adaptern

            weatherDateListView.setOnItemClickListener((pparent, vview, pposition, iid) -> { // när användaren trycker på något i datum listan ska den skickas tillbaka
                datesListView.setVisibility(View.VISIBLE); // visa datumen
                weatherDateListView.setVisibility(View.INVISIBLE); // dölj väder-information om ett specifikt datum
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nav_forecast)); // titel i toolbar
            });

            datesListView.setVisibility(View.INVISIBLE); // dölj datumen
            weatherDateListView.setVisibility(View.VISIBLE); // visa väder-information om ett specifikt datum
        });
    }
}
