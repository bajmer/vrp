package network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Klasa implementujaca obsluge zapytan w formacie JSON
 */
class JSON {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(JSON.class);

    /**
     * Tworzy obiekt klasy
     */
    JSON() {
    }

    /**
     * Parsuje adres URL, na ktory zostanie wyslane zapytanie
     *
     * @param beginOfURL Poczatek adresu URL
     * @param srcLong    Dlugosc geograficzna adresu klienta poczatkowego
     * @param srcLat     Szerokosc geograficzna adresu klienta poczatkowego
     * @param dstLong    Dlugosc geograficzna adresu klienta docelowego
     * @param dstLat     Szerokosc geograficzna adresu klienta docelowego
     * @param endOfURL   Koncowka adresu URL
     * @return Zwraca sparsowany adres URL
     */
    String parseURL(String beginOfURL, double srcLong, double srcLat, double dstLong, double dstLat, String endOfURL) {
        return beginOfURL + srcLong + "," + srcLat + ";" + dstLong + "," + dstLat + endOfURL;
    }

    /**
     * Parsuje adres URL, na ktory zostanie wyslane zapytanie
     *
     * @param beginOfURL      Poczatek adresu URL
     * @param streetAndNumber Nazwa ulicy i numer domu
     * @param postalCode      Kod pocztowy
     * @param city            Miasto
     * @param endOfURL        Koncowka adresu URL
     * @return Zwraca sparsowany adres URL
     */
    String parseURL(String beginOfURL, String streetAndNumber, String postalCode, String city, String endOfURL) {
        streetAndNumber = streetAndNumber.replace(" ", "+");
        city = city.replace(" ", "+");
        return beginOfURL + streetAndNumber + ",%20" + postalCode + "+" + city + endOfURL;
    }

    /**
     * Wysyla zapytanie HTTP na serwer, a nastepnie pobiera odpowiedz i opakowuje ja w obiekt JSON
     *
     * @param url Adres URL, na ktory bedzie wyslane zapytanie
     * @return Zwraca obiekt JSON zawierajacy odpowiedz serwera
     * @throws ConnectException Wyjatek bledu polaczenia
     */
    JSONObject sendRequest(String url) throws ConnectException {
        logger.debug("Sending a request to URL: " + url + " ...");
        HttpURLConnection connection = null;
        String line;
        String response = "";
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(5000);
            connection.connect();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    response += line;
                }
            } else {
                logger.warn("Response error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            logger.error("Bad URL address!");
        } catch (ConnectException e) {
            logger.error("Application cannot connect to server!");
            throw e;
        } catch (IOException e) {
            logger.error("Unexpected error while sending request!");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        if (!response.equals("") && response.length() > 2) {
            if (response.startsWith("[") && response.endsWith("]")) {
                response = response.substring(1, response.length() - 1);
            }
            return new JSONObject(response);
        } else {
            return null;
        }
    }
}
