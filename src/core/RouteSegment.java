package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Klasa reprezentujaca pojedynczy odcinek trasy
 */
public class RouteSegment implements Cloneable {

    /**
     * Logger klasy
     */
    private static final Logger logger = LogManager.getLogger(RouteSegment.class);

    /**
     * Numer ID
     */
    private static int routeSegmentID = 1;

    /**
     * Numer ID odcinka
     */
    private int id;

    /**
     * Klient poczatkowy
     */
    private Customer src;

    /**
     * Klient docelowy
     */
    private Customer dst;

    /**
     * Godzina odjazdu od klienta poczatkowego
     */
    private LocalTime departure = LocalTime.MIDNIGHT;

    /**
     * Godzina przyjazdu do klienta docelowego
     */
    private LocalTime arrival = LocalTime.MIDNIGHT;

    /**
     * Dlugosc odcinka w kilometrach
     */
    private double distance;

    /**
     * Czas przejazdu odcinka
     */
    private Duration duration;

    /**
     * Oszczednosc obliczona dla odcinka w alogrytmie Clarka i Wrighta
     */
    private double clarkWrightSaving;

    /**
     * Ksztalt odcinka w postaci kolejnych wspolrzednych zakodowanych kodem ASCII
     */
    private String geometry;

    /**
     * Obraz mapy z narysowanym na niej odcinkiem trasy
     */
    private ImageIcon imageIcon;

    /**
     * Tworzy odcinek trasy
     * @param src Klient poczatkowy
     * @param dst Klient docelowy
     * @param distance Dlugosc odcinka
     * @param duration Czas przejazdu odcinka
     * @param geometry Ksztalt odcinka w postaci kolejnych wspolrzednych zakodowanych kodem ASCII
     */
    public RouteSegment(Customer src, Customer dst, double distance, Duration duration, String geometry) {
        this.id = routeSegmentID;
        routeSegmentID++;
        this.src = src;
        this.dst = dst;
        this.distance = distance;
        this.duration = duration;
        this.geometry = geometry;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getSrc() {
        return src;
    }

    public void setSrc(Customer src) {
        this.src = src;
    }

    public Customer getDst() {
        return dst;
    }

    public void setDst(Customer dst) {
        this.dst = dst;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public LocalTime getDeparture() {
        return departure;
    }

    void setDeparture(LocalTime departure) {
        this.departure = departure;
    }

    public LocalTime getArrival() {
        return arrival;
    }

    void setArrival(LocalTime arrival) {
        this.arrival = arrival;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public double getClarkWrightSaving() {
        return clarkWrightSaving;
    }

    public void setClarkWrightSaving(double clarkWrightSaving) {
        this.clarkWrightSaving = clarkWrightSaving;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    /**
     * Zaokroagla liczbe double do jednego miejsca po przecinku
     * @param x Liczba do zaokraglenia
     * @return Zwraca zaokraglana liczbe
     */
    private double round(double x) {
        return new BigDecimal(x).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * Wypisuje informacje o odcinku trasy
     * @return Zwraca opis odcinka trasy
     */
    @Override
    public String toString() {
        long minutes = duration.toMinutes() % 60;
        String sMinutes;
        sMinutes = minutes < 10 ? "0" + Long.toString(minutes) : Long.toString(minutes);
        return src.getId() + "-" + dst.getId()
                + "(" + src.getCity().toUpperCase() + "-" + dst.getCity().toUpperCase() + ")"
                + ", " + round(distance)
                + "km, " + duration.toHours() + ":" + sMinutes + "h";
    }

    /**
     * Klonuje odcinek trasy
     * @return Zwraca sklonowany odcinek trasy
     */
    @Override
    public RouteSegment clone() {
        RouteSegment clone = null;
        try {
            clone = (RouteSegment) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("Error while cloning route segment!", e);
        }
        return clone;

    }
}
