package com.johandrex.l6weather;

/**
 * Väder objekt skapas utifrån information som hämtas från SMHI
 * Klassen används endast i ForecastFragment och lagras inte någonstans eftersom informationen hämtas i realtid.
 */
public class Weather {
    private String date;
    private String time;

    private double temp; // t
    private double ws; // ws (wind per second
    private int description; // Wsymb2

    /**
     * Konstruktor
     * @param datetime tid
     */
    public Weather(String datetime) {
        this.date = datetime.substring(0,10);
        this.time = datetime.substring(11,16);
    }

    // Setters
    public void setTemp(double temp) { this.temp = temp; }
    public void setWs(double ws) { this.ws = ws; }
    public void setDescription(int description) { this.description = description; }

    /**
     * Hämta informationen om ett väderobjekt
     * @return
     */
    @Override
    public String toString() {
        return "\nTime: " + time +
                "\nTemperature: " + temp + " °C" +
                "\nWind speed: " + ws + " m/s" +
                "\nDescription: " + getDescription() + "\n";
    }

    /**
     * Hämta datum
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Vilken typ av beskrivning det är, från https://opendata.smhi.se/apidocs/metfcst/parameters.html#parameter-wsymb
     * @return beskrivning av vädertyp
     */
    public String getDescription() {
        switch (description) {
            case 1:
                return "Clear sky";
            case 2:
                return "Nearly clear sky";
            case 3:
                return "Variable cloudiness";
            case 4:
                return "Halfclear sky";
            case 5:
                return "Cloudy sky";
            case 6:
                return "Overcast";
            case 7:
                return "Fog";
            case 8:
                return "Light rain showers";
            case 9:
                return "Moderate rain showers";
            case 10:
                return "Heavy rain showers";
            case 11:
                return "Thunderstorm";
            case 12:
                return "Light sleet showers";
            case 13:
                return "Moderate sleet showers";
            case 14:
                return "Heavy sleet showers";
            case 15:
                return "Light snow showers";
            case 16:
                return "Moderate snow showers";
            case 17:
                return "Heavy snow showers";
            case 18:
                return "Light rain";
            case 19:
                return "Moderate rain";
            case 20:
                return "Heavy rain";
            case 21:
                return "Thunder";
            case 22:
                return "Light sleet";
            case 23:
                return "Moderate sleet";
            case 24:
                return "Heavy sleet";
            case 25:
                return "Light snowfall";
            case 26:
                return "Moderate snowfall";
            case 27:
                return "Heavy snowfall";
            default:
                return "No description";
        }
    }
}
