# elite-weather
Slutprojekt i kursen "Applikationsutveckling och mobilitet". Byggdes i Android Studios. Den mobila Android applikationen visar väder med data från SMHI's API.

## information

Applikationen är en simpel väderapp som hämtar data från SMHI.se.
Användaren navigerar i appen genom en så kallad DrawerLayout, när användaren trycker på en sak i menyn skapas ett fragment och presenteras inför användare. I "Maps" går det att ändra position för vart vädret ska hämtas genom att användaren trycker på kartan. Sedan kan användaren ändra tema, map-typ, rensa logs och inställningar i "Settings". Inställningarna och positionen lagras i SharedPreferences. När användaren ändrar inställningarna och positionen lagras detta i SQLdatabasen "LOGS" som lagrar loggar.

@version 2021-02-26
