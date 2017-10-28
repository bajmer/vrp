package core;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa reprezentujaca klienta lub magazyn
 */
public class Customer {

    /**
     * Czas obslugi klienta w minutach
     */
    private static final Duration serviceTime = Duration.ofMinutes(10);

    /**
     * Numer ID
     */
    private static int customerID;

    /**
     * Numer ID na mapie
     */
    private static int customerMapID = 0;

    /**
     * Numer ID klienta
     */
    private int id;

    /**
     * Numer ID klienta na mapie
     */
    private int mapId;

    /**
     * Pelny adres
     */
    private String fullAddress;

    /**
     * Nazwa ulicy i numer domu
     */
    private String streetAndNumber;

    /**
     * Kod pocztowy
     */
    private String postalCode;

    /**
     * Miasto
     */
    private String city;

    /**
     * Szerokosc geograficzna adresu
     */
    private double latitude;

    /**
     * Dlugosc geograficzna adresu
     */
    private double longitude;

    /**
     * Masa zamowionej przesylki
     */
    private double packageWeight;

    /**
     * Objetosc zamowionej przesylki
     */
    private double packageSize;

    /**
     * Najwczesniejsza mozliwa godzina odbioru przesylki
     */
    private LocalTime minDeliveryHour;

    /**
     * Najpozniejsza mozliwa godzina odbioru przesylki
     */
    private LocalTime maxDeliveryHour;

    /**
     * Mapa odleglosci do innych klientow
     */
    private Map<Integer, Double> distances = new HashMap<>();

    /**
     * Mapa czasu przejazdow do innych klientow
     */
    private Map<Integer, Duration> durations = new HashMap<>();

    /**
     * Lista odcinkow wychodzacych od klienta
     */
    private List<RouteSegment> routeSegmentsFromCustomer = new ArrayList<>();

    /**
     * Obraz mapy z wyroznionym znacznikiem klienta
     */
    private ImageIcon imageIcon;

    /**
     * Prawdopodobienstwo wyboru klienta w algorytmie mrowkowym
     */
    private double acsChoiceProbability;

    /**
     * Tworzy obiekt klienta na podstawie rzeczywistych danych pobranych z pliku tekstowego
     * @param fullAddress Pelny adres
     * @param streetAndNumber Nazwa ulicy i numer domu
     * @param postalCode Kod pocztowy
     * @param city Miasto
     * @param latitude Szerokosc geograficzna adresu
     * @param longitude Dlugosc geograficzna adresu
     * @param packageWeight Masa zamowionej przesylki
     * @param packageSize Objetosc zamowionej przesylki
     * @param minDeliveryHour Najwczesniejsza mozliwa godzina odbioru przesylki
     * @param maxDeliveryHour Najpozniejsza mozliwa godzina odbioru przesylki
     */
    public Customer(String fullAddress, String streetAndNumber, String postalCode, String city, double latitude, double longitude,
                    double packageWeight, double packageSize, LocalTime minDeliveryHour, LocalTime maxDeliveryHour) {
        this.id = customerID;
        this.mapId = customerMapID;
        customerID++;
        customerMapID++;
        this.fullAddress = fullAddress;
        this.streetAndNumber = streetAndNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.packageWeight = packageWeight;
        this.packageSize = packageSize;
        this.minDeliveryHour = minDeliveryHour;
        this.maxDeliveryHour = maxDeliveryHour;
        this.imageIcon = null;
    }

    /**
     * Tworzy obiekt klienta na podstawie danych testowych
     * @param x Polozenie klienta na osi X
     * @param y Polozenie klienta na osi Y
     * @param demand Masa zamowionej przesylki
     */
    public Customer(double x, double y, double demand) {
        this.id = customerID;
        customerID++;
        this.fullAddress = "";
        this.streetAndNumber = "";
        this.postalCode = "";
        this.city = "";
        this.latitude = x;
        this.longitude = y;
        this.packageWeight = demand;
        this.packageSize = 0;
        this.minDeliveryHour = LocalTime.of(8, 0);
        this.maxDeliveryHour = LocalTime.of(18, 0);
        this.imageIcon = null;
    }

    public static int getCustomerID() {
        return customerID;
    }

    public static void setCustomerID(int customerID) {
        Customer.customerID = customerID;
    }

    public static int getCustomerMapID() {
        return customerMapID;
    }

    public static void setCustomerMapID(int customerMapID) {
        Customer.customerMapID = customerMapID;
    }

    static Duration getServiceTime() {
        return serviceTime;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<Integer, Double> getDistances() {
        return distances;
    }

    public void setDistances(Map<Integer, Double> distances) {
        this.distances = distances;
    }

    public Map<Integer, Duration> getDurations() {
        return durations;
    }

    public void setDurations(Map<Integer, Duration> durations) {
        this.durations = durations;
    }

    public List<RouteSegment> getRouteSegmentsFromCustomer() {
        return routeSegmentsFromCustomer;
    }

    public void setRouteSegmentsFromCustomer(List<RouteSegment> routeSegmentsFromCustomer) {
        this.routeSegmentsFromCustomer = routeSegmentsFromCustomer;
    }

    public double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public double getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(double packageSize) {
        this.packageSize = packageSize;
    }

    public LocalTime getMinDeliveryHour() {
        return minDeliveryHour;
    }

    public void setMinDeliveryHour(LocalTime minDeliveryHour) {
        this.minDeliveryHour = minDeliveryHour;
    }

    public LocalTime getMaxDeliveryHour() {
        return maxDeliveryHour;
    }

    public void setMaxDeliveryHour(LocalTime maxDeliveryHour) {
        this.maxDeliveryHour = maxDeliveryHour;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public double getAcsChoiceProbability() {
        return acsChoiceProbability;
    }

    public void setAcsChoiceProbability(double acsChoiceProbability) {
        this.acsChoiceProbability = acsChoiceProbability;
    }
}
